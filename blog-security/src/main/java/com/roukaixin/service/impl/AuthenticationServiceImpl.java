package com.roukaixin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.authorization.resolver.CustomizeOAuth2AuthorizationRequestResolver;
import com.roukaixin.authorization.service.impl.JdbcClientRegistrationRepository;
import com.roukaixin.mapper.ClientRegistrationMapper;
import com.roukaixin.pojo.R;
import com.roukaixin.pojo.User;
import com.roukaixin.pojo.dto.UserDTO;
import com.roukaixin.pojo.vo.LoginSuccessVO;
import com.roukaixin.service.AuthenticationService;
import com.roukaixin.utils.AesUtils;
import com.roukaixin.utils.JsonUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.roukaixin.constant.LoginConstant.*;
import static com.roukaixin.constant.RedisConstant.COLON;

/**
 * 认证管理器
 *
 * @author 不北咪
 * @date 2024/3/4 下午10:31
 */
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RedirectStrategy authorizationRedirectStrategy = new DefaultRedirectStrategy();

    @Resource
    private JdbcClientRegistrationRepository jdbcClientRegistrationRepository;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private ClientRegistrationMapper clientRegistrationMapper;


    @Override
    public R<LoginSuccessVO> login(UserDTO user) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(token);
        // 认证成功，生产 token 并保存到 redis。getPrincipal: 用户主体信息
        User loginUser = (User) authenticate.getPrincipal();
        log.info("账号密码认证后的用户信息{}", loginUser);
        redisTemplate.opsForValue().set(USER_INFO + SYSTEM+ loginUser.getId(), loginUser);
        long issuedAt = System.currentTimeMillis();
        long expiresAt = issuedAt + EXPIRES_TIME;
        String accessToken = AesUtils.encrypt(
                AES_KEY_ACCESS_TOKEN,
                SYSTEM + loginUser.getId() + COLON + issuedAt + COLON + expiresAt);
        String refreshToken = AesUtils.encrypt(
                AES_KEY_REFRESH_TOKEN,
                SYSTEM + loginUser.getId() + COLON + issuedAt + COLON + (issuedAt + EXPIRES_TIME * 60));
        redisTemplate.opsForValue().set(USER_ACCESS_TOKEN + SYSTEM + loginUser.getId(),
                accessToken, EXPIRES_TIME, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(USER_REFRESH_TOKEN + SYSTEM + loginUser.getId(),
                refreshToken, EXPIRES_TIME * 60, TimeUnit.MILLISECONDS);
        LoginSuccessVO vo = LoginSuccessVO
                .builder()
                .tokenType(TOKEN_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();
        return R.success("登录成功", vo);
    }

    @Override
    public void oauth2RequestRedirect(String registrationId, String redirect,
                                      HttpServletRequest request, HttpServletResponse response) {
        CustomizeOAuth2AuthorizationRequestResolver resolver =
                new CustomizeOAuth2AuthorizationRequestResolver(jdbcClientRegistrationRepository, registrationId);
        OAuth2AuthorizationRequest authorizationRequest = resolver.resolve(request);
        try {
            sendRedirectForAuthorization(request, response, authorizationRequest, registrationId);
        } catch (IOException e) {
            log.error("发送请求重定向失败", e);
        }

    }

    /**
     * 发生认证请求重定向
     *
     * @param request              request
     * @param response             response
     * @param authorizationRequest OAuth2授权请求
     * @param registrationId       客户端唯一标识
     * @throws IOException IOException
     */
    private void sendRedirectForAuthorization(HttpServletRequest request,
                                              HttpServletResponse response,
                                              OAuth2AuthorizationRequest authorizationRequest,
                                              String registrationId) throws IOException {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(authorizationRequest.getGrantType())) {
            String state = authorizationRequest.getState();
            Assert.hasText(state, "authorizationRequest.state cannot be empty");
            saveAuthorizationRequest(authorizationRequest, request, response, registrationId);
        }
        this.authorizationRedirectStrategy
                .sendRedirect(request, response, authorizationRequest.getAuthorizationRequestUri());
    }

    /**
     * 保存认证请求
     *
     * @param authorizationRequest OAuth2授权请求
     * @param request              request
     * @param response             response
     * @param registrationId       客户端唯一标识
     */
    private void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                          HttpServletRequest request,
                                          HttpServletResponse response, String registrationId) {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");
        // 保存 OAuth2AuthorizationRequest，回调之后会使用到 `OAuth2AuthorizationRequest`
        com.roukaixin.authorization.endpoint.OAuth2AuthorizationRequest build =
                com.roukaixin.authorization.endpoint.OAuth2AuthorizationRequest
                        .builder()
                        .authorizationUri(authorizationRequest.getAuthorizationUri())
                        .authorizationGrantType(authorizationRequest.getGrantType().getValue())
                        .responseType(authorizationRequest.getResponseType().getValue())
                        .clientId(authorizationRequest.getClientId())
                        .redirectUri(authorizationRequest.getRedirectUri())
                        .scopes(authorizationRequest.getScopes())
                        .state(authorizationRequest.getState())
                        .additionalParameters(authorizationRequest.getAdditionalParameters())
                        .authorizationRequestUri(authorizationRequest.getAuthorizationRequestUri())
                        .attributes(authorizationRequest.getAttributes())
                        .build();
        // 保存到 redis 中，key：state
        redisTemplate.opsForValue().set(
                registrationId.toLowerCase() + COLON + STATE + authorizationRequest.getState(),
                build,
                3,
                TimeUnit.MINUTES);
    }

    @Override
    public void loginOauth2Code(String registrationId, HttpServletRequest request, HttpServletResponse response) {
        // 获取请求参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> params = toMultiMap(parameterMap);
        // 一定有 state 参数
        String state = params.getFirst(OAuth2ParameterNames.STATE);
        if (!StringUtils.hasText(state)) {
            log.info("回调地址中不包含 state，参数信息：{}", JsonUtils.toJsonString(params));
            throw new RuntimeException("错误请求");
        }
        // 判断是否为认证响应,参数中有 code 和 state 或 state 和 error 都是授权响应
        if (!isAuthorizationResponse(params)) {
            // 不是认证响应
            throw new RuntimeException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST).toString());
        }
        // 获取 OAuth2AuthorizationRequest，从 redis 中获取
        com.roukaixin.authorization.endpoint.OAuth2AuthorizationRequest authorizationRequest =
                (com.roukaixin.authorization.endpoint.OAuth2AuthorizationRequest) redisTemplate.opsForValue()
                        .get(
                                registrationId.toLowerCase() + COLON + STATE + state
                        );
        if (authorizationRequest == null) {
            // 获取不到 redis 的 AuthorizationRequest
            OAuth2Error oauth2Error = new OAuth2Error("authorization_request_not_found");
            throw new RuntimeException(oauth2Error.toString());
        }
        // 可以获取到 AuthorizationRequest，所以把 redis 中 AuthorizationRequest 的删除掉，表示一个 state 只能用一次
        redisTemplate.delete(registrationId.toLowerCase() + COLON + STATE + state);
        // 构建 OAuth2AuthorizationExchange 中的 OAuth2AuthorizationRequest 信息
        OAuth2AuthorizationRequest.Builder oAuth2AuthorizationRequestBuilder = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri(authorizationRequest.getAuthorizationUri())
                .clientId(authorizationRequest.getClientId())
                .redirectUri(authorizationRequest.getRedirectUri())
                .scopes(authorizationRequest.getScopes())
                .state(authorizationRequest.getState())
                .additionalParameters(authorizationRequest.getAdditionalParameters())
                .authorizationRequestUri(authorizationRequest.getAuthorizationRequestUri())
                .attributes(authorizationRequest.getAttributes());
        // 构建 OAuth2AuthorizationExchange 中的 ClientRegistration
        ClientRegistration clientRegistration = jdbcClientRegistrationRepository.findByRegistrationId(registrationId);
        // 格式化 redirectUri 并校验是否是 uri 请求
        String redirectUri = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replaceQuery(null)
                .build()
                .toUriString();
        // 根据请求参数和 oauth2 的从定向 uri 转化成 OAuth2AuthorizationExchange 中的 OAuth2AuthorizationResponse
        OAuth2AuthorizationResponse authorizationResponse = convert(params, redirectUri);
        // 构建未认证 Authentication。
        OAuth2LoginAuthenticationToken authenticationRequest = new OAuth2LoginAuthenticationToken(clientRegistration,
                new OAuth2AuthorizationExchange(oAuth2AuthorizationRequestBuilder.build(), authorizationResponse));
        // 调用认证管理器进行认证，返回经过认证的 Authentication。
        OAuth2LoginAuthenticationToken authenticationResult = (OAuth2LoginAuthenticationToken) authenticationManager
                .authenticate(authenticationRequest);
        // 保存用户信息(OAuth2User -> DefaultOAuth2User)到 redis
        redisTemplate.opsForValue().set(
                USER_INFO + registrationId.toLowerCase() + COLON + authenticationResult.getName(),
                authenticationResult.getPrincipal()
        );
        log.info("oauth2 认证后的用户信息:{}", JSON.toJSONString(authenticationResult.getPrincipal()));
        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authenticationResult);
        log.info("认证信息:{}", JSON.toJSON(authorizedClient));
        // 重定向到前端页面
        try {
            redisTemplate.opsForValue().set(
                    registrationId.toLowerCase() + COLON + NAME + state,
                    authenticationResult.getName(),
                    3,
                    TimeUnit.MINUTES
            );
            // 获取前端重定向 uri
            com.roukaixin.pojo.ClientRegistration selectOne = clientRegistrationMapper.selectOne(
                    Wrappers.<com.roukaixin.pojo.ClientRegistration>lambdaQuery()
                            .eq(com.roukaixin.pojo.ClientRegistration::getRegistrationId, registrationId)
            );
            response.sendRedirect("https://baidu.com?redirect=" +
                    selectOne.getRedirect() + "&state=" + state + "&registrationId=" + selectOne.getRegistrationId());
        } catch (IOException e) {
            log.error("重定向到前端页面失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 参数转换成 MultiValueMap
     * @param parameterMap 参数 map
     * @return MultiValueMap
     */
    private MultiValueMap<String, String> toMultiMap(Map<String, String[]> parameterMap) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        parameterMap.forEach((k, v) -> {
            for (String value : v) {
                params.add(k, value);
            }
        });
        return params;
    }

    /**
     * 获取授权客户端
     * @param authenticationResult Authentication(认证之后的)
     * @return OAuth2AuthorizedClient
     */
    private static OAuth2AuthorizedClient getAuthorizedClient(OAuth2LoginAuthenticationToken authenticationResult) {
        OAuth2AuthenticationToken oauth2Authentication = new OAuth2AuthenticationToken(
                authenticationResult.getPrincipal(),
                authenticationResult.getAuthorities(),
                authenticationResult.getClientRegistration().getRegistrationId()
        );
        return new OAuth2AuthorizedClient(
                authenticationResult.getClientRegistration(),
                oauth2Authentication.getName(),
                authenticationResult.getAccessToken(),
                authenticationResult.getRefreshToken()
        );
    }

    /**
     * 转化 OAuth2AuthorizationResponse
     * @param request request
     * @param redirectUri redirectUri
     * @return OAuth2AuthorizationResponse
     */
    private OAuth2AuthorizationResponse convert(MultiValueMap<String, String> request, String redirectUri) {
        String code = request.getFirst(OAuth2ParameterNames.CODE);
        String errorCode = request.getFirst(OAuth2ParameterNames.ERROR);
        String state = request.getFirst(OAuth2ParameterNames.STATE);
        if (StringUtils.hasText(code)) {
            return OAuth2AuthorizationResponse.success(code).redirectUri(redirectUri).state(state).build();
        }
        String errorDescription = request.getFirst(OAuth2ParameterNames.ERROR_DESCRIPTION);
        String errorUri = request.getFirst(OAuth2ParameterNames.ERROR_URI);
        // @formatter:off
        return OAuth2AuthorizationResponse.error(errorCode)
                .redirectUri(redirectUri)
                .errorDescription(errorDescription)
                .errorUri(errorUri)
                .state(state)
                .build();
        // @formatter:on
    }

    /**
     * 是否是认证响应
     * @param params 参数
     * @return boolean
     */
    private boolean isAuthorizationResponse(MultiValueMap<String, String> params) {
        return isAuthorizationResponseSuccess(params) || isAuthorizationResponseError(params);
    }

    /**
     * 是否认证成功响应
     * @param params 参数
     * @return boolean
     */
    private boolean isAuthorizationResponseSuccess(MultiValueMap<String, String> params) {
        return StringUtils.hasText(params.getFirst(OAuth2ParameterNames.CODE))
                && StringUtils.hasText(params.getFirst(OAuth2ParameterNames.STATE));
    }

    /**
     * 是否认证失败响应
     * @param params 参数
     * @return boolean
     */
    private boolean isAuthorizationResponseError(MultiValueMap<String, String> params) {
        return StringUtils.hasText(params.getFirst(OAuth2ParameterNames.ERROR))
                && StringUtils.hasText(params.getFirst(OAuth2ParameterNames.STATE));
    }

    @Override
    public R<LoginSuccessVO> oAuth2Token(String registrationId, String state) {
        Object name = redisTemplate.opsForValue().get(registrationId.toLowerCase() + COLON + NAME + state);
        if (ObjectUtils.isEmpty(name)) {
            throw new RuntimeException("未进行 oauth2 登录，无法获取 oauth2 令牌");
        }
        OAuth2User oAuth2User = (OAuth2User) redisTemplate.opsForValue().get(
                USER_INFO + registrationId.toLowerCase() + COLON + name);
        if (oAuth2User == null) {
            throw new RuntimeException("当前用户未登录");
        }
        // 颁发自己系统的 token
        long issuedAt = System.currentTimeMillis();
        long expiresAt = issuedAt + EXPIRES_TIME;
        // 访问令牌
        String accessToken = AesUtils.encrypt(
                AES_KEY_ACCESS_TOKEN,
                registrationId.toLowerCase() + COLON + oAuth2User.getName() +
                        COLON + issuedAt + COLON + expiresAt);
        // 刷新令牌
        String refreshToken = AesUtils.encrypt(
                AES_KEY_REFRESH_TOKEN,
                registrationId.toLowerCase() + COLON + oAuth2User.getName() +
                        COLON + issuedAt + COLON + (issuedAt + EXPIRES_TIME * 60));
        // 保存访问令牌和刷新令牌到 redis
        redisTemplate.opsForValue().set(
                USER_ACCESS_TOKEN + registrationId.toLowerCase() + COLON + oAuth2User.getName(),
                accessToken, EXPIRES_TIME, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(
                USER_REFRESH_TOKEN + registrationId.toLowerCase() + COLON + oAuth2User.getName(),
                refreshToken, EXPIRES_TIME * 60, TimeUnit.MILLISECONDS);
        LoginSuccessVO vo = LoginSuccessVO
                .builder()
                .tokenType(TOKEN_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();
        // 获取成功后删除 state
        redisTemplate.delete(registrationId.toLowerCase() + COLON + NAME + state);
        return R.success("登录成功", vo);
    }

}
