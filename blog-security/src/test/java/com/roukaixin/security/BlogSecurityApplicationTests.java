package com.roukaixin.security;

import com.roukaixin.security.constant.LoginConstant;
import com.roukaixin.common.utils.AesUtils;
import com.roukaixin.common.utils.JsonUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
class BlogSecurityApplicationTests {

    @Test
    @SneakyThrows
    void contextLoads() {
        String secretKey = AesUtils.getSecretKey(128);
        System.out.println(secretKey);
    }

    @Test
    @SneakyThrows
    void testShell() {
        Set<String> set = new HashSet<>();
        set.add("a");
        set.add("b");
        set.add("c");
        System.out.println(JsonUtils.toJsonString(set));
    }

    @Test
    @SneakyThrows
    void testIP6() {
        InetAddress inetAddress = InetAddress.getLocalHost();
        System.out.println(inetAddress);

        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            System.out.println(networkInterface);
        }


        System.out.println(AesUtils.getSecretKey(128));
        System.out.println(AesUtils.getSecretKey(128));

        System.out.println(AesUtils.decrypt(LoginConstant.AES_KEY_ACCESS_TOKEN,
                "Ee39bUsSUVn8WzAQ6DEHEhh9vTxpGqz7qIGG1tRsZ1/GjNTKVHWGwTNRfiYaAdF2"));
    }

}
