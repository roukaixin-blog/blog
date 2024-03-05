package com.roukaixin.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 认证方法枚举
 *
 * @author 不北咪
 * @date 2024/3/5 上午11:03
 */
@Getter
@AllArgsConstructor
public enum AuthenticationMethodEnum {

    /**
     * 请求头方式
     */
    HEADER("header"),

    /**
     * 表单请求
     */
    FORM("form"),

    /**
     * 请求参数
     */
    QUERY("query");

    @EnumValue
    private final String value;
}
