package com.roukaixin.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.security.mapper.UserMapper;
import com.roukaixin.security.pojo.User;
import com.roukaixin.security.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 针对表【sys_user(用户表)】的数据库操作Service实现
 *
 * @author 不北咪
 * @description 针对表【sys_user(用户表)】的数据库操作Service实现
 * @date 2024-01-19 11:09:24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




