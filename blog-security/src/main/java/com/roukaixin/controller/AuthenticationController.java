package com.roukaixin.controller;

import com.roukaixin.annotation.NoPermitLogin;
import com.roukaixin.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author 不北咪
 * @date 2024/1/8 下午7:35
 */
@RestController
@RequestMapping("/authentication")
public class AuthenticationController {


    private final AuthenticationManager authenticationManager;

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    AuthenticationService authenticationService) {
        this.authenticationManager = authenticationManager;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    @NoPermitLogin
    public Object login() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("admin", "123456");
        Authentication authenticate = authenticationManager.authenticate(token);
        return authenticate.getAuthorities();
    }

    @GetMapping("/oauth2/authorization/{registrationId}")
    @NoPermitLogin
    public void oauth2RequestRedirect(@PathVariable("registrationId") String registrationId, String redirect) {
        authenticationService.oauth2RequestRedirect(registrationId, redirect);
    }

//    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter see(String id) {
//        SseEmitter sseEmitter = new SseEmitter(0L);
//        SSE.put(id, sseEmitter);
//        HashMap<String, String> map = new HashMap<>(4);
//        map.put("sse", id);
//        authenticationService.runShell(sseEmitter,id);
//        return sseEmitter;
//    }
}
