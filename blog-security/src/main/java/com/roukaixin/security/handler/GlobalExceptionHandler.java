package com.roukaixin.security.handler;

import com.roukaixin.common.exception.JsonException;
import com.roukaixin.common.exception.LimitException;
import com.roukaixin.common.pojo.R;
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

    @ExceptionHandler(LimitException.class)
    public R<String> limitException(LimitException limitException) {
        log.error("接口发生限流", limitException);
        return R.error(limitException.getMessage());
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
