package com.github.resetpwd;

/**
 * @program: resetpwd
 * @description:
 * @author: jiangsk@inspur.com
 * @create: 2019-12-10 15:19
 **/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceTest {
    private static Thread thread = null;
    private static Service service = null;

    /**
     * 退出服务方法(该方法必须有参数 String [] args)
     *
     * @param args
     */
    public static void StopService(String[] args) {
        System.out.println(service.getLocalTime() + "停止服务");
        service.setRunFlag(false);
    }

    /**
     * 启动服务方法(该方法必须有参数 String [] args)
     *
     * @param args
     */
    public static void StartService(String[] args) {
        // 产生服务线程
        service = new Service();
        thread = new Thread(service);
        System.out.println("\r\n" + service.getLocalTime() + "-----------启动服务-----------");

        try {
            // 将服务线程设定为用户线程，以避免StartService方法结束后线程退出
            thread.setDaemon(false);
            if (!thread.isDaemon()) {
                System.out.println(service.getLocalTime() + "成功设定线程为用户线程！");
            }
            // 启动服务线程
            thread.start();
        } catch (SecurityException se) {
            System.out.println(service.getLocalTime() + "线程类型设定失败！");
        }
    }

    public static void main(String[] args) {
        new Service().run();
//      ECService.StartService(null);
    }
}

class Service implements Runnable {
    private boolean runFlag = true;

    /**
     * 设定服务线程运行标志值
     *
     * @param runFlag
     */
    public synchronized void setRunFlag(boolean runFlag) {
        this.runFlag = runFlag;
    }

    /**
     * 取得服务线程运行标志值
     *
     * @param
     */
    @SuppressWarnings("unused")
    private synchronized boolean getRunFlag() {
        return runFlag;
    }

    @Override
    public void run() {
        System.out.println(getLocalTime() + "服务线程开始运行");
        // while (getRunFlag()) {
        // 获取当前计算机名和MAC
        String hostName = getHostName();
        if (hostName != null) {
            System.out.println(getLocalTime() + "计算机名获取成功！");
            // 获取本机mac地址
            String localMac = getMac();
            if (localMac != null) {
                System.out.println(getLocalTime() + "mac获取成功！");
                String mac = subMac(localMac);
                // 匹配本机计算机名与mac地址
                if (!hostName.equals(mac)) {
                    System.out.println(getLocalTime() + "开始执行修改计算机名称。");
                    // 执行修改计算机名
                    // 生成一个bat批处理文件
                    boolean isMakeBatSuccess = makeBat(mac);
                    if (isMakeBatSuccess == true) {
                        System.out.println(getLocalTime() + "生成bat成功！");
                        // 执行bat
                        runCMD("C:\\ECService\\modify_hostname.bat");
                        System.out.println(getLocalTime() + "执行bat完毕！");
                        // 删除bat
                        removeBat("C:\\ECService\\modify_hostname.bat");
                        System.out.println(getLocalTime() + "执行删除bat完毕！");
                        // 重启机器
                        runCMD("shutdown -r -t 0");
                        System.out.println(getLocalTime() + "执行修改计算机名操作完毕，系统正在重启。");
                    } else {
                        System.out.println(getLocalTime() + "生成bat失败！");
                    }
                } else {
                    System.out.println(getLocalTime() + "本机计算机名与mac地址一致，不需要修改计算机名。");
                }
            } else {
                System.out.println(getLocalTime() + "获取本机mac地址失败！");
            }
        } else {
            System.out.println(getLocalTime() + "获取本机计算机名失败！");
        }

        // }
        System.out.println(getLocalTime() + "服务线程结束运行");
    }

