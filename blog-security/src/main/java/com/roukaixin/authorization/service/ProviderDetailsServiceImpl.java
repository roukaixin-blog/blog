package com.roukaixin.authorization.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.mapper.ProviderDetailsMapper;
import com.roukaixin.pojo.ProviderDetails;
import com.roukaixin.service.ProviderDetailsService;
import org.springframework.stereotype.Service;

/**
 * 针对表【provider_details(oauth2 服务商提供的信息)】的数据库操作Service实现
 *
 * @author 不北咪
 * @description 针对表【provider_details(oauth2 服务商提供的信息)】的数据库操作Service实现
 * @createDate 2024-03-05 22:46:39
 */
@Service
public class ProviderDetailsServiceImpl extends ServiceImpl<ProviderDetailsMapper, ProviderDetails>
    implements ProviderDetailsService {

}




