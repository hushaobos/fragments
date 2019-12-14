package com.hjc.sign.core.common.encrypt;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AesHelper {

    private static final String ALGORITHM = "AES";

    private static final String CODEFORMAT = "MD5";

    private static final String STRING_FORMAT = "UTF-8";

    /**
     * AES使用CBC模式與PKCS5Padding
     */
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

	//-----類別常數-----
	/**
	 * 預設的Initialization Vector，為16 Bits的0
	 */
	private static final IvParameterSpec DEFAULT_IV = new IvParameterSpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

	private static Cipher cipherInit(String content,SecretKeySpec keySpec) throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidAlgorithmParameterException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);// 创建密码器
		cipher.init(Cipher.ENCRYPT_MODE, keySpec,DEFAULT_IV);// 初始化
		return cipher;
	}

	/**秘密密钥规格
	 *
	 * @param enCodeFormat
	 * @return
	 */
	private static SecretKeySpec secretKey(byte[] enCodeFormat)
	{
		SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, ALGORITHM);//秘密密钥规格
		return keySpec;
	}

	//-----物件方法-----
	/**
	 * 取得字串的雜湊值
	 *
	 * @param algorithm 傳入雜驟演算法
	 * @param text 傳入要雜湊的字串
	 * @return 傳回雜湊後資料內容
	 */
	private static byte[] getHash(final String algorithm, final String text) {
		try {
			return getHash(algorithm, text.getBytes(STRING_FORMAT));
		} catch (final Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	/**
	 * 取得資料的雜湊值
	 *
	 * @param algorithm 傳入雜驟演算法
	 * @param data 傳入要雜湊的資料
	 * @return 傳回雜湊後資料內容
	 */
	private static byte[] getHash(final String algorithm, final byte[] data) {
		try {
			final MessageDigest digest = MessageDigest.getInstance(algorithm);
			digest.update(data);
			return digest.digest();
		} catch (final Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}
    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @param KEY  加密密码
     * @Param digit 秘钥
     * @return
     */
    public static String encrypt(String content, String KEY,int digit) {
        try {
        	byte[] enCodeFormat = getHash(CODEFORMAT,KEY);
            SecretKeySpec keySpec = secretKey(enCodeFormat);//秘密密钥规格
			Cipher cipher = cipherInit(content,keySpec);// 创建密码器
            byte[] byteContent = content.getBytes(STRING_FORMAT);
            byte[] result = cipher.doFinal(byteContent);
            return Base64.encode(result);// 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return null;
    }

    /**解密
     * @param content  待解密内容
     * @param KEY 解密密钥
     * @return
     */
    public static String decrypt(String content, String KEY,int digit) {
        try {
			byte[] enCodeFormat = getHash(CODEFORMAT,KEY);
			SecretKeySpec keySpec = secretKey(enCodeFormat);//秘密密钥规格
			Cipher cipher = cipherInit(content,keySpec);// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, keySpec,DEFAULT_IV);// 初始化
            byte[] result = cipher.doFinal(Base64.decode(content));
            return new String(result,STRING_FORMAT);// 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return null;
    }
}
