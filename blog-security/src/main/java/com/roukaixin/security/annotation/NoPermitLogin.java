package com.roukaixin.security.annotation;

import java.lang.annotation.*;

/**
 * 允许不用登录就可以访问接口
 *
 * @author 不北咪
 * @date 2024/3/4 上午10:25
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface NoPermitLogin {

}
