package com.puhui.lib.utils.encrypt;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密工具类
 *
 * @author tangjian
 * @date 2015年6月5日
 */
public class AesEncryptUtil {
    private static final String IV_STRING = "9rpIAy@i1_4$j#*P";

    /**
     * 加密
     *
     * @param encryptKey   秘钥
     * @param dataPassword 需加密的原字符串
     * @return
     */
    public static String encrypt(String encryptKey, String dataPassword) {
        byte[] encryptedData = null;
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(encryptKey.getBytes("utf-8"));
            SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes("utf-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            encryptedData = cipher.doFinal(dataPassword.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return dataPassword;
        }

        return Base64.encodeToString(encryptedData, Base64.NO_WRAP);
    }

    /**
     * 解密
     *
     * @param dataPassword 解密前字符串
     * @param encrypted    秘钥
     * @return
     */
    public static String decrypt(String encrypted, String dataPassword) {
        byte[] decryptedData = null;
        String res = null;
        try {
            byte[] byteMi = Base64.decode(dataPassword, Base64.NO_WRAP);
            IvParameterSpec zeroIv = new IvParameterSpec(encrypted.getBytes("utf-8"));
            SecretKeySpec key = new SecretKeySpec(encrypted.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            decryptedData = cipher.doFinal(byteMi);
            res = new String(decryptedData, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Base64加密
     *
     * @param data
     * @param key
     * @return
     */
    public static String base64Encrypt(String data, String key) {
        if (data == null) {
            return "";
        }
        String encode = "";
        try {
            encode = new String(Base64.encode(data.getBytes("utf-8"), Base64.NO_WRAP));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        encode += key;
        byte[] b = encode.getBytes();
        for (int i = 0; i < b.length / 2; i++) {
            byte temp = b[i];
            b[i] = b[b.length - 1 - i];
            b[b.length - 1 - i] = temp;
        }
        return new String(b);
    }

    /**
     * Base64解密
     *
     * @param data
     * @param key
     * @return
     */
    public static String base64Decode(String data, String key) {
        if (data == null) {
            return "";
        }
        String decode = "";
        return decode;
    }
}