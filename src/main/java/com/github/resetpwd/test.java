package com.github.resetpwd;

import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.Date;

/**
 * @program: resetpwd
 * @description:
 * @author: jiangsk@inspur.com
 * @create: 2019-12-04 17:04
 **/
public class test {
    private static Logger logger = Logger.getLogger(AESUtil.class);


    @Test
    public void test2() throws Exception {
        try {
            //待加密内容
            String str = "385152jsk";
            AESUtil aesUtil = new AESUtil();
            String re = aesUtil.encrypt(str);
            System.out.println(re);
            String re2 = aesUtil.decrypt(re);
            System.out.println(re2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void wr() throws Exception {
        try {
            //待加密内容
            FileUtil.createFile("D:/a/b", "test.conf", "test");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void re() throws Exception {
        try {
            //待加密内容
            String re = FileUtil.readFileByBytes("D:/a/b/test.conf");
            System.out.println(re);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
