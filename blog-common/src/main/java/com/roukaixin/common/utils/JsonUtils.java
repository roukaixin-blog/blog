package com.roukaixin.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.roukaixin.common.exception.JsonException;

/**
 * json 操作工具类
 * @author pankx
 * @date 2023/8/29 22:30
 */
public class JsonUtils {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private final static Gson GSON = new Gson();

    private JsonUtils(){

    }

    public static Gson gson() {
        return GSON;
    }

    public static String toJsonString(Object value){
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new JsonException(e.getMessage());
        }
    }
}
