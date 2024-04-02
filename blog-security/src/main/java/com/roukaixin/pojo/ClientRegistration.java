package com.roukaixin.pojo;

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
@TableName(value = "client_registration", autoResultMap = true)
public class ClientRegistration {

    /**
     * 主键
     */
    private Long id;

    /**
     * 注册端标识，唯一（一个项目只能有一个项目的id）
     */
    private String registrationId;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户端密码
     */
    private String clientSecret;

    /**
     * 客户端认证方法
     */
    private String clientAuthenticationMethod;

    /**
     * 认证授权类型
     */
    private String authorizationGrantType;

    /**
     * 重定向uri（重定向到服务地址(个人项目的地址)接口）
     */
    private String redirectUri;

    /**
     * 授权范围
     */
    @TableField(value = "scope", typeHandler = JacksonTypeHandler.class)
    private Set<String> scopes;

    /**
     * 提供商详情信息id（provider_details关联）
     */
    private Long providerDetailsId;

    /**
     * 客户端名字
     */
    private String clientName;

    /**
     *
     */
    private String redirect;

}
