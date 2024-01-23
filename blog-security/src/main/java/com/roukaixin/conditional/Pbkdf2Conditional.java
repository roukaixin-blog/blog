package com.roukaixin.conditional;

import com.alibaba.fastjson2.JSON;
import com.roukaixin.enums.PasswordEncoderEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Pbkdf2PasswordEncoder 注入判断
 *
 * @author 不北咪
 * @date 2024/1/23 下午3:20
 */
@Slf4j
public class Pbkdf2Conditional implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        log.info("Pbkdf2Conditional - 方法中的注解：{}", JSON.toJSONString(metadata.getAnnotations()));
        String encodingIdKey = "spring.security.password-encoder.encoding-id";
        PasswordEncoderEnum encodingId = context.getEnvironment().getProperty(encodingIdKey, PasswordEncoderEnum.class);
        if (encodingId == null) {
            // 如果没有配置 encodingId 默认是 BCryptPasswordEncoder 加解密
            return false;
        } else {
            if (encodingId.equals(PasswordEncoderEnum.PBKDF2)) {
                // encodingId 为 pbkdf2Encoder 时，注入 Pbkdf2PasswordEncoder 加密
                return true;
            } else {
                String encodersKey = "spring.security.password-encoder.encoders." +
                        PasswordEncoderEnum.PBKDF2.getEncodingId();
                Boolean pbkdf2Encoder = context.getEnvironment().getProperty(encodersKey, Boolean.class);
                return pbkdf2Encoder == null ? PasswordEncoderEnum.PBKDF2.isStatus() : pbkdf2Encoder;
            }
        }
    }
}
