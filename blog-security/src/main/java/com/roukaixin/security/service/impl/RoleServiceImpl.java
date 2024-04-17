package com.roukaixin.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.security.mapper.RoleMapper;
import com.roukaixin.security.pojo.Role;
import com.roukaixin.security.service.RoleService;
import org.springframework.stereotype.Service;

/**
* @author pankx
* @description 针对表【sys_role(角色)】的数据库操作Service实现
* @createDate 2024-04-16 15:17:17
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService {

}




