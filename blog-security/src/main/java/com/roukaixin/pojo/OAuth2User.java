package com.roukaixin.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.Assert;

import java.util.*;

/**
 * oauth2 用户
 *
 * @author 不北咪
 * @date 2024/4/2 下午4:57
 */
@Setter
@NoArgsConstructor
public class OAuth2User implements org.springframework.security.oauth2.core.user.OAuth2User {

    private String name;

    private Set<GrantedAuthority> authorities;

    private Map<String, Object> attributes;

    @Getter
    private String nameAttributeKey;

    public OAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes,
                      String nameAttributeKey) {
        Assert.notEmpty(attributes, "attributes cannot be empty");
        Assert.hasText(nameAttributeKey, "nameAttributeKey cannot be empty");
        if (!attributes.containsKey(nameAttributeKey)) {
            throw new IllegalArgumentException("Missing attribute '" + nameAttributeKey + "' in attributes");
        }
        this.authorities = (authorities != null)
                ? Collections.unmodifiableSet(new LinkedHashSet<>(this.sortAuthorities(authorities)))
                : Collections.unmodifiableSet(new LinkedHashSet<>(AuthorityUtils.NO_AUTHORITIES));
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
        this.nameAttributeKey = nameAttributeKey;
        this.setName(attributes.get(nameAttributeKey).toString());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    private Set<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(
                Comparator.comparing(GrantedAuthority::getAuthority));
        sortedAuthorities.addAll(authorities);
        return sortedAuthorities;
    }
}
