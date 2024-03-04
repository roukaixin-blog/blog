package com.roukaixin.service;

/**
 * 认证
 *
 * @author 不北咪
 * @date 2024/3/4 下午8:50
 */

public interface AuthenticationService {

    /**
     * 跳转到第三方登陆页面
     * @param registrationId 客户端id
     */
    void oauth2RequestRedirect(String registrationId);
}
