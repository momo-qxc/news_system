package com.tester;

import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.util.Base64;

public class MD5Parse {

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

    public void testmethod(){
        MD5Parse.encryptMD5("123");
    }
}