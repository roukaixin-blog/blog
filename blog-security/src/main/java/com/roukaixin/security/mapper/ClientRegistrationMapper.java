package com.roukaixin.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roukaixin.security.pojo.ClientRegistration;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表【client_registration(客户端注册信息)】的数据库操作Mapper
 *
 * @author 不北咪
 * @description 针对表【client_registration(客户端注册信息)】的数据库操作Mapper
 * @createDate 2024-03-05 11:25:53
 */
@Mapper
public interface ClientRegistrationMapper extends BaseMapper<ClientRegistration> {

}




