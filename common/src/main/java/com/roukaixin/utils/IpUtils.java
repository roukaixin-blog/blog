package com.roukaixin.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取 ip 地址
 *
 * @author 不北咪
 * @date 2024/3/25 上午9:42
 */
@Slf4j
public class IpUtils {

    private final static String UNKNOWN = "unknown";

    private final static String IP4_127 = "127.0.0.1";

    private final static String IP6_127 = "0:0:0:0:0:0:0:1";

    private static final String[] IP_HEADER = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    private IpUtils() {

    }

    public static String getIp(HttpServletRequest request) {
        // 提取header得到IP地址列表（多重代理场景），取第一个IP
        for (String header : IP_HEADER) {
            String ipList = request.getHeader(header);
            if (ipList != null && !ipList.isEmpty() &&
                    !UNKNOWN.equalsIgnoreCase(ipList)) {
                return ipList.split(",")[0];
            }
        }

        // 没有经过代理或者SLB，直接 getRemoteAddr 方法获取IP
        String ip = request.getRemoteAddr();

        // 如果是本地环回IP，则根据网卡取本机配置的IP
        if (IP4_127.equals(ip) || IP6_127.equals(ip)) {
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                return inetAddress.getHostAddress();
            } catch (UnknownHostException e) {
                log.error("获取ip出错", e);
            }
        }
        return ip;
    }
}
