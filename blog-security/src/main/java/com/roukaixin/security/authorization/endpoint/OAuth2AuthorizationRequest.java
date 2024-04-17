package com.roukaixin.security.authorization.endpoint;

import lombok.*;

import java.util.Map;
import java.util.Set;

/**
 * 自定义 OAuth2 认证请求
 * see {@link org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest}
 *
 * @author 不北咪
 * @date 2024/3/7 下午3:47
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2AuthorizationRequest {

    /**
     * 认证 uri
     */
    private String authorizationUri;

    /**
     * 授权类型
     */
    private String authorizationGrantType;

    /**
     * 响应类型
     */
    private String responseType;

    /**
     *  客户端id
     */
    private String clientId;

    /**
     * 重定向地址
     */
    private String redirectUri;

    /**
     * 权限
     */
    private Set<String> scopes;

    /**
     * state
     */
    private String state;

    /**
     * 附加参数
     */
    private Map<String, Object> additionalParameters;

    /**
     * 认证请求 uri
     */
    private String authorizationRequestUri;

    /**
     * 属性
     */
    private Map<String, Object> attributes;
}
