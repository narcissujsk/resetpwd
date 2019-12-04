package com.github.resetpwd;


import org.junit.Test;
import org.apache.log4j.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
/**
 AES加密算法就是众多对称加密算法中的一种，它的英文全称是Advanced Encryption Standard，
 翻译过来是高级加密标准，它是用来替代之前的DES加密算法的。
 AES加密算法采用分组密码体制，每个分组数据的长度为128位16个字节，密钥长度可以是128位16个字节、192位或256位，
 一共有四种加密模式，我们通常采用需要初始向量IV的CBC模式，初始向量的长度也是128位16个字节。


 */

/**
 * @program: cps-serviceapi
 * @description:
 * @author: jiangsk@inspur.com
 * @create: 2019-08-09 09:08
 **/
public class AESTest {
    private static Logger logger = Logger.getLogger(AESTest.class);
    /**
     * 加密用的Key 可以用26个字母和数字组成
     * 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    private static String sKey = "1234567838515212";
    private static String ivParameter = "1234567890123456";

    // 加密
    public static String encrypt(String sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] str = encoder.encode(encrypted);//此处使用BASE64做转码。
        return new String(str, UTF_8);
    }

    // 解密
    public static String decrypt(String sSrc) {
        try {
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] encrypted1 = decoder.decode(sSrc);//先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }


    public static void main(String[] args)
    {
        logger.info("CloudResetPwdAgent start..........");
        try
        {
            logger.info("log");
            String str = "385152jsk";
            String sec = encrypt(str);
            System.out.println(sec);
            System.out.println(sKey);

            System.out.println(decrypt(sec));
        }
        catch (Exception e)
        {
            logger.error(e);
        }
        logger.info("CloudResetPwdAgent finish..........");
    }
    @Test
    public  void test2() throws Exception {
        try {
            //待加密内容
            logger.info("log");
            String str = "385152jsk";
            String sec = encrypt(str);
            System.out.println(sec);
            System.out.println(sKey);

            System.out.println(decrypt(sec));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}