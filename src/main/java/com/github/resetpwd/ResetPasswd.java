package com.github.resetpwd;

import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @program: resetpwd
 * @description:
 * @author: jiangsk@inspur.com
 * @create: 2019-12-04 16:54
 **/
public class ResetPasswd {
    private static Logger logger = Logger.getLogger(ResetPasswd.class);
    public static void run()
    {
        String localFlag = FileUtil.getFlag();
        String flag=Metadata.getFlag();
        if(localFlag==null||flag==null||localFlag.equals(flag)){
            logger.info("no flag or already used");
        }else{
            String passwd = Metadata.getAdminpass();
            resetPwd(passwd);
            FileUtil.writeFlag(flag);
        }
    }


    public static void resetPwd(String password) {
        Runtime rt = Runtime.getRuntime();

        Process p = null;
        try {
            String[] cmds = {"/bin/sh", "-c", "echo root:" + password + "|chpasswd"};
            p = rt.exec(cmds);
            if (null != p) {
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    logger.error(e);
                }
                p.destroy();
                p = null;
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
