package com.roukaixin.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 登录实体
 *
 * @author 不北咪
 * @date 2024/3/19 下午8:57
 */
@Setter
@Getter
@ToString
public class UserDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
