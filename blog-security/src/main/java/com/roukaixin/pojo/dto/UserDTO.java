package com.roukaixin.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "用户账号")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;
}
