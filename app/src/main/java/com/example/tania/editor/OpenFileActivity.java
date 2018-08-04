package com.example.tania.editor;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;

public class OpenFileActivity extends ListActivity {

    private ArrayList<String> tmpFileList = new ArrayList<>();
    private ArrayList<File> tmpFileListFull = new ArrayList<>();
    Stack<File> filesStack = new Stack<>();
    private String localFileName = "";
    String tmpDirectory = Environment.getExternalStorageDirectory().getPath();
    TextView textView;
    private FileAdapter fileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.open);

        textView = findViewById(R.id.header);

        listDirectories(new File(tmpDirectory));
        Button openButton = findViewById(R.id.button_open);
        Button cancelButton = findViewById(R.id.button_cancel);


        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                localFileName = "";
                MainActivity.canceled = true;
                OpenFileActivity.super.onBackPressed();
            }
        });

        openButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localFileName.equals(""))
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_choose_file), Toast.LENGTH_LONG).show();
                else {
                    MainActivity.fileName = localFileName;
                    OpenFileActivity.super.onBackPressed();
                }
            }
        });
    }

    void listDirectories(File f) {
        tmpDirectory = f.getPath();
        textView.setText(tmpDirectory);
        filesStack.push(f);
        File[] files = f.listFiles();
        Arrays.sort(files, filesComparator);
        tmpFileList.clear();
        tmpFileListFull.clear();
        for (File file : files) {
            tmpFileList.add(file.getName());
            tmpFileListFull.add(file);

        }
        fileAdapter = new FileAdapter(tmpFileListFull, this, R.layout.open);
        ArrayAdapter<String> directoryList = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tmpFileList);
        setListAdapter(fileAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        fileAdapter.changeHighlighting(position);
        File thisFile = tmpFileListFull.get(position);
        if (thisFile.isDirectory()) {
            localFileName = "";
            listDirectories(thisFile);
        } else {
            localFileName = thisFile.getAbsolutePath();
        }
    }

    Comparator<? super File> filesComparator = new Comparator<File>() {
        public int compare(File a, File b) {
            return String.valueOf(a.getName()).compareTo(b.getName());
        }
    };

    @Override
    public void onBackPressed() {

        filesStack.pop();

        if (filesStack.empty())
            super.onBackPressed();
        else {
            File selected = filesStack.pop();
            listDirectories(selected);
        }

    }
}
