package com.roukaixin.config;

import com.roukaixin.conditional.BCryptConditional;
import com.roukaixin.conditional.NoOpConditional;
import com.roukaixin.conditional.Pbkdf2Conditional;
import com.roukaixin.conditional.SCryptConditional;
import com.roukaixin.enums.PasswordEncoderEnum;
import com.roukaixin.properties.PasswordEncoderProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

/**
 * password 加密配置。BCryptPasswordEncoder（官方推荐）、Pbkdf2PasswordEncoder、SCryptPasswordEncoder
 * 自定义加密方式，把 security 过时的加密方式去掉，保留一个 noop(明文密码) 加密
 *
 * @author 不北咪
 * @date 2024/1/22 上午10:38
 */
@Configuration
@EnableConfigurationProperties(PasswordEncoderProperties.class)
public class PasswordEncoderConfig {

    private static PasswordEncoderProperties PASSWORD_ENCODER_PROPERTIES;

    public PasswordEncoderConfig(PasswordEncoderProperties passwordEncoderProperties) {
        PASSWORD_ENCODER_PROPERTIES = passwordEncoderProperties;
    }

    @Bean
    @Conditional(BCryptConditional.class)
    public PasswordEncoder bcrypt() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Conditional(Pbkdf2Conditional.class)
    public PasswordEncoder pbkdf2() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    @Conditional(SCryptConditional.class)
    public PasswordEncoder scrypt() {
        return SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    @Conditional(NoOpConditional.class)
    public PasswordEncoder noop() {
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }

    public static PasswordEncoderEnum encodingId() {
        return PASSWORD_ENCODER_PROPERTIES.getEncodingId();
    }


}
