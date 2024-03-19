package com.roukaixin.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 登录成功返回 token 信息
 *
 * @author 不北咪
 * @date 2024/3/19 下午9:47
 */
@Setter
@Getter
@ToString
public class LoginSuccessVO {

    /**
     * 令牌类型
     */
    private String tokenType;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 访问令牌发放时间
     */
    private Long issuedAt;

    /**
     * 访问令牌过期时间
     */
    private Long expiresAt;
}
