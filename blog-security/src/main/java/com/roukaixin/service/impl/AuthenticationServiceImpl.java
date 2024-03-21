package com.roukaixin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.roukaixin.authorization.resolver.CustomizeOAuth2AuthorizationRequestResolver;
import com.roukaixin.authorization.service.impl.JdbcClientRegistrationRepository;
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
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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


    @Override
    public R<LoginSuccessVO> login(UserDTO user) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(token);
        // 认证成功，生产 token 并保存到 redis
        // getPrincipal: 用户主体信息
        User loginUser = (User) authenticate.getPrincipal();
        log.info("用户信息{}", loginUser);
        redisTemplate.opsForValue().set("login:user:info", loginUser);
        long issuedAt = System.currentTimeMillis();
        String accessToken = AesUtils.encrypt("jdb9H6spaVAoTfwiwDiSCw==".getBytes(StandardCharsets.UTF_8)
                , "system:" + loginUser.getId() + ":" + issuedAt);
        long expiresAt = issuedAt + 30 * 60 * 1000;
        String refreshToken = AesUtils.encrypt("jdb9H6spaVAoTfwiwDiSCw==".getBytes(StandardCharsets.UTF_8)
                , "system:" + loginUser.getId() + ":" + expiresAt);
        LoginSuccessVO vo = LoginSuccessVO
                .builder()
                .tokenType("Bearer")
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
            sendRedirectForAuthorization(request, response, authorizationRequest);
        } catch (IOException e) {
            log.error("发送请求重定向失败", e);
        }

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
                        .get(Objects.requireNonNull(params.getFirst(OAuth2ParameterNames.STATE)));
        if (authorizationRequest == null) {
            OAuth2Error oauth2Error = new OAuth2Error("authorization_request_not_found");
            throw new RuntimeException(oauth2Error.toString());
        }
        OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri(authorizationRequest.getAuthorizationUri())
                .clientId(authorizationRequest.getClientId())
                .redirectUri(authorizationRequest.getRedirectUri())
                .scopes(authorizationRequest.getScopes())
                .state(authorizationRequest.getState())
                .additionalParameters(authorizationRequest.getAdditionalParameters())
                .authorizationRequestUri(authorizationRequest.getAuthorizationRequestUri())
                .attributes(authorizationRequest.getAttributes());
        ClientRegistration clientRegistration = jdbcClientRegistrationRepository.findByRegistrationId(registrationId);
        String redirectUri = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replaceQuery(null)
                .build()
                .toUriString();
        OAuth2AuthorizationResponse authorizationResponse = convert(params, redirectUri);
        // 调用认证管理器
        OAuth2LoginAuthenticationToken authenticationRequest = new OAuth2LoginAuthenticationToken(clientRegistration,
                new OAuth2AuthorizationExchange(builder.build(), authorizationResponse));
        OAuth2LoginAuthenticationToken authenticationResult = (OAuth2LoginAuthenticationToken) authenticationManager
                .authenticate(authenticationRequest);
        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authenticationResult);
        log.info("认证信息:{}", JSON.toJSON(authorizedClient));
    }

    private static OAuth2AuthorizedClient getAuthorizedClient(OAuth2LoginAuthenticationToken authenticationResult) {
        OAuth2AuthenticationToken oauth2Authentication = new OAuth2AuthenticationToken(
                authenticationResult.getPrincipal(), authenticationResult.getAuthorities(),
                authenticationResult.getClientRegistration().getRegistrationId());
        return new OAuth2AuthorizedClient(
                authenticationResult.getClientRegistration(), oauth2Authentication.getName(),
                authenticationResult.getAccessToken(), authenticationResult.getRefreshToken());
    }

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

    private boolean isAuthorizationResponse(MultiValueMap<String, String> params) {
        return isAuthorizationResponseSuccess(params) || isAuthorizationResponseError(params);
    }

    private boolean isAuthorizationResponseSuccess(MultiValueMap<String, String> params) {
        return StringUtils.hasText(params.getFirst(OAuth2ParameterNames.CODE))
                && StringUtils.hasText(params.getFirst(OAuth2ParameterNames.STATE));
    }

    private boolean isAuthorizationResponseError(MultiValueMap<String, String> params) {
        return StringUtils.hasText(params.getFirst(OAuth2ParameterNames.ERROR))
                && StringUtils.hasText(params.getFirst(OAuth2ParameterNames.STATE));
    }

    private MultiValueMap<String, String> toMultiMap(Map<String, String[]> parameterMap) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        parameterMap.forEach((k, v) -> {
            for (String value : v) {
                params.add(k, value);
            }
        });
        return params;
    }

    private void sendRedirectForAuthorization(HttpServletRequest request, HttpServletResponse response,
                                              OAuth2AuthorizationRequest authorizationRequest) throws IOException {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(authorizationRequest.getGrantType())) {
            String state = authorizationRequest.getState();
            Assert.hasText(state, "authorizationRequest.state cannot be empty");
            saveAuthorizationRequest(authorizationRequest, request, response);
        }
        this.authorizationRedirectStrategy
                .sendRedirect(request, response, authorizationRequest.getAuthorizationRequestUri());
    }

    private void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
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
        redisTemplate.opsForValue().set(authorizationRequest.getState(), build, 5, TimeUnit.MINUTES);
    }

}
