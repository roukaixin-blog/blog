package com.roukaixin.security.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * OAuto2 客户端注册信息
 *
 * @author 不北咪
 * @date 2024/3/5 上午10:18
 */
@Setter
@Getter
@TableName(value = "sys_client_registration", autoResultMap = true)
public class ClientRegistration {

    /**
     * 主键
     */
    private Long id;

    /**
     * 注册端标识，唯一
     */
    private String registrationId;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户密钥
     */
    private String clientSecret;

    /**
     * 客户端认证方式
     */
    private String clientAuthenticationMethod;

    /**
     * 认证授权类型
     */
    private String authorizationGrantType;

    /**
     * 重定向uri, 不能修改, 生成后需要填写到 OAuth2 应用中去
     */
    private String redirectUri;

    /**
     * 授权权限
     */
    @TableField(value = "scope", typeHandler = JacksonTypeHandler.class)
    private Set<String> scopes;

    /**
     * 提供商详情信息id, 与 sys_provider_details 关联
     */
    private Long providerDetailsId;

    /**
     * 客户端名字, 用于前端展示
     */
    private String clientName;

    /**
     * 重定向的地址, 不是 OAuth2 的重定向地址
     */
    private String redirect;

}
