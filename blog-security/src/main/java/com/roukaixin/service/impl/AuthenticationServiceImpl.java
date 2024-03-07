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

    private void sendRedirectForAuthorization(HttpServletRequest request, HttpServletResponse response,
                                              OAuth2AuthorizationRequest authorizationRequest) throws IOException {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(authorizationRequest.getGrantType())) {
            saveAuthorizationRequest(authorizationRequest, request, response);
            String state = authorizationRequest.getState();
            Assert.hasText(state, "authorizationRequest.state cannot be empty");
        }
        this.authorizationRedirectStrategy
                .sendRedirect(request, response, authorizationRequest.getAuthorizationRequestUri());
    }

    private void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");
        if (authorizationRequest == null) {
            // 删除 authorizationRequest
            redisTemplate.delete(request.getSession().getId());
            return;
        }
        String state = authorizationRequest.getState();
        Assert.hasText(state, "authorizationRequest.state cannot be empty");
        // 保存 authorizationRequest
        redisTemplate.opsForValue().set(request.getSession().getId(), authorizationRequest);
    }

//    @SneakyThrows
//    @Async
//    public void runShell(SseEmitter sseEmitter, String id) {
//        Runtime runtime = Runtime.getRuntime();
//        Process exec = runtime.exec(new String[]{"/bin/sh", "-c", "echo 1 && sleep 5 && echo 1"});
//        BufferedReader reader = exec.inputReader();
//        String flag;
//        // SseEmitter sseEmitter = AuthenticationController.sse.get(id);
//        while ((flag = reader.readLine()) != null) {
//            sseEmitter.send(flag);
//        }
//        sseEmitter.complete();
//
//    }
}
