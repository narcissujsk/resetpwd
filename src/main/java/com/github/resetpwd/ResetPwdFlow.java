package com.github.resetpwd;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;


/**
 * @program: resetpwd
 * @description:
 * @author: jiangsk@inspur.com
 * @create: 2019-12-04 17:40
 **/
public class ResetPwdFlow {
    private static Logger logger = LogManager.getLogger(ResetPwdFlow.class);
    private static final String RESET_PWD_FLAG_URL = "http://169.254.169.254/openstack/latest/resetpwd_flag";
    private static final String RESET_PASSWORD_URL = "http://169.254.169.254/openstack/latest/reset_password";
    private static final String MATA_DAYA_URL = "http://169.254.169.254/openstack/latest/meta_data.json";
    private static final String UUID = "uuid";
    private static final String RESET_PASSWORD = "reset_password";
    private static final String RESET_PWD_FLAG = "resetpwd_flag";
    private static final long FLAG_REQUEST_INTERVAL_TIMES = 2000L;
    private static final long PASSWORD_REQUEST_INTERVAL_TIMES = 2000L;
    public static final String KEY_ALGORITHM = "AES";
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String RESET_PWD_CMD_WINDOWS = "net user Administrator ";
    private static final String RESET_PWD_CMD_LINUX_FRONT = "echo 'root:";
    private static final String RESET_PWD_CMD_LINUX_TAIL = "' | chpasswd";

    public void run()
    {
        String flag = getFlagAlone();
        if ((flag == null) || (!"True".equals(flag))) {
            setFlag();
        }
        String resetPassword = getPWD();
        if ((resetPassword == null) || ("None".equals(resetPassword)) || (resetPassword.length() == 0)) {
            return;
        }
        String uuid = getUuid();
        if (uuid == null) {
            return;
        }
        byte[] key = getAesKey(uuid, resetPassword);
        if (key == null) {
            return;
        }
        String decryptedPassword = resetPassword.substring(12, resetPassword.length());
        String password = aesCbcDecrypt(decryptedPassword, key, key);


        resetVMPwd(password);


        deleteResetPwd();
    }

    private void deleteResetPwd()
    {
        for (int i = 0; i < 10; i++) {
            try
            {
                curl("DELETE", "http://169.254.169.254/openstack/latest/reset_password");
                Thread.sleep(2000L);
                String resetPwd = getResetPwdAlone();
                if ((resetPwd == null) || (resetPwd.length() == 0)) {
                    return;
                }
            }
            catch (InterruptedException e)
            {
                logger.error(e);
            }
        }
    }

    public String getUuid()
    {
        String result = null;
        String resultUuid = getUuidAlone();
        if ((resultUuid == null) || ("Fail".equals(resultUuid)))
        {
            for (int i = 0; i < 10; i++) {
                try
                {
                    Thread.sleep(2000L);
                    resultUuid = getUuidAlone();
                    if ((resultUuid != null) && (!"Fail".equals(resultUuid))) {
                        return resultUuid;
                    }
                }
                catch (InterruptedException e)
                {
                    logger.error("getUuid error", e);
                }
            }
            logger.info("GetUuid failed .....................");
            return null;
        }
        result = resultUuid;
        return result;
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

    private void resetVMPwd(String password)
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

    public byte[] getAesKey(String uuid, String encryptedData)
    {
        int iterations = 5000;
        String salt = getSault(encryptedData);
        System.out.println(salt);
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            byte[] saltBytes = decoder.decodeBuffer(salt);
            PBEKeySpec spec = new PBEKeySpec(uuid.toCharArray(), saltBytes, iterations, 128);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        }
        catch (IOException e)
        {
            logger.error(e);
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error(e);
        }
        catch (InvalidKeySpecException e)
        {
            logger.error(e);
        }
        return null;
    }

    private String getSault(String encryptedData)
    {
        if ((encryptedData == null) || (encryptedData.length() < 12)) {
            return null;
        }
        return encryptedData.substring(0, 12);
    }

    public String aesCbcDecrypt(String encryptedData, byte[] key, byte[] iv)
    {
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            byte[] passData = decoder.decodeBuffer(encryptedData);
            Key scretkey = convertToKey(key);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(2, scretkey, new IvParameterSpec(iv));
            byte[] result = cipher.doFinal(passData);
            return new String(result);
        }
        catch (IOException e)
        {
            logger.error(e);
            return null;
        }
        catch (Exception e)
        {
            logger.error(e);
        }
        return null;
    }

    public static Key convertToKey(byte[] keyBytes)
            throws Exception
    {
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        return secretKey;
    }

    private String getPWD()
    {
        String result = null;
        String resetPwd = getResetPwdAlone();
        if ((resetPwd == null) || ("Fail".equals(resetPwd)))
        {
            for (int i = 0; i < 10; i++) {
                try
                {
                    Thread.sleep(2000L);
                    resetPwd = getResetPwdAlone();
                    if ((resetPwd != null) && (!"Fail".equals(resetPwd))) {
                        return resetPwd;
                    }
                }
                catch (InterruptedException e)
                {
                    logger.error("getPWD error", e);
                }
            }
            return null;
        }
        result = resetPwd;
        return result;
    }

    public String getResetPwdAlone()
    {
        String resetPwdJsonStr = curl("GET", "http://169.254.169.254/openstack/latest/reset_password");
        if (resetPwdJsonStr == null) {
            return null;
        }
        String resetPassword = null;
        try
        {
            JSONObject resetPwdJson = new JSONObject(resetPwdJsonStr);
            resetPassword = resetPwdJson.getString("reset_password");
            if (resetPassword == null) {
                return null;
            }
            return resetPassword;
        }
        catch (JSONException e)
        {
            logger.error("getResetPwdAlone error,", e);
        }
        return null;
    }

    public void setFlag()
    {
        String flag = null;
        for (int i = 0; i < 10; i++)
        {
            curl("POST", "http://169.254.169.254/openstack/latest/resetpwd_flag");
            try
            {
                Thread.sleep(2000L);
                flag = getFlagAlone();
                if ("True".equals(flag)) {
                    return;
                }
            }
            catch (InterruptedException e)
            {
                logger.error("set flag error,", e);
            }
        }
    }

    public String getFlag()
    {
        String result = null;
        String resultFlag = getFlagAlone();
        if ((resultFlag == null) || ("Fail".equals(resultFlag)))
        {
            for (int i = 0; i < 10; i++) {
                try
                {
                    Thread.sleep(2000L);
                    resultFlag = getFlagAlone();
                    if ((resultFlag != null) && (!"Fail".equals(resultFlag))) {
                        return resultFlag;
                    }
                }
                catch (InterruptedException e)
                {
                    logger.error("getFlag error", e);
                }
            }
            return null;
        }
        result = resultFlag;
        return result;
    }

    public String getFlagAlone()
    {
        String flgJsonStr = curl("GET", "http://169.254.169.254/openstack/latest/resetpwd_flag");
        if (flgJsonStr == null) {
            return null;
        }
        String flag = null;
        try
        {
            JSONObject flagJson = new JSONObject(flgJsonStr);
            flag = flagJson.getString("resetpwd_flag");
            if (flag == null) {
                return null;
            }
            return flag;
        }
        catch (JSONException e)
        {
            logger.error("getFlagAlone error:", e);
        }
        return null;
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
}
