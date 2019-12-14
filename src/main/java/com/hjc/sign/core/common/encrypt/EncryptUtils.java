package com.hjc.sign.core.common.encrypt;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtils {
    public static String getSHA256Base64String(String sourceString) {
        try {
            return getBase64WithDigestString(sourceString, "SHA-256");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static String getBase64WithDigestString(String sourceString, String algorithmn) throws UnsupportedEncodingException {
        MessageDigest md5Digest = getMessageDigest(algorithmn);
        byte[] result = md5Digest.digest(sourceString.getBytes("UTF-8"));
        return new String(Base64.encode(result));
    }

    public static String getSHA256Hash(String sourceString) throws UnsupportedEncodingException {
        MessageDigest md5Digest = getMessageDigest("SHA-256");
        byte[] result = md5Digest.digest(sourceString.getBytes("UTF-8"));
        return bytesToHex(result);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for(byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }
        return buf.toString();
    }

    protected static final MessageDigest getMessageDigest(String algorithm) throws IllegalArgumentException {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException var2) {
            throw new IllegalArgumentException("No such algorithm [" + algorithm + "]");
        }
    }
}
