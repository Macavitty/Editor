package com.example.tania.editor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Main extends AppCompatActivity {

    private EditText edit;
    static String fileName = "", directory = "", newFile = "";
    private boolean goToOpen = false;
    private boolean gotoSaveAs = false;
    static boolean canceled = false;
    String buffer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submain);
        edit = findViewById(R.id.editText);
        edit.setSingleLine(false);

       /* edit.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable s) { ifTextChanged = true; }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_submain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        Intent intent = new Intent();

        switch(item.getItemId()){

            case R.id.action_save:
                if (!fileName.equals(""))saveFile();
                else{
                    saveTextToBuffer();
                    gotoSaveAs = true;
                    intent.setClass(this, SaveAs.class);
                    startActivity(intent);
                }
                return true;

            case R.id.action_save_as:
                saveTextToBuffer();
                gotoSaveAs = true;
                intent.setClass(this, SaveAs.class);
                startActivity(intent);
                return true;

            case R.id.action_open:
                saveTextToBuffer();
                goToOpen = true;
                intent.setClass(this, Open.class);
                startActivity(intent);
                return true;

            case R.id.action_settings:
                intent.setClass(this, Settings.class);
                startActivity(intent);
                return true;

            case R.id.action_new_file:
                edit.setText("");
                fileName = "";
                return true;

            case R.id.action_clear:
                edit.setText("");
                return true;

            default:
                return true;
        }
    }

    @Override
    public void onResume(){

        super.onResume();

        /*Button mButtonSave = findViewById(R.id.action_save);

        if (fileName.equals("")) mButtonSave.setEnabled(false);
        else mButtonSave.setEnabled(true);*/

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        // After Settings  //

        int size = Integer.parseInt(sp.getString(getString(R.string.pref_size), "20"));
        if (size < 10) size = 10;
        else if (size > 600) size = 600;
        edit.setTextSize(size);

        int typeface = Typeface.NORMAL;
        String type = sp.getString(getString(R.string.pref_style), "");
        //if (sp.getBoolean(getString(R.string.pref_style_regular),false)) typeface += Typeface.NORMAL;
        if (type.contains("Полужирный")) typeface += Typeface.BOLD;
        if (type.contains("Курсив") || type.contains("курсив")) typeface += Typeface.ITALIC;
        edit.setTypeface(null, typeface);

        String tcolor = sp.getString(getString(R.string.pref_text_color), "");
        int  ct = Color.rgb(0,128,0);
        if (tcolor.contains("Чёрный")) ct = Color.BLACK;
        if (tcolor.contains("Красный")) ct = Color.RED;
        if (tcolor.contains("Белый")) ct = Color.WHITE;
        if (tcolor.contains("Зелёный")) ct = Color.rgb(0,128,0);
        if (tcolor.contains("Синий")) ct = Color.BLUE;
        if (tcolor.contains("Серый")) ct = Color.GRAY;
        if (tcolor.contains("Сиреневый")) ct = Color.MAGENTA;
        edit.setTextColor(ct);

        String color = sp.getString(getString(R.string.pref_back_color), "");
        int  c = Color.rgb(255,225,90);
        if (color.contains("Чёрный")) c = Color.BLACK;
        if (color.contains("Белый")) c = Color.rgb(255,255,240);
        if (color.contains("Жёлтый")) c = Color.rgb(255,225,90);
        if (color.contains("Серый")) c = Color.rgb(220,220,220);
        if (color.contains("Сиреневый")) c = Color.rgb(238,130,238);
        if (color.contains("Бежевый")) c = Color.rgb(255,228,181);
        if (color.contains("Фиолетовый")) c = Color.rgb(138,43,226);
        if (color.contains("Голубой")) c = Color.rgb(135,206,250);
        if (color.contains("Зелёный")) c = Color.rgb(173,255,47);
        if (color.contains("Оранжевый")) c = Color.rgb(255,165,0);
        if (color.contains("Корраловый")) c = Color.rgb(255,127,80);
        if (color.contains("Бордовый")) c = Color.rgb(128,0,0);
        edit.setBackgroundColor(c);

        /*if (sp.getBoolean(getString(R.string.pref_first_litera), false)) {
            edit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES); // work please
        } */

        //After Open  //
        if (goToOpen){
            goToOpen = false;
            if (!canceled){
                if (!fileName.equals("")) openFile();
            }
            else{
                canceled = false;
                edit.setText(buffer);
            }
        }

        //  After SaveAs  //
        else if (gotoSaveAs){
            gotoSaveAs = false;
            if (!canceled){
                if (!fileName.equals("")) saveFile();
            }
            else{
                canceled = false;
                edit.setText(buffer);
            }
        }
        //else openFile();


    }

    private void saveFile(){
        try{
            FileOutputStream outputStream = new FileOutputStream(fileName);
            OutputStreamWriter out = new OutputStreamWriter(outputStream);
            out.write(edit.getText().toString());
            out.close();
            Toast.makeText(getApplicationContext(),"Всё норм, файл сохранён." , Toast.LENGTH_SHORT).show();
            openFile();
            //edit.setText(fileName);
        }catch(IOException e){
            Toast.makeText(getApplicationContext(), "Увы, не получилось.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFile(){
        try {

            //InputStream inputStream = openFileInput(file); // for internal
            FileInputStream inputStream = new FileInputStream (new File(fileName));
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();
            edit.setText(builder.toString());
        } catch (Throwable t) { Toast.makeText(getApplicationContext(),"Хм, что-то пошло не так." , Toast.LENGTH_SHORT).show();}
    }

    private void createFile(){
        new File(directory, newFile);
        fileName = directory + "/" + newFile;
        saveFile();
    }

    private void saveTextToBuffer(){
        buffer = edit.getText().toString();
        }
}