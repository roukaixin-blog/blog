package com.kai.handler;

import com.kai.exception.JsonException;
import com.kai.pojo.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author pankx
 * @date 2023/8/30 10:13
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JsonException.class)
    public R<String> jsonException(JsonException jsonException){
        return R.error(jsonException.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public R<String> exception(Exception e){
        return R.error(e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public R<String> throwable(){
        return R.error();
    }

}
