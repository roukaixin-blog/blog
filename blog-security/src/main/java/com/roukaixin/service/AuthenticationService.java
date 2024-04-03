package com.roukaixin.service;

import com.roukaixin.pojo.R;
import com.roukaixin.pojo.dto.UserDTO;
import com.roukaixin.pojo.vo.LoginSuccessVO;
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
     * 登陆
     * @param user 账号/密码
     * @return R
     */
    R<LoginSuccessVO> login(UserDTO user);

    /**
     * 跳转到第三方登陆页面
     *
     * @param registrationId 客户端id
     * @param redirect 前端需要重定向的前端路径
     * @param request 请求
     * @param response  响应
     */
    void oauth2RequestRedirect(String registrationId, String redirect, HttpServletRequest request, HttpServletResponse response);

    /**
     * 回调地址
     * @param registrationId 客户端id
     * @param request 请求
     * @param response 响应
     */
    void loginOauth2Code(String registrationId, HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取 OAuth2 令牌
     * @param registrationId 客户端id
     * @param state state
     * @return R<LoginSuccessVO>
     */
    R<LoginSuccessVO> oAuth2Token(String registrationId, String state);
}
