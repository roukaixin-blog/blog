package com.roukaixin.config;

import com.roukaixin.handler.FormLoginFailureHandler;
import com.roukaixin.handler.FormLoginSuccessHandler;
import com.roukaixin.handler.Oauth2LoginFailureHandler;
import com.roukaixin.handler.Oauth2LoginSuccessHandler;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

    @Resource
    private Oauth2LoginSuccessHandler oauth2LoginSuccessHandler;

    @Resource
    private Oauth2LoginFailureHandler oauth2LoginFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return
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
                        oauth2Login(oauth2Login ->
                                oauth2Login.
                                        successHandler(oauth2LoginSuccessHandler).
                                        failureHandler(oauth2LoginFailureHandler)

                        )
                        .build();
    }
}
