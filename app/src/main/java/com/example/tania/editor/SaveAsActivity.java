package com.example.tania.editor;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;

public class SaveAsActivity extends ListActivity {

    private ArrayList<String> tmpFileList = new ArrayList<>();
    private ArrayList<File> tmpFileListFull = new ArrayList<>();
    private Stack<File> filesStack = new Stack<>();
    private File dir;
    private TextView userFileName;
    private Context thisc = this;
    private String localFileName = "";
    private String tmpDirectory = Environment.getExternalStorageDirectory().getPath();
    private TextView textView;
    private FileAdapter fileAdapter;
    private String extension = "";
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.save_as);

        textView = findViewById(R.id.header);

        Button cancelButton = findViewById(R.id.button_cancel_in_save);
        Button createButton = findViewById(R.id.button_create);
        Button rewriteButton = findViewById(R.id.button_rewrite);

        userFileName = findViewById(R.id.user_file_name);


        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                localFileName = "";
                MainActivity.canceled = true;
                SaveAsActivity.super.onBackPressed();
            }
        });

        createButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater l = LayoutInflater.from(thisc);
                View dialog = l.inflate(R.layout.alert_dialog, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(SaveAsActivity.this);

                mDialogBuilder.setView(dialog);

                final EditText userText = dialog.findViewById(R.id.input_for_user_file_name);
                spinner = dialog.findViewById(R.id.extension_spinner);

                ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(SaveAsActivity.this, R.array.extension, android.R.layout.simple_spinner_item);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);

                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(getText(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        userFileName.setText(userText.getText().toString().equals("") ? getString(R.string.unnamed_file) : userText.getText());
                                        extension = spinner.getSelectedItem().toString();
                                        MainActivity.newFile = userFileName.getText().toString() + extension;
                                        MainActivity.fileName = tmpDirectory + "/" + MainActivity.newFile;
                                        new File(tmpDirectory, MainActivity.newFile);
                                        SaveAsActivity.super.onBackPressed();

                                    }
                                })
                        .setNegativeButton(getText(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = mDialogBuilder.create();

                alertDialog.show();

            }
        });
        rewriteButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localFileName.equals(""))
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_choose_file), Toast.LENGTH_LONG).show();
                else {
                    MainActivity.fileName = localFileName;
                    SaveAsActivity.super.onBackPressed();
                }
            }
        });
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File thisFile = new File(  tmpDirectory + "/" + tmpFileList.get(position));
//        getListView().setItemChecked(position, true);
                if(thisFile.isDirectory()){
                    localFileName = "";
                    listDirectories(thisFile);
                }
                else{
                    localFileName = thisFile.getAbsolutePath();
                }
            }
        });*/

        /*@Override
        protected void onListItemClick(ListView l, View v, int position, long id) {
            fileAdapter.changeHighlighting(position);
            File thisFile = new File(  tmpDirectory + "/" + tmpFileList.get(position));
            if(thisFile.isDirectory()){
                localFileName = "";
                listDirectories(thisFile);
            }
            else{
                localFileName = thisFile.getAbsolutePath();
            }
        }*/


        listDirectories(new File(tmpDirectory));
    }


    void listDirectories(File f) {
        tmpDirectory = f.getAbsolutePath();
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
        fileAdapter = new FileAdapter(tmpFileListFull, this, R.layout.save_as);
        //ArrayAdapter<String> directoryList = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tmpFileList);
        //listView.setAdapter(fileAdapter);
        setListAdapter(fileAdapter);
    }

    /*@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File thisFile = new File(tmpFileList.get(position));

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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        fileAdapter.changeHighlighting(position);
//        File thisFile = new File(tmpDirectory + "/" + tmpFileList.get(position));
        File thisFile = tmpFileListFull.get(position); //
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
            dir = filesStack.pop();
            listDirectories(dir);
        }

    }
}
