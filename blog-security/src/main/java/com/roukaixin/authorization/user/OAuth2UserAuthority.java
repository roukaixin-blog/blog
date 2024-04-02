package com.roukaixin.authorization.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * oauth2 登陆用户权限
 *
 * @author 不北咪
 * @date 2024/4/3 上午12:03
 */
@Setter
@EqualsAndHashCode
public class OAuth2UserAuthority implements GrantedAuthority {

    private String authority;

    @Getter
    private Map<String, Object> attributes;

    public OAuth2UserAuthority(Map<String, Object> attributes) {
        this("OAUTH2_USER", attributes);
    }

    public OAuth2UserAuthority(String authority, Map<String, Object> attributes) {
        Assert.hasText(authority, "authority cannot be empty");
        Assert.notEmpty(attributes, "attributes cannot be empty");
        this.authority = authority;
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
