package com.roukaixin.security.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.util.Map;

/**
 * 第三方客户端认证等一些信息
 *
 * @author 不北咪
 * @date 2024/3/5 上午10:20
 */
@Setter
@Getter
@TableName(value = "sys_provider_details", autoResultMap = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProviderDetails {

    /**
     * 主键
     */
    private Long id;

    /**
     * OAuth2 提供商标识。例如: github、google
     */
    private String registrationId;

    /**
     * OAuth2 提供商认证接口
     */
    private String authorizationUri;

    /**
     * OAuth2 提供商获取 token 接口
     */
    private String tokenUri;

    /**
     * 用户信息端点id，关联 sys_user_info_endpoint 表
     */
    private Long userInfoEndpointId;

    private String jwkSetUri;

    private String issuerUri;

    /**
     * 配置元数据
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> configurationMetadata;
}
