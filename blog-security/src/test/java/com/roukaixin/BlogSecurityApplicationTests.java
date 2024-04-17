package com.roukaixin;

import com.roukaixin.constant.LoginConstant;
import com.roukaixin.utils.AesUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BlogSecurityApplicationTests {

    @Test
    void contextLoads() {

        System.out.println(AesUtils.getSecretKey(128));
        System.out.println(AesUtils.getSecretKey(128));

        System.out.println(AesUtils.decrypt(LoginConstant.AES_KEY_ACCESS_TOKEN,
                "Ee39bUsSUVn8WzAQ6DEHEhh9vTxpGqz7qIGG1tRsZ1/GjNTKVHWGwTNRfiYaAdF2"));
    }

}
