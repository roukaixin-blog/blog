package com.roukaixin.handler;

import com.roukaixin.pojo.R;
import com.roukaixin.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 请求保护接口时，未认证处理器
 *
 * @author 不北咪
 * @date 2024/3/26 下午11:25
 */
@Slf4j
public class NotAuthenticationHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("未认证处理器", authException);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter()
                .write(JsonUtils.toJsonString(R.error(HttpStatus.UNAUTHORIZED.value(), "未登录，请先登录")));
    }
}
