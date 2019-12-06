package com.github.resetpwd;

import org.apache.log4j.Logger;

import java.util.Date;

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
        try {
          //  logger.info("log");
            for (int i = 0; i < 9; i++) {
               // logger.info("log"+ new Date().toString());
            }
            while(true){
                Thread.sleep(3000);
                ResetPasswd.run();
               logger.info("agent run : "+ new Date().toString());
            }
        } catch (Exception e) {
            logger.error(e);
        }
        //logger.info("CloudResetPwdAgent finish..........");
    }
}
