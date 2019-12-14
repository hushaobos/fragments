package com.hjc.sign.core.common.encrypt;

import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;

public class PasswordEncrypt {
    //系统默认盐值
    public static final String DEFAULT_SALT = "hjc718";

    public static String encrypt(String password,String salt) throws UnsupportedEncodingException {
        String md5RawPass = DigestUtils.md5DigestAsHex(password.getBytes("UTF-8"));
        String saltedRawPass = DigestUtils.md5DigestAsHex(String.format("%s%s", md5RawPass, DEFAULT_SALT).getBytes("UTF-8"));
        String result = EncryptUtils.getSHA256Base64String(String.format("%s%s", salt,saltedRawPass));
        return result;
    }

    /**密码加密
     *
     * @param password 密码
     * @param salt 盐值
     * @return
     */
    public static String encryptSHS256(String password,String salt) throws UnsupportedEncodingException {
        String result = EncryptUtils.getSHA256Base64String(String.format("%s%s", salt,password));
        return result;
    }

    public static String encryptMD5T(String password) throws UnsupportedEncodingException {
        String md5RawPass = DigestUtils.md5DigestAsHex(password.getBytes("UTF-8"));
        String saltedRawPass = DigestUtils.md5DigestAsHex(String.format("%s%s", md5RawPass, DEFAULT_SALT).getBytes("UTF-8"));
        return saltedRawPass;
    }
}
