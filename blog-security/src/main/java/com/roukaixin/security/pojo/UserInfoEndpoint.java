package com.roukaixin.security.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.roukaixin.security.enums.AuthenticationMethodEnum;
import lombok.*;

/**
 * oauth2 用户断点信息
 *
 * @author 不北咪
 * @date 2024/3/5 上午10:47
 */
@Setter
@Getter
@TableName(value = "user_info_endpoint")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoEndpoint {

    /**
     * 主键
     */
    private Long id;

    /**
     * 获取第三方服务商用户信息接口
     */
    private String uri;

    /**
     * 认证方法。可选值：header，form，query
     */
    private AuthenticationMethodEnum authenticationMethod;

    /**
     * 第三方用户名的字段
     */
    private String userNameAttributeName;
}
