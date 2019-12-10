package com.github.resetpwd;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: resetpwd
 * @description:
 * @author: jiangsk@inspur.com
 * @create: 2019-12-09 14:33
 **/
public class WindowsUtil {
    private static Logger logger = Logger.getLogger(AESUtil.class);

    //ShellExecute(0,"runas", LPCSTR("cmd.exe"),LPCSTR("/c net user administrator /active:yes"),"",SW_HIDE);
//runas /noprofile /user:administrator\administrator "cmd /c \"net user administrator /active:yes\""
    //ShellExecute(0,"runas","cmd","","",1);
    public void setPasswd() {

    }

    public static String executeCmd(String cmd) throws UnsupportedEncodingException {
        String re = null;
        Runtime rt = Runtime.getRuntime(); //
        Map<String, String> lineMap = new HashMap<String, String>();//
        try {
            Process proc = rt.exec(cmd);//
            InputStream stderr = proc.getInputStream();//
            InputStreamReader isr = new InputStreamReader(stderr);//
           Process process = Runtime.getRuntime().exec(cmd);
           BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            StringBuilder sb=new StringBuilder();
            while ((line = br.readLine()) != null) {
                if (!(line==null||line.isEmpty())) {
                    sb.append(line);
                }
            }
            re=sb.toString();
            br.close();
            isr.close();
            stderr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
       String _str = new String(re.getBytes("utf-8"),"GBK" );
        logger.info(_str);

        return re;
    }
    public static boolean runCMD(String cmd) throws IOException, InterruptedException {
        final String METHOD_NAME = "runCMD";

        // Process p = Runtime.getRuntime().exec("cmd.exe /C " + cmd);
        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader br = null;
        try {
            // br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String readLine = br.readLine();
            StringBuilder builder = new StringBuilder();
            while (readLine != null) {
                readLine = br.readLine();
                builder.append(readLine);
            }
            logger.debug(METHOD_NAME + "#readLine: " + builder.toString());

            p.waitFor();
            int i = p.exitValue();
            logger.info(METHOD_NAME + "#exitValue = " + i);
            if (i == 0) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            logger.error(METHOD_NAME + "#ErrMsg=" + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }
}
