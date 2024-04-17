package com.roukaixin.security.properties;

import com.roukaixin.security.enums.PasswordEncoderEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * password encoder yml配置
 *
 * @author 不北咪
 * @date 2024/1/23 上午8:48
 */
@ConfigurationProperties(prefix = "spring.security.password-encoder")
@Setter
@Getter
public class PasswordEncoderProperties {

    /**
     * 主要加解密类型
     */
    PasswordEncoderEnum encodingId = PasswordEncoderEnum.BCRYPT;

    /**
     * 加密算法是否开启。noop 不开启
     */
    Map<PasswordEncoderEnum, Boolean> encoders;
}
