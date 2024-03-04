package com.roukaixin.annotation;

import java.lang.annotation.*;

/**
 * 允许不登录
 *
 * @author 不北咪
 * @date 2024/3/4 上午10:25
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface NoPermitLogin {

}
