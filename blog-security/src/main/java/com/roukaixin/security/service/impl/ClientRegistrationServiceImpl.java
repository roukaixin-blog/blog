package com.roukaixin.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.security.mapper.ClientRegistrationMapper;
import com.roukaixin.security.pojo.ClientRegistration;
import com.roukaixin.security.service.ClientRegistrationService;
import org.springframework.stereotype.Service;

/**
 * 针对表【client_registration(客户端注册信息)】的数据库操作Service实现
 *
 * @author 不北咪
 * @description 针对表【client_registration(客户端注册信息)】的数据库操作Service实现
 * @createDate 2024-03-05 11:25:53
 */
@Service
public class ClientRegistrationServiceImpl extends ServiceImpl<ClientRegistrationMapper, ClientRegistration>
    implements ClientRegistrationService {

}




