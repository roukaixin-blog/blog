package com.roukaixin.handler;

import com.roukaixin.exception.JsonException;
import com.roukaixin.pojo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author pankx
 * @date 2023/8/30 10:13
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(JsonException.class)
    public R<String> jsonException(JsonException jsonException){
        log.error("", jsonException);
        return R.error(jsonException.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public R<String> exception(Exception e){
        log.error("", e);
        return R.error(e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public R<String> throwable(Throwable e){
        log.error("", e);
        return R.error();
    }

}
