package com.github.resetpwd;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Map;

/**
 * @program: resetpwd
 * @description:
 * @author: jiangsk@inspur.com
 * @create: 2019-12-05 09:55
 **/
public class Main {
    private static Logger logger = Logger.getLogger(AESUtil.class);

    public static void main(String[] args) {
        logger.info("CloudResetPwdAgent start..........");
        //System.out.println(System.getProperty("file.encoding"));
        //String re = WindowsUtil.executeCmd("net user administrator passwd");
        //logger.info(re);
        try {
          //  logger.info("log");
            for (int i = 0; i < 9; i++) {
                logger.info("log"+ new Date().toString());
            }
            while(true){
                Thread.sleep(3000);

                logger.info("CloudResetPwdAgent start..........");
               // ResetPasswd.run();
              // logger.info("agent run : "+ new Date().toString());
            }
        } catch (Exception e) {
            logger.error(e);
        }
        //logger.info("CloudResetPwdAgent finish..........");
    }
}
