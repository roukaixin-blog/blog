package com.roukaixin.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}
