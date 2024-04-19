package com.roukaixin.security.authorization.utils;

import com.roukaixin.security.pojo.ProviderDetails;
import com.roukaixin.security.pojo.UserInfoEndpoint;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

/**
 * OAuth2 工具类
 *
 * @author 不北咪
 * @date 2024/4/19 下午2:20
 */
public class OAuth2Utils {

    private OAuth2Utils() {

    }

    public static ClientRegistration localClientRegistrationConvertSecurityClientRegistration(
            com.roukaixin.security.pojo.ClientRegistration clientRegistration,
            ProviderDetails providerDetails,
            UserInfoEndpoint userInfoEndpoint
    ) {
        return ClientRegistration.withRegistrationId(clientRegistration.getRegistrationId())
                .clientId(clientRegistration.getClientId())
                .clientSecret(clientRegistration.getClientSecret())
                .clientAuthenticationMethod(
                        new ClientAuthenticationMethod(clientRegistration.getClientAuthenticationMethod())
                )
                .authorizationGrantType(
                        new AuthorizationGrantType(clientRegistration.getAuthorizationGrantType())
                )
                .redirectUri(clientRegistration.getRedirectUri())
                .scope(clientRegistration.getScopes())
                .authorizationUri(providerDetails.getAuthorizationUri())
                .tokenUri(providerDetails.getTokenUri())
                .userInfoUri(userInfoEndpoint.getUri())
                .userInfoAuthenticationMethod(
                        new AuthenticationMethod(userInfoEndpoint.getAuthenticationMethod().getValue())
                )
                .userNameAttributeName(userInfoEndpoint.getUserNameAttributeName())
                .jwkSetUri(providerDetails.getJwkSetUri())
                .issuerUri(providerDetails.getIssuerUri())
                .providerConfigurationMetadata(providerDetails.getConfigurationMetadata())
                .clientName(clientRegistration.getClientName())
                .build();
    }
}