    /**
     * 获取计算机名称
     *
     * @return
     */
    public String getHostName() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String hostName = "";
            hostName = inetAddress.getHostName();
            return hostName;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(getLocalTime() + "获取计算机名称失败：" + e.getMessage());
        }
        return null;
    }

    /**
     * 获取MAC地址
     *
     * @return
     */
    public String getMac() {
        NetworkInterface byInetAddress;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            byInetAddress = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = byInetAddress.getHardwareAddress();
            return getMacFromBytes(hardwareAddress);
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(getLocalTime() + "获取mac地址失败：" + e.getMessage());
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(getLocalTime() + "获取mac地址失败：" + e.getMessage());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(getLocalTime() + "获取mac地址失败：" + e.getMessage());
        }
        return null;
    }

    public String subMac(String mac) {
        String r = mac.substring(9, mac.length());
        return r;
    }

    public String getMacFromBytes(byte[] bytes) {
        StringBuffer mac = new StringBuffer();
        byte currentByte;
        boolean first = false;
        for (byte b : bytes) {
            if (first) {
                mac.append("-");
            }
            currentByte = (byte) ((b & 240) >> 4);
            mac.append(Integer.toHexString(currentByte));
            currentByte = (byte) (b & 15);
            mac.append(Integer.toHexString(currentByte));
            first = true;
        }
        return mac.toString().toLowerCase();
    }

    public boolean makeBat(String hostname) {
        boolean isSuccess = false;
        File file = new File("C:\\ECService\\modify_hostname.bat");
        try {
            file.createNewFile();
            StringBuilder sb = appendBat(hostname);
            String str = sb.toString();
            byte bt[] = new byte[1024];
            bt = str.getBytes();
            FileOutputStream in;
            in = new FileOutputStream(file);
            in.write(bt, 0, bt.length);
            in.close();
            isSuccess = true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public StringBuilder appendBat(String hostname) {
        StringBuilder sb = new StringBuilder();
        sb.append("set cname=" + hostname);
        sb.append("\r\n");
        sb.append("echo REGEDIT4 >c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo [HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\ComputerName] >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo [HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\ComputerName\\ComputerName] >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo \"ComputerName\"=\"%cname%\" >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo [HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\ComputerName\\ActiveComputerName] >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo \"ComputerName\"=\"%cname%\" >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo [HKEY_LOCAL_MACHINE\\SYSTEM\\ControlSet002\\Control\\ComputerName\\ComputerName] >> c:\\windows\\reg.reg ");
        sb.append("\r\n");
        sb.append("echo \"ComputerName\"=\"%cname%\" >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo [HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services\\Tcpip\\Parameters] >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo \"NV Hostname\"=\"%cname%\" >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo \"Hostname\"=\"%cname%\" >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo [HKEY_USERS\\S-1-5-18\\Software\\Microsoft\\Windows\\ShellNoRoam] >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo @=\"%cname%\" >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo [HKEY_LOCAL_MACHINE\\SYSTEM\\ControlSet001\\Control\\ComputerName\\ActiveComputerName] >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo \"ComputerName\"=\"%cname%\" >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo [HKEY_LOCAL_MACHINE\\SYSTEM\\ControlSet001\\Services\\Tcpip\\Parameters] >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo \"NV Hostname\"=\"%cname%\" >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo \"Hostname\"=\"%cname%\" >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo [HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon] >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo \"DefaultDomainName\"=\"%cname%\" >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("echo \"AltDefaultDomainName\"=\"%cname%\" >> c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("regedit /s c:\\windows\\reg.reg");
        sb.append("\r\n");
        sb.append("exit");
        return sb;
    }

    public String runCMD(String commands) {
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec(commands);
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = errorReader.readLine()) != null) {
                result += line + "\n";
            }
            errorReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void removeBat(String path) {
        File file = new File(path);
        if (file.exists()) {
            boolean isflag = file.delete();
            if (isflag == true) {
                System.out.println(getLocalTime() + "删除bat成功！");
            } else {
                System.out.println(getLocalTime() + "删除bat失败！");
            }
        } else {
            System.out.println(getLocalTime() + "删除bat失败，批处理文件不存在！");
        }
    }

    public String getLocalTime() {
        String time = "[";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time += df.format(new Date()).toString();
        time += "]";
        return time;
    }

}