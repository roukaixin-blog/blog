package com.roukaixin.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * aes 加密算法
 *
 * @author 不北咪
 * @date 2024/3/21 上午11:10
 */
@Slf4j
public class AesUtils {

    private final static String AES = "AES";
    private final static String AES_ECB_PKCS5PADDING = "AES/ECB/PKCS5Padding";

    private final static List<Integer> KEY_SIZE = Arrays.asList(128, 192, 256);

    private AesUtils() {

    }

    /**
     * 获取密钥
     * @param keySize 只能输入 128、192、256
     * @return byte[]
     */
    public static String getSecretKey(int keySize) {
        if (!KEY_SIZE.contains(keySize)) {
            throw new RuntimeException("输入的 key size 不符合 AES 所需的位数");
        }
        try {
            // 创建机密生成器
            KeyGenerator generator = KeyGenerator.getInstance(AES);
            // 初始化
            generator.init(keySize, new SecureRandom());
            // 获取密钥
            SecretKey secretKey = generator.generateKey();
            byte[] key = secretKey.getEncoded();
            return Base64.getEncoder().encodeToString(key);
        } catch (NoSuchAlgorithmException e) {
            log.error("获取 AES 密钥失败。", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 加密
     * @param key 密钥
     * @param data 内容
     * @return byte[]
     */
    public static String encrypt(byte[] key, String data) {
        try {
            byte[] encrypt = getCipher(key, Cipher.ENCRYPT_MODE).doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypt);
        } catch (Exception e) {
            log.error("加密失败", e);
            throw new RuntimeException("AES 加密失败");
        }

    }

    /**
     * 解密
     * @param key 密钥
     * @param data 密文
     * @return byte[]
     */
    public static byte[] decrypt(byte[] key, String data) {
        try {
            return getCipher(key, Cipher.DECRYPT_MODE).doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("加密失败", e);
            throw new RuntimeException("AES 加密失败");
        }
    }

    private static Cipher getCipher(byte[] key, int mode)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKey secretKey = new SecretKeySpec(key, AES);
        Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5PADDING);
        cipher.init(mode, secretKey);
        return cipher;
    }
}
