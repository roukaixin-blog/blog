package com.roukaixin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roukaixin.pojo.ClientRegistration;
import org.apache.ibatis.annotations.Mapper;

/**
* @author dell
* @description 针对表【client_registration(客户端注册信息)】的数据库操作Mapper
* @createDate 2024-03-05 11:25:53
*/
@Mapper
public interface ClientRegistrationMapper extends BaseMapper<ClientRegistration> {

}




