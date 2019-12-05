package com.github.resetpwd;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * @program: resetpwd
 * @description:
 * @author: jiangsk@inspur.com
 * @create: 2019-12-05 09:27
 **/
public class FileUtil {
    private static Logger logger = Logger.getLogger(AESUtil.class);

    public static  void createFile(String filePath,String fileName,String content ) throws IOException {

        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File checkFile = new File(filePath + "/"+fileName);
        FileWriter writer = null;
        try {

            if (!checkFile.exists()) {
                checkFile.createNewFile();
            }
            writer = new FileWriter(checkFile, false);
            writer.append(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }
    public static String readFileByBytes(String fileName) throws IOException {
        File file = new File(fileName);
        InputStream in = null;
        StringBuffer sb = new StringBuffer();


        if (file.isFile() && file.exists()) {
            byte[] tempbytes = new byte[1024];
            int byteread = 0;
            in = new FileInputStream(file);
            while ((byteread = in.read(tempbytes)) != -1) {
                //  System.out.write(tempbytes, 0, byteread);
                String str = new String(tempbytes, 0, byteread);
                sb.append(str);
            }
        } else {
            logger.info("找不到指定的文件，请确认文件路径是否正确");
        }
        return sb.toString();
    }
}
