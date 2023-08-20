package com.kai.config;

import com.kai.handler.FormLoginFailureHandler;
import com.kai.handler.FormLoginSuccessHandler;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 自定义 security 配置
 *
 * @author pankx
 * @date 2023/6/30 上午11:16
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    /*
    CREATE TABLE oauth2_authorized_client (
        client_registration_id varchar(100) NOT NULL,
        principal_name varchar(200) NOT NULL,
        access_token_type varchar(100) NOT NULL,
        access_token_value blob NOT NULL,
        access_token_issued_at timestamp NOT NULL,
        access_token_expires_at timestamp NOT NULL,
        access_token_scopes varchar(1000) DEFAULT NULL,
        refresh_token_value blob DEFAULT NULL,
        refresh_token_issued_at timestamp DEFAULT NULL,
        created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
        PRIMARY KEY (client_registration_id, principal_name)
    );
*/

    @Resource
    private FormLoginSuccessHandler formLoginSuccessHandler;

    @Resource
    private FormLoginFailureHandler formLoginFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests.
                                anyRequest().authenticated()
                ).
                formLogin(formLogin ->
                        formLogin.
                                successHandler(formLoginSuccessHandler).
                                failureHandler(formLoginFailureHandler)
                ).
                oauth2Login(oauth2Login -> {

                }).
                oauth2Client(oauth2Client ->
                        oauth2Client.
                                clientRegistrationRepository(clientRegistrationRepository())
                );

        return http.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
    }

    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
                .clientId("google-client-id")
                .clientSecret("google-client-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email", "address", "phone")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .clientName("Google")
                .build();
    }
}
