package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018/4/24 0024.
 */
public class FtpUtil {
    private static String ftpIp = PropertiesUtil.getPropertie("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getPropertie("ftp.user");
    private static String ftpPass= PropertiesUtil.getPropertie("ftp.pass");
    private static Logger logger  = LoggerFactory.getLogger(FtpUtil.class);
    private String ip;
    private Integer port;
    private String user;
    private String passWord;
    private FTPClient ftpClient;

    public FtpUtil(String ip, Integer port, String user, String passWord) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.passWord = passWord;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException {//抛出异常，然后让业务层处理
        FtpUtil ftpUtil = new FtpUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始链接ftp服务器");
        boolean result =     ftpUtil.uploadFile("img",fileList);
        logger.info("开始链接ftp服务器 链接成功--");
        return result;
    }
    private boolean uploadFile(String remotePath,List<File> fileList) throws  IOException
    {
        boolean unupload = false;
        FileInputStream inputStream = null;
        if(connectFtpServer(this.ip,this.port,this.user,this.passWord)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                for(File file :fileList)
                {
                    inputStream = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(),inputStream);


                }
                unupload  = true;
            } catch (IOException e) {
                logger.error("上传文件异常");
            }finally {

                inputStream.close();
                ftpClient.disconnect();//释放资源
            }
        }
        return unupload;
    }
    private boolean connectFtpServer(String ip,int port,String user,String passWord)
    {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            ftpClient.login(user,passWord);
            isSuccess = true;
        }catch (Exception e)
        {
            logger.error("连接失败",e);
        }
        return isSuccess;
    }
    public static String getFtpIp() {
        return ftpIp;
    }

    public static void setFtpIp(String ftpIp) {
        FtpUtil.ftpIp = ftpIp;
    }

    public static String getFtpUser() {
        return ftpUser;
    }

    public static void setFtpUser(String ftpUser) {
        FtpUtil.ftpUser = ftpUser;
    }

    public static String getFtpPass() {
        return ftpPass;
    }

    public static void setFtpPass(String ftpPass) {
        FtpUtil.ftpPass = ftpPass;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
