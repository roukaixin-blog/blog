package com.roukaixin.security.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roukaixin.security.pojo.ProviderDetails;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表【client_registration(客户端注册信息)】的数据库操作Mapper
 *
 * @author 不北咪
 * @description 针对表【provider_details(oauth2 服务商提供的信息)】的数据库操作Mapper
 * @createDate 2024-03-05 22:46:39
 */
@Mapper
public interface ProviderDetailsMapper extends BaseMapper<ProviderDetails> {

}




