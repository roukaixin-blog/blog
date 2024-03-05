package com.roukaixin.pojo;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 第三方客户端认证等一些信息
 *
 * @author 不北咪
 * @date 2024/3/5 上午10:20
 */
@Setter
@Getter
@TableName(value = "provider_details")
public class ProviderDetails {

    /**
     * 主键
     */
    private Long id;

    /**
     * 第三方服务商(github)的登录接口
     */
    private String authorizationUri;

    /**
     * 第三方服务商(github)获取token的接口
     */
    private String tokenUri;

    /**
     * 用户信息端点id
     */
    private Long userInfoEndpointId;

    private String jwkSetUri;

    private String issuerUri;

    /**
     * 配置源数据
     */
    private Map<String, Object> configurationMetadata;
}
