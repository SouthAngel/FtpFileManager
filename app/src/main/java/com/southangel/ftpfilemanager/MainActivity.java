package com.southangel.ftpfilemanager;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private Context context = this;
    private ImageButton listConnectionsButton;
    private UILink uiLink = new Test.UILink();
    private ListView filesListView;
    private List<FileData> filesListArray = new ArrayList<>();
    private ListAdapter filesListAdapter;
    private ListView conListView;
    private List<ConnectionData> conListArray = new ArrayList<>();
    private ListAdapter conListAdapter;

    public interface UILink{
        void onCreate();
        void onListFiles(List<FileData> d);
        void onListConnections(List<ConnectionData> d);
    }

    static class FileData{
        enum Type{FILE, FOLDER}
        public String name = "name";
        public boolean selected = false;
        public Type type = Type.FILE;
    }

    static class ConnectionData{
        public String host = "127.0.0.1";
        public long port = 21;
    }

    class FileListAdapter extends BaseAdapter{
        class Holder{
            public ImageButton imageButton;
            public TextView textView;
            public CheckBox checkBox;
        }
        @Override
        public int getCount() {
            return filesListArray.size();
        }

        @Override
        public Object getItem(int position) {
            return filesListArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FileData d = filesListArray.get(position);
            Holder h;
            if (convertView==null){
                h = new Holder();
                convertView = LayoutInflater.from(context).inflate(R.layout.file_option_item, parent, false);
                h.imageButton = convertView.findViewById(R.id.foImageButton);
                h.textView = convertView.findViewById(R.id.foTextView);
                h.checkBox = convertView.findViewById(R.id.foCheckBox);
                convertView.setTag(h);
            } else {
                h = (Holder) (convertView.getTag());
            }
            h.textView.setText(d.name);
            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        matchId();
        bindListener();
        test();
    }

    private void matchId(){
        listConnectionsButton = findViewById(R.id.dockButton);
        filesListView = findViewById(R.id.filesListView);
    }

    private void bindListener(){
        listConnectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout dl = ((Activity)context).findViewById(R.id.drawerLayout);
                dl.openDrawer(Gravity.LEFT, true);
            }
        });
    }

    private void test(){
        uiLink = new Test.UILink();
        uiLink.onListFiles(filesListArray);
        filesListAdapter = new FileListAdapter();
        filesListView.setAdapter(filesListAdapter);
    }
}

class Test{
    public static class UILink implements MainActivity.UILink{
        @Override
        public void onCreate() {

        }

        @Override
        public void onListFiles(List<MainActivity.FileData> d) {
            MainActivity.FileData data;
            for (int i = 0; i < 32; i++) {
                data = new MainActivity.FileData();
                data.name = "name_" + i;
                d.add(data);
            }
        }

        @Override
        public void onListConnections(List d) {

        }
    }
}
