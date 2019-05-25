package com.puhui.lib.utils.encrypt;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/2/14.
 * Description: DES加密解密工具包
 * Modified: by TangJian on 2017/2/14.
 */

public class DesEncryptUtil {

    /****
     * 在下面的加密和解密的方法里面，我们在获取Cipher实例的时候，传入了一个字符串"DES/CBC/PKCS5Padding"，这三个参数是什么意思呢？
     实际上，这三个参数分别对应的是“算法/模式/填充”，也就是说我们要用DES算法进行加密，采用的是CBC模式，填充方式采用PKCS5Padding。
     除了CBC模式，还有ECB模式等，指的是不同的加密方式。
     那么CBC模式和ECB模式又有什么区别呢？

     ECB模式指的是电子密码本模式，是一种最古老,最简单的模式，将加密的数据分成若干组，每组的大小跟加密密钥长度相同；
     然后每组都用相同的密钥加密, 比如DES算法, 如果最后一个分组长度不够64位,要补齐64位。这种模式的特点是：
     1.每次Key、明文、密文的长度都必须是64位；
     2.数据块重复排序不需要检测；
     3.相同的明文块(使用相同的密钥)产生相同的密文块，容易遭受字典攻击；
     4.一个错误仅仅会对一个密文块产生影响；

     CBC模式指的是加密块链模式，与ECB模式最大的不同是加入了初始向量。下面的代码就是获取一个初始向量，
     IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
     这种模式的特点是：
     1.每次加密的密文长度为64位(8个字节);
     2.当相同的明文使用相同的密钥和初始向量的时候CBC模式总是产生相同的密文;
     3.密文块要依赖以前的操作结果,所以，密文块不能进行重新排列;
     4.可以使用不同的初始化向量来避免相同的明文产生相同的密文,一定程度上抵抗字典攻击;
     5.一个错误发生以后,当前和以后的密文都会被影响;

     PKCS5Padding参数则是在说明当数据位数不足的时候要采用的数据补齐方式，也可以叫做数据填充方式。
     PKCS5Padding这种填充方式，具体来说就是“填充的数字代表所填字节的总数”
     比如说，差两个字节，就是######22，差5个字节就是###55555，这样根据最后一个自己就可以知道填充的数字和数量。

     介绍完DES的这些细节之后，我们就可以知道，在不同的平台上，只要能保证这几个参数的一致，就可以实现加密和解密的一致性。
     1.加密和解密的密钥一致
     2.采用CBC模式的时候，要保证初始向量一致
     3.采用相同的填充模式
     */
    public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @param key  加密私钥，长度不能小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     */
    public static String desEncode(String key, String data) {
        if (data == null)
            return null;
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
            AlgorithmParameterSpec paramSpec = iv;
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
            byte[] bytes = cipher.doFinal(data.getBytes());
            return byte2String(bytes);
        } catch (InvalidAlgorithmParameterException exception) {  // 证书问题
            exception.printStackTrace();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * DES算法，解密
     *
     * @param data 待解密字符串
     * @param key  解密私钥，长度不能够小于8位
     * @return 解密后的字节数组
     */
    public static String desDecode(String key, String data) {
        if (data == null)
            return null;
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
            AlgorithmParameterSpec paramSpec = iv;
            cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
            return new String(cipher.doFinal(byte2hex(data.getBytes())));
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * 二行制转字符串
     *
     * @param b
     * @return
     */
    private static String byte2String(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toUpperCase(Locale.CHINA);
    }

    /**
     * 二进制转化成16进制
     *
     * @param b
     * @return
     */
    private static byte[] byte2hex(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException();
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

}
