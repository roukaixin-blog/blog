package com.roukaixin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.mapper.ClientRegistrationMapper;
import com.roukaixin.pojo.ClientRegistration;
import com.roukaixin.service.ClientRegistrationService;
import org.springframework.stereotype.Service;

/**
* @author 不北咪
* @description 针对表【client_registration(客户端注册信息)】的数据库操作Service实现
* @createDate 2024-03-05 11:25:53
*/
@Service
public class ClientRegistrationServiceImpl extends ServiceImpl<ClientRegistrationMapper, ClientRegistration>
    implements ClientRegistrationService {

}




