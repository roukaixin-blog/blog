package com.roukaixin.security.constant;

/**
 * 登陆相关的常量
 *
 * @author 不北咪
 * @date 2024/3/21 下午9:43
 */
public class LoginConstant {

    public final static String TOKEN_TYPE = "Bearer";

    public final static String AES_KEY_ACCESS_TOKEN = "t/qaiTKne8IpAn66iv34oA==";

    public final static String AES_KEY_REFRESH_TOKEN = "6sZ5tWD3JzZKTGtsj+lZvA==";

    public final static long EXPIRES_TIME = 5 * 60 * 1000;

    public final static long REFRESH_TOKEN_EXPIRES_TIME = EXPIRES_TIME * 60;

    public final static String SYSTEM = "system";

    public final static String OAUTH2 = "oauth2";

    public final static String LOGIN_USER_INFO = "login:user:info:";

    public final static String LOGIN_USER_ACCESS_TOKEN = "login:user:access_token:";

    public final static String LOGIN_USER_REFRESH_TOKEN = "login:user:refresh_token:";

    public final static String STATE = "state:";

    public final static String NAME = "name:";
}
