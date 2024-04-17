package com.roukaixin.security.authorization.authority;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

/**
 * 简单授予权限。自定义来覆盖框架的 SimpleGrantedAuthority
 *
 * <p>
 *     账号密码登录
 *     oauth2
 * </p>
 *
 * @author 不北咪
 * @date 2024/4/3 上午9:21
 */
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SimpleGrantedAuthority implements GrantedAuthority {

    /**
     * 权限，框架字段名是使用 role
     */
    private String authority;

    public SimpleGrantedAuthority(String authority) {
        Assert.hasText(authority, "A granted authority textual representation is required");
        this.authority = authority;
    }


    @Override
    public String getAuthority() {
        return this.authority;
    }
}
