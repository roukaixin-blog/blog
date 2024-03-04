package com.roukaixin.handler;

import com.roukaixin.pojo.R;
import com.roukaixin.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 登陆成功时处理器
 *
 * @author 不北咪
 * @date 2023/7/25 下午8:40
 */
@Component
public class FormLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        // 告诉 servlet 用 utf-8 转码
        response.setCharacterEncoding("UTF-8");
        // 告诉浏览器使用 utf-8 解析数据
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(JsonUtils.toJsonString(R.success("登录成功",null)));
    }
}
