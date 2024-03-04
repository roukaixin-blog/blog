package com.roukaixin.handler;

import com.roukaixin.pojo.R;
import com.roukaixin.utils.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * oauth2 登录成功处理器
 * @author 不北咪
 * @date 2023/8/28 22:13
 */
@Component
public class Oauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        // 告诉 servlet 用 utf-8 转码
        response.setCharacterEncoding("UTF-8");
        // 告诉浏览器使用 utf-8 解析数据
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(JsonUtils.toJsonString(R.success("登录成功")));
    }
}
