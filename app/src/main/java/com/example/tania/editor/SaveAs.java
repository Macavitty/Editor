package com.example.tania.editor;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class SaveAs extends ListActivity {

    private List<String> fileList = new ArrayList<>();
    private Stack<File> filesStack = new Stack<>();
    private File dir;
    private TextView userFileName;
    private Context thisc = this;
    private String localFileName = "";
    private String currDir = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.save_as);

        Button cancelButton = findViewById(R.id.button_cancel_in_save);
        Button createButton = findViewById(R.id.button_create);
        Button rewriteButton = findViewById(R.id.button_rewrite);

        userFileName = findViewById(R.id.user_file_name);

        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                localFileName = "";
                Main.canceled = true;
                SaveAs.super.onBackPressed();
            }
        });

        createButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currDir.equals("")){
                LayoutInflater l = LayoutInflater.from(thisc);
                View dialog = l.inflate(R.layout.alert_dialog, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(thisc);

                mDialogBuilder.setView(dialog);

                final EditText userText = dialog.findViewById(R.id.input_text);

                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        userFileName.setText(userText.getText());
                                        Main.newFile = userFileName.getText().toString();
                                        Main.fileName = currDir + "/" + Main.newFile;
                                        new File(currDir, Main.newFile);
                                        SaveAs.super.onBackPressed();

                                        //Toast.makeText(getApplicationContext(), Main.newFile , Toast.LENGTH_LONG).show();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = mDialogBuilder.create();

                alertDialog.show();

                //Main.directory = dir.getPath();
                // Main.newFile = userFileName.getText().toString();
                //Main.fileName = dir.getPath()+ "/" + Main.newFile;
                //Toast.makeText(getApplicationContext(), Main.newFile , Toast.LENGTH_LONG).show();
            }
                else Toast.makeText(getApplicationContext(),"Выберите файл." , Toast.LENGTH_LONG).show();
        }
        });
        rewriteButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localFileName.equals("")) Toast.makeText(getApplicationContext(),"Выберите файл." , Toast.LENGTH_LONG).show();
                else {
                    Main.fileName = localFileName;
                    SaveAs.super.onBackPressed();
                }
            }
        });


        listDirectories(new File(Environment.getExternalStorageDirectory().getPath()));
    }



    void listDirectories(File f){
        currDir = f.getAbsolutePath();
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

    /*@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File thisFile = new File(fileList.get(position));

        if(thisFile.isDirectory()){
            dir = thisFile;
            //Toast toast = Toast.makeText(getApplicationContext(), dir.getPath(), Toast.LENGTH_SHORT);
            //toast.show();

        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),"Вы пытаетель открыть файл", Toast.LENGTH_SHORT);
            toast.show();
        }
    }*/

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
            dir = filesStack.pop();
            listDirectories(dir);
        }

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
}
