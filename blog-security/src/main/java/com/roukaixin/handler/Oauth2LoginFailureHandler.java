package com.roukaixin.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * oauth2 登录失败处理器
 * @author pankx
 * @date 2023/8/28 22:13
 */
@Component
public class Oauth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        // 告诉 servlet 用 utf-8 转码
        response.setCharacterEncoding("UTF-8");
        // 告诉浏览器使用 utf-8 解析数据
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("登录失败");
    }
}
