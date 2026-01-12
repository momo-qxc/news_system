package com.newsmanager.core.util;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * MD5加密工具类
 * 使用MD5算法+Base64编码
 */
public class MD5Parse {

    /**
     * MD5加密（Base64编码）
     * @param input 明文字符串
     * @return Base64编码的MD5字符串
     */
    public static String encryptMD5(String input) {
        String encodedDigest = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes()); // 计算MD5摘要
            encodedDigest = Base64.getEncoder().encodeToString(digest); // 进行Base64编码

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return encodedDigest;
    }

}
