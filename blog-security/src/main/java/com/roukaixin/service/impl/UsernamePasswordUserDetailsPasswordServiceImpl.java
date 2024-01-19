package com.roukaixin.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;

/**
 * 用户/密码登陆自动升级密码
 *
 * @author 不北咪
 * @date 2024/1/19 上午10:40
 */
@Service
public class UsernamePasswordUserDetailsPasswordServiceImpl implements UserDetailsPasswordService {

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        return null;
    }
}
