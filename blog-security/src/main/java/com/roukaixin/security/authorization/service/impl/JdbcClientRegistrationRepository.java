package com.roukaixin.security.authorization.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.security.mapper.ClientRegistrationMapper;
import com.roukaixin.security.mapper.ProviderDetailsMapper;
import com.roukaixin.security.mapper.UserInfoEndpointMapper;
import com.roukaixin.security.pojo.ProviderDetails;
import com.roukaixin.security.pojo.UserInfoEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthenticationMethod;
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

    private final ProviderDetailsMapper providerDetailsMapper;

    private final UserInfoEndpointMapper userInfoEndpointMapper;

    @Autowired
    public JdbcClientRegistrationRepository(ClientRegistrationMapper clientRegistrationMapper,
                                            ProviderDetailsMapper providerDetailsMapper,
                                            UserInfoEndpointMapper userInfoEndpointMapper) {
        this.clientRegistrationMapper = clientRegistrationMapper;
        this.providerDetailsMapper = providerDetailsMapper;
        this.userInfoEndpointMapper = userInfoEndpointMapper;
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        com.roukaixin.security.pojo.ClientRegistration clientRegistration = clientRegistrationMapper.selectOne(
                Wrappers.<com.roukaixin.security.pojo.ClientRegistration>lambdaQuery()
                        .eq(com.roukaixin.security.pojo.ClientRegistration::getRegistrationId, registrationId)
        );
        if (ObjectUtils.isEmpty(clientRegistration)) {
            throw new RuntimeException("不支持当前第三方登录");
        }
        ProviderDetails providerDetails = providerDetailsMapper.selectOne(
                Wrappers.<ProviderDetails>lambdaQuery().eq(ProviderDetails::getRegistrationId, registrationId));
        UserInfoEndpoint userInfoEndpoint = userInfoEndpointMapper.selectOne(
                Wrappers.<UserInfoEndpoint>lambdaQuery().eq(UserInfoEndpoint::getId, providerDetails.getUserInfoEndpointId()));
        return ClientRegistration.withRegistrationId(registrationId)
                .clientId(clientRegistration.getClientId())
                .clientSecret(clientRegistration.getClientSecret())
                .clientAuthenticationMethod(
                        new ClientAuthenticationMethod(clientRegistration.getClientAuthenticationMethod()))
                .authorizationGrantType(new AuthorizationGrantType(clientRegistration.getAuthorizationGrantType()))
                .redirectUri(clientRegistration.getRedirectUri())
                .scope(clientRegistration.getScopes())
                .authorizationUri(providerDetails.getAuthorizationUri())
                .tokenUri(providerDetails.getTokenUri())
                .userInfoUri(userInfoEndpoint.getUri())
                .userInfoAuthenticationMethod(new AuthenticationMethod(
                        userInfoEndpoint.getAuthenticationMethod().getValue()))
                .userNameAttributeName(userInfoEndpoint.getUserNameAttributeName())
                .jwkSetUri(providerDetails.getJwkSetUri())
                .issuerUri(providerDetails.getIssuerUri())
                .providerConfigurationMetadata(providerDetails.getConfigurationMetadata())
                .clientName(clientRegistration.getClientName())
                .build();
    }
}
