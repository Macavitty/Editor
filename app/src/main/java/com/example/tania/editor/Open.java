package com.example.tania.editor;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class Open extends ListActivity{

    private List<String> fileList = new ArrayList<>();
    Stack<File> filesStack = new Stack<>();
    private String localFileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.open);

        listDirectories(new File(Environment.getExternalStorageDirectory().getPath()));

        Button openButton = findViewById(R.id.button_open);
        Button cancelButton = findViewById(R.id.button_cancel);


        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                localFileName = "";
                Main.canceled = true;
                Open.super.onBackPressed();
            }
        });

        openButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localFileName.equals("")) Toast.makeText(getApplicationContext(),"Выберите файл." , Toast.LENGTH_LONG).show();
                else{
                    Main.fileName = localFileName;
                    Open.super.onBackPressed(); // may not work
                }
            }
        });
    }

    void listDirectories(File f){

        filesStack.push(f);
        File[] files = f.listFiles();
        Arrays.sort(files, filesComparator);
        fileList.clear();
        for (File file : files){
            fileList.add(file.getPath());
        }

        ArrayAdapter<String> directoryList = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
        setListAdapter(directoryList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File thisFile = new File(fileList.get(position));
        if(thisFile.isDirectory()){
            localFileName = "";
            listDirectories(thisFile);
        }
        else{
            localFileName = thisFile.getAbsolutePath();
        }
    }

    Comparator<? super File> filesComparator = new Comparator<File>(){
        public int compare(File a, File b) {
            return String.valueOf(a.getName()).compareTo(b.getName());
        }
    };

    @Override
    public void onBackPressed() {

        filesStack.pop();

        if(filesStack.empty())
            super.onBackPressed();
        else{
            File selected = filesStack.pop();
            listDirectories(selected);
        }

    }
}
