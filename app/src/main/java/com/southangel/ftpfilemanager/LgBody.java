package com.southangel.ftpfilemanager;

import android.os.Environment;

import com.southangel.ftplib.Ftp;

import java.io.File;
import java.util.List;

public class LgBody {
    public class UILink extends MainActivity.UILink {
        @Override
        public boolean onDownload(MainActivity.FileData d) {
            return false;
        }

        @Override
        public boolean onUpload(MainActivity.FileData d) {
            return false;
        }

        @Override
        public boolean onCreateNewConnection(MainActivity.ConnectionData d) {
            return false;
        }

        @Override
        public void onListFiles(List<MainActivity.FileData> d, MainActivity.ConnectionData c, File parent) {

        }

        @Override
        public void onListConnections(List<MainActivity.ConnectionData> d) {

        }
    }

    public String mapFolder = Environment.getExternalStorageDirectory() + "/ftpfilemanager";
    private String parent = ".";
    private MainActivity.UILink uiLink;
    private Ftp ftp;

    public LgBody() {
        this.ftp = new Ftp();
    }
}
