package com.github.resetpwd;

/**
 * @program: resetpwd
 * @description:
 * @author: jiangsk@inspur.com
 * @create: 2019-12-05 16:22
 **/
public class OSUtil {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static  String getOsName(){
        String osname = System.getProperty("os.name").toLowerCase();
        return osname;
    }
    public static  boolean isWindows(){
        String osname = System.getProperty("os.name").toLowerCase();
        return osname.contains("windows");
    }
}
