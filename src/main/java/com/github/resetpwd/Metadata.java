package com.github.resetpwd;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @program: resetpwd
 * @description:
 * @author: jiangsk@inspur.com
 * @create: 2019-12-05 19:27
 **/
public class Metadata {
    private static Logger logger1 = Logger.getLogger(Metadata.class);
    public static String curl(String type, String url) {
        URL urlOb = null;
        try {
            urlOb = new URL(url);
        } catch (MalformedURLException e) {
            //logger.error("curl new URL() exception", e);
            return null;
        }
        try {
            if (urlOb == null) {
                return null;
            }
            HttpURLConnection conn = (HttpURLConnection) urlOb.openConnection();
            conn.setRequestMethod(type);
            if ("GET".equals(type)) {
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
            if ("POST".equals(type)) {
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.connect();

                PrintWriter out = new PrintWriter(conn.getOutputStream());

                out.write("True");

                out.flush();
                int responseCode = conn.getResponseCode();
                //logger.info("responseCode:." + responseCode + "..............................");
                //logger.info("post...............................");
                return null;
            }
            if ("DELETE".equals(type)) {
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                int responseCode = conn.getResponseCode();
               // logger.info("responseCode:." + responseCode + "..............................");
               // logger.info("delete...............................");
                return null;
            }
        } catch (IOException e) {
            //logger.error("curl exception", e);
            return null;
        }
        return null;
    }

    public static String getUuidAlone() {
        String mata_data = curl("GET", "http://169.254.169.254/openstack/latest/meta_data.json");
        if (mata_data == null) {
            return null;
        }
        String uuid = null;
        try {
            JSONObject mataDataJson = new JSONObject(mata_data);
            uuid = mataDataJson.getString("uuid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return uuid;
    }
    public static JSONObject getMetadata() {
        String mata_data = curl("GET", "http://169.254.169.254/openstack/latest/meta_data.json");
        if (mata_data == null) {
            return null;
        }
        JSONObject mataDataJson  = null;
        try {
             mataDataJson = new JSONObject(mata_data);

        } catch (JSONException e) {

        }
        return mataDataJson;
    }
    public static String getAdminpass() {
        String mata_data = curl("GET", "http://169.254.169.254/openstack/latest/meta_data.json");
      //  logger.info("mata_data: " + mata_data );
        if (mata_data == null) {
            return null;
        }
        String admin_pass = null;
        try {
            JSONObject mataDataJson = new JSONObject(mata_data);
            JSONObject meta = mataDataJson.getJSONObject("meta");
            if(meta.has("admin_pass")){
                admin_pass = meta.getString("admin_pass");
            }else{
                admin_pass = null;
            }
           // logger.info("meta: " + meta);

            //logger.info("admin_pass: " + admin_pass);
        } catch (JSONException e) {

        }
        return admin_pass;
    }
    public static String getFlag() {
       String mata_data = curl("GET", "http://169.254.169.254/openstack/latest/meta_data.json");
       // logger.info("mata_data: " + mata_data );
       if (mata_data == null) {
            return null;
        }
        String reset_passwd_flag = null;
        try {
            JSONObject mataDataJson = new JSONObject(mata_data);
            JSONObject meta = mataDataJson.getJSONObject("meta");
            if(meta.has("reset_passwd_flag")){
                reset_passwd_flag = meta.getString("reset_passwd_flag");
            }else{
                reset_passwd_flag = null;
            }
           // logger.info("meta: " + meta + "..............................");
           // logger.info("reset_passwd_flag: " + reset_passwd_flag );
        } catch (JSONException e) {

        }
        return reset_passwd_flag;
    }
}
