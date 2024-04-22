package com.roukaixin.security.authorization.filter;


import com.roukaixin.common.pojo.R;
import com.roukaixin.common.utils.AesUtils;
import com.roukaixin.common.utils.JsonUtils;
import com.roukaixin.security.authorization.registration.JdbcClientRegistrationRepository;
import com.roukaixin.security.constant.LoginConstant;
import com.roukaixin.security.constant.RedisConstant;
import com.roukaixin.security.pojo.OAuth2User;
import com.roukaixin.security.pojo.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * 校验 token 是否存在、合法
 *
 * @author 不北咪
 * @date 2024/4/3 下午10:32
 */
@Slf4j
public class AuthenticationFiler extends OncePerRequestFilter {

    private final static String AUTHORIZATION = "Authorization";

    private final static int LENGTH = 2;

    private final RedisTemplate<String, Object> redisTemplate;

    private final JdbcClientRegistrationRepository jdbcClientRegistrationRepository;

    public AuthenticationFiler(RedisTemplate<String, Object> redisTemplate,
                               JdbcClientRegistrationRepository jdbcClientRegistrationRepository) {
        this.redisTemplate = redisTemplate;
        this.jdbcClientRegistrationRepository = jdbcClientRegistrationRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(AUTHORIZATION);
        long currentTime = System.currentTimeMillis();
        if (StringUtils.hasText(authorization)) {
            String[] authorizationSplit = authorization.split(" ");
            if (authorizationSplit.length == LENGTH) {
                String accessToken = authorizationSplit[1];
                String encrypt = AesUtils.decrypt(LoginConstant.AES_KEY_ACCESS_TOKEN, accessToken);
                if (StringUtils.hasText(encrypt)) {
                    // 解密成功
                    String[] tokenSplit = encrypt.split(RedisConstant.COLON);
                    // 判断是否过期
                    String expiresAt = tokenSplit[tokenSplit.length - 2];
                    if (currentTime > Long.parseLong(expiresAt)) {
                        // token 没有过期
                        // 登录类型。system、oauth2
                        String loginType = tokenSplit[0];
                        setAuthenticationToken(loginType, tokenSplit, accessToken, request, response, filterChain);
                    } else {
                        // token 过期
                        sendResponse(441, "访问令牌过期", response);
                    }
                } else {
                    // token 无法解密，不是本系统的 token
                    sendResponse(442, "不是有效的访问令牌，访问令牌不能解析", response);
                }
            } else {
                // 非法 token（伪造或者不是本系统的 token）
                log.error("[AuthenticationFiler] 非法 token => {}", authorization);
                sendResponse(442, "不是有效的访问令牌", response);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * 设置 AuthenticationToken
     *
     * @param loginType   登录类型
     * @param tokenSplit  token 分割
     * @param accessToken 访问令牌
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     * @throws ServletException 异常
     * @throws IOException      io 异常
     */
    private void setAuthenticationToken(String loginType,
                                        String[] tokenSplit,
                                        String accessToken,
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
        switch (loginType) {
            case LoginConstant.SYSTEM -> {
                // 获取用户信息
                User user = (User) redisTemplate.opsForValue().get(
                        LoginConstant.LOGIN_USER_INFO + LoginConstant.SYSTEM + RedisConstant.COLON +
                                tokenSplit[1]);
                logUserInfo(JsonUtils.toJsonString(user));
                if (user != null) {
                    // 构建 UsernamePasswordAuthenticationToken
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    user.getUsername(),
                                    user.getPassword(),
                                    user.getAuthorities()
                            );
                    SecurityContextHolder.getContext()
                            .setAuthentication(usernamePasswordAuthenticationToken);
                }
                filterChain.doFilter(request, response);
            }
            case LoginConstant.OAUTH2 -> {
                // 从 redis 获取 oauth2 用户信息。
                OAuth2User oAuth2User = (OAuth2User) redisTemplate.opsForValue().get(
                        LoginConstant.LOGIN_USER_INFO + tokenSplit[1] + RedisConstant.COLON + tokenSplit[2]);
                logUserInfo(JsonUtils.toJsonString(oAuth2User));
                ClientRegistration clientRegistration = jdbcClientRegistrationRepository
                        .findByRegistrationId(tokenSplit[1]);
                if (oAuth2User != null) {
                    // 构建 OAuth2LoginAuthenticationToken
                    OAuth2LoginAuthenticationToken oAuth2LoginAuthenticationToken =
                            new OAuth2LoginAuthenticationToken(
                                    clientRegistration,
                                    new OAuth2AuthorizationExchange(
                                            OAuth2AuthorizationRequest.authorizationCode()
                                                    .attributes((attrs) ->
                                                            attrs.put(
                                                                    OAuth2ParameterNames.REGISTRATION_ID,
                                                                    clientRegistration.getRegistrationId()
                                                            )
                                                    )
                                                    .clientId(clientRegistration.getClientId())
                                                    .authorizationUri(
                                                            clientRegistration
                                                                    .getProviderDetails().getAuthorizationUri()
                                                    )
                                                    .redirectUri(clientRegistration.getRedirectUri())
                                                    .scopes(clientRegistration.getScopes())
                                                    .build(),
                                            OAuth2AuthorizationResponse
                                                    .success(accessToken)
                                                    .redirectUri(clientRegistration.getRedirectUri())
                                                    .build()
                                    ),
                                    oAuth2User,
                                    oAuth2User.getAuthorities(),
                                    new OAuth2AccessToken(
                                            OAuth2AccessToken.TokenType.BEARER,
                                            accessToken,
                                            Instant.ofEpochMilli(Long.parseLong(tokenSplit[tokenSplit.length - 2])),
                                            Instant.ofEpochMilli(Long.parseLong(tokenSplit[tokenSplit.length - 1]))
                                    )
                            );
                    SecurityContextHolder.getContext()
                            .setAuthentication(oAuth2LoginAuthenticationToken);
                }
                filterChain.doFilter(request, response);
            }
            default -> sendResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "不支持其他登录方式", response);
        }
    }

    /**
     * 发生响应
     *
     * @param status   响应状态
     * @param message  响应消息
     * @param response 响应请求
     * @throws IOException 异常
     */
    private void sendResponse(int status, String message, HttpServletResponse response) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(JsonUtils.toJsonString(R.error(status, message)));
        response.getWriter().flush();
    }

    /**
     * 打印用户信息
     *
     * @param userInfo 用户信息
     */
    private void logUserInfo(String userInfo) {
        log.info("[AuthenticationFiler] 用户信息 => {}", userInfo);
    }
}
