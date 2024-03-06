package com.roukaixin.service.impl;

import com.roukaixin.authorization.resolver.CustomizeOAuth2AuthorizationRequestResolver;
import com.roukaixin.service.AuthenticationService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Service;

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

    @Override
    public void oauth2RequestRedirect(String registrationId, String redirect,
                                      HttpServletRequest request, HttpServletResponse response) {
        CustomizeOAuth2AuthorizationRequestResolver resolver =
                new CustomizeOAuth2AuthorizationRequestResolver(jdbcClientRegistrationRepository, registrationId);
        OAuth2AuthorizationRequest authorizationRequest = resolver.resolve(request);
        try {
            this.authorizationRedirectStrategy
                    .sendRedirect(request, response, authorizationRequest.getAuthorizationRequestUri());
        } catch (IOException e) {
            log.error("", e);
        }

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
