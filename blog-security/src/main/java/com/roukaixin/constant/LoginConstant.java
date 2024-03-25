package com.roukaixin.constant;

/**
 * 登陆相关的常量
 *
 * @author 不北咪
 * @date 2024/3/21 下午9:43
 */
public class LoginConstant {

    public final static String TOKEN_TYPE = "Bearer";

    public final static String AES_KEY_ACCESS_TOKEN = "jdb9H6spaVAoTfwiwDiSCw==";

    public final static String AES_KEY_REFRESH_TOKEN = "5y6+hhmXsPIclHbwi4XQHQ==";

    public final static long EXPIRES_TIME = 5 * 60 * 1000;

    public final static String SYSTEM = "system:";

    public final static String USER_INFO_SYSTEM = "login:user:info:system:";

    public final static String USER_ACCESS_TOKEN_SYSTEM = "login:user:access_token:system:";

    public final static String USER_REFRESH_TOKEN_SYSTEM = "login:user:refresh_token:system:";
}
