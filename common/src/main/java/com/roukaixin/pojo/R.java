package com.roukaixin.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 统一结果返回
 * @author pankx
 * @date 2023/8/29 22:09
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class R<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    public static <T> R<T> success(Integer code, String message, T data){
        return new R<>(code, message, data);
    }

    public static <T> R<T> success(String message, T data){
        return success(200, message, data);
    }

    public static <T> R<T> success(T data){
        return success(null, data);
    }

    public static <T> R<T> error(Integer code, String message, T data){
        return new R<>(code, message, data);
    }

    public static <T> R<T> error(String message, T data){
        return error(500, message, data);
    }

    public static <T> R<T> error(String message){
        return error(message, null);
    }

    public static <T> R<T> error(){
        return error("服务器异常");
    }
}
