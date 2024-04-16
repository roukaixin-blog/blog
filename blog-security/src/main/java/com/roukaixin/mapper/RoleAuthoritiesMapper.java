package com.roukaixin.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roukaixin.pojo.RoleAuthorities;
import org.apache.ibatis.annotations.Mapper;

/**
* @author pankx
* @description 针对表【sys_role_authorities(角色和权限关联表)】的数据库操作Mapper
* @createDate 2024-04-16 15:46:14
* @Entity generator.domain.RoleAuthorities
*/
@Mapper
public interface RoleAuthoritiesMapper extends BaseMapper<RoleAuthorities> {

}




