package com.roukaixin.security.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roukaixin.security.pojo.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
* @author pankx
* @description 针对表【sys_user_role(用户权限关联表)】的数据库操作Mapper
* @createDate 2024-04-16 15:20:24
* @Entity generator.domain.UserRole
*/
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

}




