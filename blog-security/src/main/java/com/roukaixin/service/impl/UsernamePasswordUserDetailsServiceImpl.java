package com.roukaixin.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户名/密码登陆获取账号信息
 *
 * @author 不北咪
 * @date 2024/1/19 上午10:42
 */
@Service
public class UsernamePasswordUserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return User.withUsername("user").password("{noop}123456").roles("USER").build();
    }
}
