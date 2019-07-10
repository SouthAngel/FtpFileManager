package com.southangel.ftplib;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.crypto.Data;

import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpDirEntry;
import sun.net.ftp.FtpProtocolException;

public class Ftp{
    public enum FileType{
        FILE, DIR
    }
    class FileInfo{
        String name;
        String path;
        FileType type = FileType.FILE;
        Date data;
        Long size;
    }

    public String ip = "127.0.0.1";
    public int port = 21;
    public String userName = "user1";
    public String userPassword = "1231";
    FtpClient ftp;

    public Ftp(){
        initialize();
    }

    private void initialize(){
        ftp = FtpClient.create();
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

    public List<FileInfo> listFiles(String path) throws IOException, FtpProtocolException {
        List<FileInfo> files = new ArrayList<>();
        Iterator<FtpDirEntry> itf = ftp.listFiles(path);
        while (itf.hasNext()){
            FtpDirEntry fde = itf.next();
            FileInfo f = new FileInfo();
            f.name = fde.getName();
            f.data = fde.getLastModified();
            f.size = fde.getSize();
            FtpDirEntry.Type type = fde.getType();
            if (type == FtpDirEntry.Type.FILE || type == FtpDirEntry.Type.LINK){
                f.type = FileType.FILE;
            } else {
                f.type = FileType.DIR;
            }
            files.add(f);
        }
        return files;
    }

    public List<FileInfo> listFiles() throws IOException, FtpProtocolException {
        return listFiles("");
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
        for (Ftp.FileInfo file : ft.listFiles()) {
            System.out.println(file.data);
        }
//        System.out.println(ft.upload("a2.jpg", "a3.jpg"));
        System.out.println("Test end");

    }
}