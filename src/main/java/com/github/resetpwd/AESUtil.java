package com.github.resetpwd;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.log4j.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AESUtil {
    private static Logger logger = Logger.getLogger(AESUtil.class);
    /**
     * 加密用的Key 可以用26个字母和数字组成
     * 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    public  String sKey="1234567812345678" ;
    public  String ivParameter="1234567812345678";

    // 加密
    public  String encrypt(String sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = getsKey().getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(getIvParameter().getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] str = encoder.encode(encrypted);//此处使用BASE64做转码。
        return new String(str, UTF_8);
    }

    // 解密
    public  String decrypt(String sSrc) {
        try {
            byte[] raw = getsKey().getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(getIvParameter().getBytes());
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

    public String getUuidAlone()
    {
        String mata_data = curl("GET", "http://169.254.169.254/openstack/latest/meta_data.json");
        if (mata_data == null) {
            return null;
        }
        String uuid = null;
        try
        {
            JSONObject mataDataJson = new JSONObject(mata_data);
            uuid = mataDataJson.getString("uuid");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return uuid;
    }
    public String curl(String type, String url)
    {
        URL urlOb = null;
        try
        {
            urlOb = new URL(url);
        }
        catch (MalformedURLException e)
        {
            logger.error("curl new URL() exception", e);
            return null;
        }
        try
        {
            if (urlOb == null) {
                return null;
            }
            HttpURLConnection conn = (HttpURLConnection)urlOb.openConnection();
            conn.setRequestMethod(type);
            if ("GET".equals(type))
            {
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(2000);
                InputStream inStream = conn.getInputStream();
                int count = 0;
                while (count == 0) {
                    count = inStream.available();
                }
                byte[] bytes = new byte[count];
                inStream.read(bytes, 0, inStream.available());
                return new String(bytes);
            }
            if ("POST".equals(type))
            {
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.connect();

                PrintWriter out = new PrintWriter(conn.getOutputStream());

                out.write("True");

                out.flush();
                int responseCode = conn.getResponseCode();
                logger.info("responseCode:." + responseCode + "..............................");
                logger.info("post...............................");
                return null;
            }
            if ("DELETE".equals(type))
            {
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                int responseCode = conn.getResponseCode();
                logger.info("responseCode:." + responseCode + "..............................");
                logger.info("delete...............................");
                return null;
            }
        }
        catch (IOException e)
        {
            logger.error("curl exception", e);
            return null;
        }
        return null;
    }
    public void resetPwd(String password)
    {
        Runtime rt = Runtime.getRuntime();

        Process p = null;
        try
        {
            String[] cmds = { "/bin/sh", "-c", "echo root:" + password + "|chpasswd" };
            p = rt.exec(cmds);
            if (null != p)
            {
                try
                {
                    p.waitFor();
                }
                catch (InterruptedException e)
                {
                    logger.error(e);
                }
                p.destroy();
                p = null;
            }
        }
        catch (IOException e)
        {
            logger.error(e);
        }
    }

    public AESUtil() {
    }
    public AESUtil(String secretKey,String ivParameter ) {
        this.sKey=secretKey;
        this.ivParameter=ivParameter;
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