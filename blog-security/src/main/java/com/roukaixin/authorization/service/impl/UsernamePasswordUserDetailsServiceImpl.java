package com.roukaixin.authorization.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.mapper.UserMapper;
import jakarta.annotation.Resource;
import com.roukaixin.pojo.User;
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

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在，请注册！");
        }
        return user;
    }
}
