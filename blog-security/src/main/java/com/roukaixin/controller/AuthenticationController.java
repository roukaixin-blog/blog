package com.roukaixin.controller;

import com.roukaixin.annotation.NoPermitLogin;
import com.roukaixin.pojo.R;
import com.roukaixin.pojo.dto.UserDTO;
import com.roukaixin.pojo.vo.LoginSuccessVO;
import com.roukaixin.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author 不北咪
 * @date 2024/1/8 下午7:35
 */
@RestController
@RequestMapping("/authentication")
@Tag(name = "认证管理模块")
public class AuthenticationController {



    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    @NoPermitLogin
    @Operation(summary = "表单登陆")
    public R<LoginSuccessVO> login(@RequestBody UserDTO user) {
        return authenticationService.login(user);
    }

    @GetMapping("/oauth2/authorization/{registrationId}")
    @NoPermitLogin
    public void oauth2RequestRedirect(@PathVariable("registrationId") String registrationId,
                                      @RequestParam(value = "redirect", required = false) String redirect,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        authenticationService.oauth2RequestRedirect(registrationId, redirect, request, response);
    }

    @GetMapping("/login/oauth2/code/{registrationId}")
    @NoPermitLogin
    public void loginOauth2Code(@PathVariable String registrationId,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        authenticationService.loginOauth2Code(registrationId, request, response);
    }

}
