package com.roukaixin.security.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roukaixin.security.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表【sys_user(用户表)】的数据库操作Mapper
 *
 * @author 不北咪
 * @description 针对表【sys_user(用户表)】的数据库操作Mapper
 * @createDate 2024-01-19 11:09:24
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




