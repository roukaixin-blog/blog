package com.roukaixin.handler;

import com.roukaixin.pojo.R;
import com.roukaixin.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 登陆失败时处理器
 *
 * @author 不北咪
 * @date 2023/7/25 下午8:42
 */
@Component
public class FormLoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        // 告诉 servlet 用 utf-8 转码
        response.setCharacterEncoding("UTF-8");
        // 告诉浏览器使用 utf-8 解析数据
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(JsonUtils.toJsonString(R.error("登录失败")));
    }
}
