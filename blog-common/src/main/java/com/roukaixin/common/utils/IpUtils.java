package com.roukaixin.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

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

    private static final Set<String> IP_HEADER = Set.of(
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
    );

    private IpUtils() {

    }

    public static String getIp(HttpServletRequest request) {

        if (request == null) {
            return UNKNOWN;
        }

        // 提取 header 得到IP地址列表（多重代理场景），取第一个IP。没有经过代理或者SLB，直接 getRemoteAddr 方法获取IP
        String ip = IP_HEADER.stream()
                .map(request::getHeader)
                .filter(sourceIp -> sourceIp != null && !sourceIp.isEmpty() && !UNKNOWN.equalsIgnoreCase(sourceIp))
                .findFirst()
                .orElse(request.getRemoteAddr())
                .split(",")[0];

        // 如果是本地环回IP，则根据网卡取本机配置的IP
        if (IP4_127.equals(ip) || IP6_127.equals(ip)) {
            try {
                // 获取内网 ip 地址
                InetAddress inetAddress = InetAddress.getLocalHost();
                return inetAddress.getHostAddress();
            } catch (UnknownHostException e) {
                log.error("获取网卡本机ip出错", e);
                return UNKNOWN;
            }
        }
        return ip;
    }
}
