package com.hjc.sign.core.common.encrypt;

import com.hjc.sign.core.common.AuthConstant;
import com.hjc.sign.core.common.EncryCredentialInfo;
import com.hjc.utils.RandomStrings;
import com.hjc.utils.StringUtil;

public class GenerateSecretTokenEncrypt {
    private static final String SEPARATE = "_";//accessToken内容分隔符
    private static final String ACCESS_TOKEN_KEY = "LSacTTOOKKEN20_15";//accessToken秘钥
    protected static final String REFEFSH_TOKEN_KEY = "reLSTT20_15OOKKEn";//refreshToken秘钥

    public static final long APP_TERM = 30 * 24 * 60 * 60L;//app 的 accessToken期限30日,单位秒

    public static final long WEB_TERM = 30 * 60L;//web 的 accessToken期限30日,单位秒

    /**生成token的基本信息
     *
     * @param prefix
     * @return
     */
    public static String generateToken(long prefix,long loginTimestamp) {
        String ranStr = RandomStrings.nextString(5);
        return StringUtil.strAppend(prefix,SEPARATE,loginTimestamp,prefix,SEPARATE,ranStr);//生成accessToken基本内容
    }

    /**生成accessToken
     *
     * @param content
     * @return
     */
    public static String createAccessToken(String content)
    {
        return AesHelper.encrypt(content,ACCESS_TOKEN_KEY,128);
    }

    /**生成refreshToken
     *
     * @param content
     * @return
     */
    public static String createRefreshToken(String content)
    {
        return AesHelper.encrypt(content,REFEFSH_TOKEN_KEY,128);
    }

    /**解密accessToken
     *
     * @param plainAccessToken
     * @param outTokenInfo
     * @return -1:fail, 0:ok
     */
    public static int decryptSecretAccessToken(String plainAccessToken, EncryCredentialInfo outTokenInfo,String client) {
        if(outTokenInfo == null)
            return -1;

        String decryptResult = AesHelper.decrypt(plainAccessToken,ACCESS_TOKEN_KEY, 128);//解密
        if(decryptResult != null ) {
            String[] infos = decryptResult.split(SEPARATE);
            if(infos.length >= 2) {
                outTokenInfo.setUserId(Long.valueOf(infos[0]));

                long expiryTime = Long.valueOf(infos[1]) * 1000;//过期时间
                long createTime;
                switch (client){
                    case AuthConstant
                            .APP_CLIENT:
                        createTime = expiryTime - GenerateSecretTokenEncrypt.APP_TERM;//创建时间
                        break;
                    case AuthConstant.WEB_CLIENT:
                    default:
                        createTime = expiryTime - GenerateSecretTokenEncrypt.WEB_TERM;//创建时间
                        break;
                }
                outTokenInfo.setCreateTime(createTime);
                outTokenInfo.setExpiryTime(expiryTime);
                return 0;
            }
        }
        return -1;
    }
}
