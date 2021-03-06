package com.github.resetpwd;
import org.apache.log4j.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AESUtil {
    private static Logger logger = Logger.getLogger(AESUtil.class);

    public String sKey = "1234567812345678";
    public String ivParameter = "1234567812345678";

    //
    public String encrypt(String sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = getsKey().getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(getIvParameter().getBytes());//
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] str = encoder.encode(encrypted);//
        return new String(str, UTF_8);
    }

    //
    public String decrypt(String sSrc) {
        try {
            byte[] raw = getsKey().getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(getIvParameter().getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] encrypted1 = decoder.decode(sSrc);//
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }





    public AESUtil() {
    }

    public AESUtil(String secretKey, String ivParameter) {
        this.sKey = secretKey;
        this.ivParameter = ivParameter;
    }

    public String getsKey() {
        return sKey;
    }

    public void setsKey(String sKey) {
        this.sKey = sKey;
    }

    public String getIvParameter() {
        return ivParameter;
    }

    public void setIvParameter(String ivParameter) {
        this.ivParameter = ivParameter;
    }

}