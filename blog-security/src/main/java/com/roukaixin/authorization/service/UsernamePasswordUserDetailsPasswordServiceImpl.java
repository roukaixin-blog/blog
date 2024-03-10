package com.roukaixin.authorization.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.mapper.UserMapper;
import com.roukaixin.pojo.User;
import jakarta.annotation.Resource;
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

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        User newUser = (User) user;
        userMapper.update(Wrappers.<User>lambdaUpdate().
                set(User::getPassword, newPassword).eq(User::getUsername, user.getUsername()));
        newUser.setPassword(newPassword);
        return newUser;
    }
}
