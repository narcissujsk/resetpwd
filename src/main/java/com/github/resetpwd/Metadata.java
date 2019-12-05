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
    private static Logger logger = Logger.getLogger(Metadata.class);
    public String curl(String type, String url) {
        URL urlOb = null;
        try {
            urlOb = new URL(url);
        } catch (MalformedURLException e) {
            logger.error("curl new URL() exception", e);
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
                logger.info("responseCode:." + responseCode + "..............................");
                logger.info("post...............................");
                return null;
            }
            if ("DELETE".equals(type)) {
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                int responseCode = conn.getResponseCode();
                logger.info("responseCode:." + responseCode + "..............................");
                logger.info("delete...............................");
                return null;
            }
        } catch (IOException e) {
            logger.error("curl exception", e);
            return null;
        }
        return null;
    }

    public String getUuidAlone() {
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
    public JSONObject getMetadata() {
        String mata_data = curl("GET", "http://169.254.169.254/openstack/latest/meta_data.json");
        if (mata_data == null) {
            return null;
        }
        JSONObject mataDataJson  = null;
        try {
             mataDataJson = new JSONObject(mata_data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mataDataJson;
    }
    public String getAdminpass() {
        String mata_data = curl("GET", "http://169.254.169.254/openstack/latest/meta_data.json");
        if (mata_data == null) {
            return null;
        }
        String admin_pass = null;
        try {
            JSONObject mataDataJson = new JSONObject(mata_data);
            JSONObject meta = mataDataJson.getJSONObject("meta");
            admin_pass = meta.getString("admin_pass");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return admin_pass;
    }
    public String getFlag() {
        String mata_data = curl("GET", "http://169.254.169.254/openstack/latest/meta_data.json");
        if (mata_data == null) {
            return null;
        }
        String resetPasswdKey = null;
        try {
            JSONObject mataDataJson = new JSONObject(mata_data);
            JSONObject meta = mataDataJson.getJSONObject("meta");
            resetPasswdKey = meta.getString("reset-passwd-key");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resetPasswdKey;
    }
}
