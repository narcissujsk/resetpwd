package com.github.resetpwd;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public static String executeCmd(String cmd) {
        String re = null;
        Runtime rt = Runtime.getRuntime(); // 运行时系统获取
        Map<String, String> lineMap = new HashMap<String, String>();//存放返回值
        try {
            Process proc = rt.exec(cmd);// 执行命令
            InputStream stderr = proc.getInputStream();//执行结果 得到进程的标准输出信息流
            InputStreamReader isr = new InputStreamReader(stderr);//将字节流转化成字符流
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
