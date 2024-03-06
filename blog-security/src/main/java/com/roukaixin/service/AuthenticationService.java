package com.roukaixin.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 认证
 *
 * @author 不北咪
 * @date 2024/3/4 下午8:50
 */

public interface AuthenticationService {

    /**
     * 跳转到第三方登陆页面
     *
     * @param registrationId 客户端id
     * @param redirect 前端需要重定向的前端路径
     */
    void oauth2RequestRedirect(String registrationId, String redirect, HttpServletRequest request, HttpServletResponse response);
}
