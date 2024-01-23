package com.roukaixin.conditional;

import com.alibaba.fastjson2.JSON;
import com.roukaixin.enums.PasswordEncoderEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * SCryptPasswordEncoder 注入判断
 *
 * @author 不北咪
 * @date 2024/1/23 下午3:15
 */
@Slf4j
public class SCryptConditional implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        log.info("SCryptPasswordEncoder - 方法中的注解：{}", JSON.toJSONString(metadata.getAnnotations()));
        String encodingIdKey = "spring.security.password-encoder.encoding-id";
        PasswordEncoderEnum encodingId = context.getEnvironment().getProperty(encodingIdKey, PasswordEncoderEnum.class);
        if (encodingId == null) {
            // 如果没有配置 encodingId 默认是 BCryptPasswordEncoder 加解密
            return false;
        } else {
            if (encodingId.equals(PasswordEncoderEnum.SCRYPT)) {
                // encodingId 为 noop 时，注入 NoOpPasswordEncoder 加密
                return true;
            } else {
                String encodersKey = "spring.security.password-encoder.encoders." +
                        PasswordEncoderEnum.SCRYPT.getEncodingId();
                Boolean noopEncoder = context.getEnvironment().getProperty(encodersKey, Boolean.class);
                return noopEncoder == null || noopEncoder;
            }
        }
    }
}
