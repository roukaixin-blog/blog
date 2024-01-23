package com.roukaixin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 加密可选类型枚举
 *
 * @author 不北咪
 * @date 2024/1/23 上午9:12
 */
@Getter
@AllArgsConstructor
public enum PasswordEncoderEnum {

    /**
     * BCryptPasswordEncoder，默认加密算法
     */
    BCRYPT("bcrypt", true),

    /**
     * Pbkdf2PasswordEncoder
     */
    PBKDF2("pbkdf2", true),

    /**
     * SCryptPasswordEncoder
     */
    SCRYPT("scrypt", true),

    /**
     * NoOpPasswordEncoder
     */
    NOOP("noop", false);

    /**
     * 加密前缀
     */
    private final String encodingId;

    /**
     * 是否开启
     */
    private final boolean status;

}
