package com.roukaixin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.mapper.ClientRegistrationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * 从数据库中获取 ClientRegistration
 *
 * @author 不北咪
 * @date 2024/3/5 下午7:33
 */
@Service
public class JdbcClientRegistrationRepository implements ClientRegistrationRepository {

    private final ClientRegistrationMapper clientRegistrationMapper;

    @Autowired
    public JdbcClientRegistrationRepository(ClientRegistrationMapper clientRegistrationMapper) {
        this.clientRegistrationMapper = clientRegistrationMapper;
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        com.roukaixin.pojo.ClientRegistration clientRegistration = clientRegistrationMapper.selectOne(
                Wrappers.<com.roukaixin.pojo.ClientRegistration>lambdaQuery()
                        .eq(com.roukaixin.pojo.ClientRegistration::getRegistrationId, registrationId)
        );
        if (ObjectUtils.isEmpty(clientRegistration)) {
            throw new RuntimeException("不支持当前第三方登录");
        }
        return ClientRegistration.withRegistrationId(registrationId)
                .clientId(clientRegistration.getClientId())
                .clientSecret(clientRegistration.getClientSecret())
                .clientAuthenticationMethod(
                        new ClientAuthenticationMethod(clientRegistration.getClientAuthenticationMethod()))
                .authorizationGrantType(new AuthorizationGrantType(clientRegistration.getAuthorizationGrantType()))
                .redirectUri(clientRegistration.getRedirectUri())
                .scope(clientRegistration.getScopes())

                .clientName(clientRegistration.getClientName())
                .build();
    }
}
