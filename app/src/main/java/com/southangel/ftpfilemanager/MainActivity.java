package com.southangel.ftpfilemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    enum FileListSelectMode {BROWSER, SELECT}
    enum NewEditConnectionMode{CREATE, EDIT}

    public interface FtpInfterface{
        boolean onUpload(String localPath, String ftpPath);
        boolean onDownload(String localPath, String ftpPath);
    }

    static abstract class UILink{

        public List<String> parentPath = new ArrayList<>();

        public abstract boolean onDownload(FileData d);
        public abstract boolean onUpload(FileData d);
        public abstract boolean onCreateNewConnection(ConnectionData d);
        public abstract void onListFiles(List<FileData> d, ConnectionData c, File parent);
        public abstract void onListConnections(List<ConnectionData> d);
    }

    static class FileData{
        enum Type{FILE, FOLDER}
        enum Status{LOCAL,CLOUD,LOCALANDCLOUD,DIFF}
        public String name = "name";
        public boolean selected = false;
        public Type type = Type.FILE;
        public Status status = Status.CLOUD;
    }

    static class ConnectionData{
        public String name = "local";
        public String host = "127.0.0.1";
        public int port = 21;
        public String user = "userA";
        public String password = "1231";
    }

    class FileListAdapter extends BaseAdapter{
        public FileListSelectMode mode = FileListSelectMode.BROWSER;

        class Holder{
            ImageView image;
            ImageView status;
            TextView text;
            CheckBox checkBox;
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
                h.image = convertView.findViewById(R.id.foImageView);
                h.status = convertView.findViewById(R.id.fosImageView);
                h.text = convertView.findViewById(R.id.foTextView);
                h.checkBox = convertView.findViewById(R.id.foCheckBox);
                convertView.setTag(h);
            } else {
                h = (Holder) (convertView.getTag());
            }
            h.checkBox.setVisibility(mode == FileListSelectMode.SELECT ? View.VISIBLE : View.GONE);
            h.checkBox.setChecked(d.selected);
            h.text.setText(d.name);
            setStatus(h.status, d);
            return convertView;
        }

        private void setStatus(ImageView v, FileData d){
            switch (d.status){
                case LOCALANDCLOUD:
                    v.setImageAlpha(0);
                    break;
                case LOCAL:
                    v.setImageAlpha(255);
                    v.setImageResource(R.drawable.ic_local);
                    break;
                case CLOUD:
                    v.setImageAlpha(255);
                    v.setImageResource(R.drawable.ic_cloud_black_24dp);
                    break;
                case DIFF:
                    v.setImageAlpha(255);
                    v.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                    break;
            }
        }
    }

    class ConListAdapter extends BaseAdapter{
        class Holder{
            ImageView image;
            TextView text;
            ImageButton button;
        }

        @Override
        public int getCount() {
            return conListArray.size();
        }

        @Override
        public Object getItem(int position) {
            return conListArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ConnectionData d = conListArray.get(position);
            Holder h;
            if (convertView==null){
                h = new Holder();
                convertView = LayoutInflater.from(context).inflate(R.layout.connections_item, parent, false);
                h.image = convertView.findViewById(R.id.conImageView);
                h.text = convertView.findViewById(R.id.conTextView);
//                h.button = convertView.findViewById(R.id.conButton);
                convertView.setTag(h);
            } else {
                h = (Holder) (convertView.getTag());
            }
            h.text.setText(String.format("%s\n%s:%s\n%s", d.name, d.host, d.port, d.user));
            return convertView;
        }
    }

    class DialogNewEditConnection{
        AlertDialog.Builder builder;
        AlertDialog dialog;
        NewEditConnectionMode mode = NewEditConnectionMode.CREATE;
        public ConnectionData conData = new ConnectionData();

        public DialogNewEditConnection(Context context) {
            builder = new AlertDialog.Builder(context);
            builder.setView(R.layout.create_connection_dialog);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    EditText tx;
                    tx = dialog.findViewById(R.id.newDialogName);
                    conData.name = tx.getText().toString();
                    tx = dialog.findViewById(R.id.newDialogHost);
                    conData.host = tx.getText().toString();
                    tx = dialog.findViewById(R.id.newDialogPort);
                    conData.port = Integer.parseInt(tx.getText().toString());
                    tx = dialog.findViewById(R.id.newDialogPassword);
                    conData.password = tx.getText().toString();
                    conListAdapter.notifyDataSetChanged();
                if (mode == NewEditConnectionMode.CREATE) conListView.smoothScrollToPosition(conListArray.size()-1);
                    System.out.println("OK");
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println("cancel");
                }
            });
            dialog = builder.create();
        }

        public void show(){
            dialog.show();
            EditText tx;
            tx = dialog.findViewById(R.id.newDialogName);
            tx.setText(conData.name);
            tx = dialog.findViewById(R.id.newDialogHost);
            tx.setText(conData.host);
            tx = dialog.findViewById(R.id.newDialogPort);
            tx.setText(String.valueOf(conData.port));
            tx = dialog.findViewById(R.id.newDialogPassword);
            tx.setText(conData.password);
            }
    }

    private Context context = this;
    private ImageButton listConnectionsButton;
    private ImageButton newConnectionButton;
    private ImageButton[] upDownButton = {null, null};
    private ImageButton returnParentButton;
    private UILink uiLink = new Test.UILink();
    private ListView filesListView;
    private List<FileData> filesListArray = new ArrayList<>();
    private FileListAdapter filesListAdapter = new FileListAdapter();
    private ListView conListView;
    private List<ConnectionData> conListArray = new ArrayList<>();
    private ConListAdapter conListAdapter = new ConListAdapter();
    private CheckBox switchSelectAbleButton;
    private DialogNewEditConnection connectionDialog;
    private Object temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        matchId();
        bindListener();
        test();
    }

    private void init(){
        connectionDialog = new DialogNewEditConnection(context);
    }

    private void matchId(){
        listConnectionsButton = findViewById(R.id.dockButton);
        newConnectionButton = findViewById(R.id.newConButton);
        filesListView = findViewById(R.id.filesListView);
        conListView = findViewById(R.id.connectionsListView);
        switchSelectAbleButton = findViewById(R.id.switchSelectAbleButton);
        upDownButton[0] = findViewById(R.id.uploadButton);
        upDownButton[1] = findViewById(R.id.downloadButton);
        returnParentButton = findViewById(R.id.returnParentButton);
    }

    private void bindListener(){
        listConnectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout dl = ((Activity)context).findViewById(R.id.drawerLayout);
                dl.openDrawer(Gravity.LEFT, true);
            }
        });
        switchSelectAbleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                filesListAdapter.mode = b ? FileListSelectMode.SELECT : FileListSelectMode.BROWSER;
                if (filesListAdapter.mode == FileListSelectMode.SELECT) {
                    for (FileData fileData : filesListArray) {
                        fileData.selected = false;
                    }
                }
                filesListAdapter.notifyDataSetChanged();
            }
        });
        newConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectionDialog.mode = NewEditConnectionMode.CREATE;
                ConnectionData data = new ConnectionData();
                conListArray.add(data);
                connectionDialog.conData = data;
                connectionDialog.show();
            }
        });
        upDownButton[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("upload", "click");
            }
        });
        upDownButton[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("download", "click");
            }
        });
        returnParentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", "return parent");
            }
        });
        filesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("item", "click");
                if (filesListAdapter.mode == FileListSelectMode.SELECT) {
                    FileData d = filesListArray.get(i);
                    d.selected = !d.selected;
                    filesListAdapter.notifyDataSetChanged();
                }
            }
        });
        filesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("item", "long click");
                filesListArray.get(i).selected = false;
                switchSelectAbleButton.setChecked(true);
                return false;
            }
        });
        conListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("click", "connections");
            }
        });
        conListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("LongClick", "connections");
                System.out.println(position);
                System.out.println(id);
                PopupMenu pm = new PopupMenu(context, view);
                Menu m = pm.getMenu();
                MenuItem tm;
                tm = m.add("set");
                connectionDialog.mode = NewEditConnectionMode.EDIT;
                connectionDialog.conData = conListArray.get(position);
                tm.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        connectionDialog.show();
                        return false;
                    }
                });
                tm = m.add("sortHome");
                tm = m.add("sortEnd");
                tm = m.add("delete");
                pm.show();
                return false;
            }
        });
    }

    private void test(){
        uiLink = new Test.UILink();
        uiLink.onListFiles(filesListArray, new ConnectionData(), new File("/"));
        uiLink.onListConnections(conListArray);
        filesListView.setAdapter(filesListAdapter);
        conListView.setAdapter(conListAdapter);
    }
}

class Test{
    public static class UILink extends MainActivity.UILink{
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
            MainActivity.FileData data;
            MainActivity.FileData.Status[] map = {
                    MainActivity.FileData.Status.CLOUD,
                    MainActivity.FileData.Status.LOCAL,
                    MainActivity.FileData.Status.LOCALANDCLOUD,
                    MainActivity.FileData.Status.DIFF,
            };
            for (int i = 0; i < 32; i++) {
                data = new MainActivity.FileData();
                data.name = "name_" + i;
                data.status = map[i%map.length];
                d.add(data);
            }
        }

        @Override
        public void onListConnections(List<MainActivity.ConnectionData> d) {
            MainActivity.ConnectionData data;
            for (int i = 0; i < 32; i++) {
                data = new MainActivity.ConnectionData();
                data.name = "name_" + i;
                d.add(data);
            }
        }
    }
}
