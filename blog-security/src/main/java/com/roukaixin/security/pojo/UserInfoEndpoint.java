package com.roukaixin.security.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.roukaixin.security.enums.AuthenticationMethodEnum;
import lombok.*;

/**
 * oauth2 用户信息端点
 *
 * @author 不北咪
 * @date 2024/3/5 上午10:47
 */
@Setter
@Getter
@TableName(value = "sys_user_info_endpoint")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoEndpoint {

    /**
     * 主键
     */
    private Long id;

    /**
     * OAuth2 获取用户信息接口
     */
    private String uri;

    /**
     * OAuth2 身份验证方式。可选值：header、form、query
     */
    private AuthenticationMethodEnum authenticationMethod;

    /**
     * OAuth2 第三方账号唯一标识
     */
    private String userNameAttributeName;
}
