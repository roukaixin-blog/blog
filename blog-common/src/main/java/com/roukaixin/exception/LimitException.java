package com.roukaixin.exception;


/**
 * 限流异常
 *
 * @author 不北咪
 * @date 2024/3/24 下午11:37
 */
public class LimitException extends RuntimeException {

    public LimitException(){
        super("请求太频繁，请稍后点击");
    }
}
