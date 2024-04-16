package com.roukaixin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.mapper.UserRoleMapper;
import com.roukaixin.pojo.UserRole;
import com.roukaixin.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
* @author pankx
* @description 针对表【sys_user_role(用户权限关联表)】的数据库操作Service实现
* @createDate 2024-04-16 15:20:24
*/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>
    implements UserRoleService {

}




