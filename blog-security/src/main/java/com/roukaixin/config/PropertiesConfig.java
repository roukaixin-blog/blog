package com.roukaixin.config;

import com.roukaixin.properties.PasswordEncoderProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * yml 属性配置，开启那些 bean 对应 yml 配置
 *
 * @author 不北咪
 * @date 2024/1/23 下午3:33
 */
@EnableConfigurationProperties(PasswordEncoderProperties.class)
@Configuration
public class PropertiesConfig {

}
