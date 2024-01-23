package com.roukaixin.config;


import com.roukaixin.properties.PasswordEncoderProperties;
import com.roukaixin.service.impl.UsernamePasswordUserDetailsPasswordServiceImpl;
import com.roukaixin.service.impl.UsernamePasswordUserDetailsServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;

/**
 * 自定义 security 配置
 *
 * @author pankx
 * @date 2023/6/30 上午11:16
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final PasswordEncoderProperties passwordEncoderProperties;

    private final List<AuthenticationProvider> authenticationProviders;

    private final Map<String, PasswordEncoder> encoders;

    @Autowired
    public SecurityConfig(PasswordEncoderProperties passwordEncoderProperties,
                          List<AuthenticationProvider> authenticationProviders,
                          Map<String, PasswordEncoder> encoders) {
        this.passwordEncoderProperties = passwordEncoderProperties;
        this.authenticationProviders = authenticationProviders;
        this.encoders = encoders;
    }

    @Resource
    private UsernamePasswordUserDetailsServiceImpl usernamePasswordUserDetailsService;

    @Resource
    private UsernamePasswordUserDetailsPasswordServiceImpl usernamePasswordUserDetailsPasswordService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return
                http
                        .csrf(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests(authorizeHttpRequests ->
                                authorizeHttpRequests
                                        .requestMatchers(HttpMethod.POST,"/authentication/login").permitAll()
                                        .anyRequest().authenticated()
                        )
                        .build();
    }

    /**
     * 加密方式。BCryptPasswordEncoder、Pbkdf2PasswordEncoder、SCryptPasswordEncoder
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new DelegatingPasswordEncoder(passwordEncoderProperties.getEncodingId().getEncodingId(), encoders);
    }

    /**
     * 自定义 AuthenticationManager
     * @return 局部 AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        authenticationProviders.add(usernamePasswordAuthenticationProvider());
        return new ProviderManager(authenticationProviders);
    }



    public AuthenticationProvider usernamePasswordAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(usernamePasswordUserDetailsService);
        authenticationProvider.setUserDetailsPasswordService(usernamePasswordUserDetailsPasswordService);
        return authenticationProvider;
    }
}
