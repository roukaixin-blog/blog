package com.roukaixin.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 *
 * @author 不北咪
 * @date 2024/3/24 下午10:41
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Limit {

    /**
     * 每多少秒请求多少次
     * @return long
     */
    long time() default 5;

    /**
     * 请求最大次数
     * @return long
     */
    long count() default 10;
}
