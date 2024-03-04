package com.roukaixin.controller;

import com.roukaixin.annotation.NoPermitLogin;
import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 *
 * @author 不北咪
 * @date 2024/1/8 下午7:35
 */
@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @Resource
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    @NoPermitLogin
    public Object login() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("user", "123456");
        Authentication authenticate = authenticationManager.authenticate(token);
        return authenticate.getAuthorities();
    }
}
