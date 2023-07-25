package com.kai.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 登陆成功时处理器
 *
 * @author pankx
 * @date 2023/7/25 下午8:40
 */
@Component
public class FormLoginSuccessHandler implements AuthenticationSuccessHandler {
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        // 告诉 servlet 用 utf-8 转码
        response.setCharacterEncoding("UTF-8");
        // 告诉浏览器使用 utf-8 解析数据
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("登录成功");
    }
}
