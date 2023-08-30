package com.kai.exception;

/**
 * json 转化异常
 * @author pankx
 * @date 2023/8/30 10:21
 */
public class JsonException extends RuntimeException {

    public JsonException(String message){
        super("json 转化异常：" + message);
    }

}
