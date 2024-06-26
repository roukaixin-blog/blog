package com.roukaixin.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.security.mapper.RoleAuthoritiesMapper;
import com.roukaixin.security.pojo.RoleAuthorities;
import com.roukaixin.security.service.RoleAuthoritiesService;
import org.springframework.stereotype.Service;

/**
* @author pankx
* @description 针对表【sys_role_authorities(角色和权限关联表)】的数据库操作Service实现
* @createDate 2024-04-16 15:46:14
*/
@Service
public class RoleAuthoritiesServiceImpl extends ServiceImpl<RoleAuthoritiesMapper, RoleAuthorities>
    implements RoleAuthoritiesService {

}




