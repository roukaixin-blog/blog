package com.roukaixin.service.impl;

import com.roukaixin.authorization.resolver.CustomizeOAuth2AuthorizationRequestResolver;
import com.roukaixin.service.AuthenticationService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;

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

    @Override
    public void oauth2RequestRedirect(String registrationId, String redirect,
                                      HttpServletRequest request, HttpServletResponse response) {
        CustomizeOAuth2AuthorizationRequestResolver resolver =
                new CustomizeOAuth2AuthorizationRequestResolver(jdbcClientRegistrationRepository, registrationId);
        OAuth2AuthorizationRequest authorizationRequest = resolver.resolve(request);
        try {
            sendRedirectForAuthorization(request, response, authorizationRequest);
        } catch (IOException e) {
            log.error("", e);
        }

    }

    @Override
    public void loginOauth2Code(String registrationId, HttpServletRequest request, HttpServletResponse response) {
        // 参数中有 code 和 state 或 state 和 error 都是授权响应

        // 获取 OAuth2AuthorizationRequest，从 redis 中获取
        com.roukaixin.authorization.endpoint.OAuth2AuthorizationRequest authorizationRequest =
                (com.roukaixin.authorization.endpoint.OAuth2AuthorizationRequest) redisTemplate.opsForValue()
                        .get(request.getSession().getId());


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
        String state = authorizationRequest.getState();
        Assert.hasText(state, "authorizationRequest.state cannot be empty");
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
        redisTemplate.opsForValue().set(request.getSession().getId(), build);
    }

}
