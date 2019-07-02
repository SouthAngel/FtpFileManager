package com.southangel.ftplib;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

public class Ftp{
    public String ip = "127.0.0.1";
    public int port = 21;
    public String userName = "user1";
    public String userPassword = "1231";
    FtpClient ftp;

    public Ftp() {
        this.ftp = FtpClient.create();
    }

    public int checkConnect() throws IOException, FtpProtocolException {
        if (ftp.isConnected()) return 1;
        ftp.connect(new InetSocketAddress(ip, port));
        if (ftp.isLoggedIn()) return 2;
        ftp.login(userName, userPassword.toCharArray());
        ftp.setBinaryType();
        return ftp.isConnected() ? 3 : 0;
    }

    public String getWorkingDirectory() throws IOException, FtpProtocolException {
        return ftp.getWorkingDirectory();
    }

    public List<String> list(String path) throws IOException, FtpProtocolException {
        List<String> fileNames = new ArrayList<>();
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(ftp.nameList(path)));
        String line;
        while ((line=bufReader.readLine())!=null){
            fileNames.add(line);
        }
        bufReader.close();
        return fileNames;
    }

    public List<String> list() throws IOException, FtpProtocolException {
        return list("");
    }

    public int download(String path1, String path2) throws IOException,FtpProtocolException{
        FileOutputStream fos = new FileOutputStream(path2);
        ftp.getFile(path1, fos);
        fos.close();
        return 1;
    }

    public int upload(String path1, String path2) throws IOException,FtpProtocolException{
        FileInputStream fis = new FileInputStream(path1);
        ftp.appendFile(path2, fis);
        fis.close();
        return 1;
    }

}


class Test{
    public static String ip = "127.0.0.1";
    public static int defaultPort = 21;
    public static String userName = "user1";
    public static String userPassword = "1231";

    public static void main(String[] args) throws IOException, FtpProtocolException, InterruptedException {
        System.out.println("Test begin");
        Ftp ft = new Ftp();
        System.out.println(ft.checkConnect());
        for (String s : ft.list()) {
            System.out.println(s);
        }
        System.out.println(ft.upload("a2.jpg", "a3.jpg"));
        System.out.println("Test end");

    }
}