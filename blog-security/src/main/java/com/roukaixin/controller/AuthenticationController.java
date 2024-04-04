package com.roukaixin.controller;

import com.roukaixin.annotation.NoPermitLogin;
import com.roukaixin.pojo.R;
import com.roukaixin.pojo.dto.UserDTO;
import com.roukaixin.pojo.vo.LoginSuccessVO;
import com.roukaixin.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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

    @PostMapping("/login/username/password")
    @NoPermitLogin
    @Operation(summary = "账号密码登录")
    public R<LoginSuccessVO> loginUsernamePassword(@RequestBody @Validated UserDTO user) {
        return authenticationService.loginUsernamePassword(user);
    }

    @GetMapping("/oauth2/authorization/{registrationId}")
    @NoPermitLogin
    @Operation(summary = "OAuth2 请求认证重定向")
    @Parameters({
            @Parameter(name = "registrationId", description = "客户端标识", required = true, example = "github"),
            @Parameter(name = "redirect", description = "OAuth2 登录成功后重定向地址", example = "https://127.0.0.1:10000")
    })
    public void oauth2RequestRedirect(@PathVariable("registrationId") String registrationId,
                                      @RequestParam(value = "redirect", required = false) String redirect,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        authenticationService.oauth2RequestRedirect(registrationId, redirect, request, response);
    }

    @GetMapping("/login/oauth2/code/{registrationId}")
    @NoPermitLogin
    @Operation(summary = "OAuth2 确认授权之后回调地址")
    @Parameter(name = "registrationId", description = "客户端标识", example = "github", required = true)
    public void loginOauth2Code(@PathVariable String registrationId,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        authenticationService.loginOauth2Code(registrationId, request, response);
    }

    @PostMapping("/oauth2/token/{registrationId}")
    @NoPermitLogin
    @Operation(summary = "获取 OAuth2 令牌")
    @Parameters({
            @Parameter(
                    name = "registrationId", description = "客户端标识", example = "github",
                    required = true, in = ParameterIn.PATH ),
            @Parameter(name = "state", description = "表示当前唯一请求", required = true)
    })
    public R<LoginSuccessVO> oAuth2Token(@PathVariable String registrationId, String state) {
        return authenticationService.oAuth2Token(registrationId, state);
    }

}
