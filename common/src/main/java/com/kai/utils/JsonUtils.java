package com.kai.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * json 操作工具类
 * @author pankx
 * @date 2023/8/29 22:30
 */
public class JsonUtils {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtils(){

    }

    @SneakyThrows(Throwable.class)
    public static String toJsonString(Object value){
        return MAPPER.writeValueAsString(value);
    }
}
