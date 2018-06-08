package com.example.tania.editor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Stack;



public class Main extends AppCompatActivity {

    private Context context = this;

    private EditText edit;
    private TabLayout tabLayout;

    static String fileName = "", directory = "", newFile = "";
    static float textSize;
    static boolean canceled = false;

    private boolean goToOpen = false;
    private boolean gotoSaveAs = false;
    private boolean isReady = true;
    private int cursorPosition = 0;
    private String currentKey = "";
    private int maxTabIndex = 0;
    private String buffer = "";
    private Stack<String> changesBufferBack = new Stack<>();
    private Stack<String> changesBufferCancel = new Stack<>();
    private HashMap<String, String> tabContext = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submain);
//        edit.setSingleLine(false);

        edit = findViewById(R.id.editText);
        tabLayout = findViewById(R.id.sliding_tabs);

        edit.addTextChangedListener(new TextWatcher(){
           final Handler handler = new Handler();
           Runnable runnable;
            @Override
            public void afterTextChanged(Editable s) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "***", Toast.LENGTH_SHORT).show();//
                        if (!changesBufferBack.empty() && isReady){
                            String s = changesBufferBack.peek();
                            if (!s.equals(edit.getText().toString())){
                                //Toast.makeText(getApplicationContext(), "**", Toast.LENGTH_SHORT).show();//
                                changesBufferBack.add(edit.getText().toString());
                                //Toast.makeText(getApplicationContext(), "***", Toast.LENGTH_SHORT).show();//
                            }
                        }
                        else changesBufferBack.push(edit.getText().toString());
                    }
                };
                handler.postDelayed(runnable, 800);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {handler.removeCallbacks(runnable); }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabContext.put(currentKey, edit.getText().toString());
                currentKey = tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString();
                edit.setText(tabContext.get(currentKey));
                //tabContext.remove(currentKey);
                changesBufferBack.clear();
                changesBufferCancel.clear();
                buffer = "";
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.addTab(tabLayout.newTab().setText(fileName.equals("") ? "This file needs a name" : fileName.substring(fileName.lastIndexOf("/")+1, fileName.contains(".")? fileName.indexOf(".") : fileName.length())));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_submain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

       cursorPosition = edit.getSelectionEnd();

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
                tabContext.remove(currentKey);
                currentKey = "This file needs a name " + ++maxTabIndex;
                tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).setText(currentKey);
                changesBufferBack.clear();
                changesBufferCancel.clear();
                buffer = "";
                edit.setText("");
                fileName = "";
                return true;

            case R.id.action_clear:
                edit.setText("");
                return true;

            case R.id.action_back: //****
                if (!changesBufferBack.empty()) changesBufferCancel.push(changesBufferBack.pop());
                if (!changesBufferBack.empty()){
                    isReady = false;
                    String s = changesBufferBack.pop();
                    edit.setText(s);
                 //   edit.setSelection(cursorPosition);
                    isReady = true;
                }
                return true;

            case R.id.action_cancel:
                if (!changesBufferCancel.empty()){
                    isReady = false;
                    edit.setText(changesBufferCancel.pop());
            //        edit.setSelection(cursorPosition);
                    isReady = true;
                }
                return true;

            case R.id.add_tab:
                tabLayout.addTab(tabLayout.newTab().setText("This file needs a name " + ++maxTabIndex));

                return true;

            case R.id.del_tab:
                if(tabLayout.getTabCount() > 1){
                    tabContext.remove(currentKey);
                    tabLayout.removeTabAt(tabLayout.getSelectedTabPosition());
                    currentKey = tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString();
                    edit.setText(tabContext.get(currentKey));
                }

            default:
                return true;
        }
    }

    @Override
    public void onResume(){

        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        // After Settings  //

        textSize = Float.parseFloat(sp.getString(getString(R.string.pref_size), "20"));
        if (textSize < 10) textSize = 10;
        else if (textSize > 600) textSize = 600;
        edit.setTextSize(textSize);

        int typefaceStyle = Typeface.NORMAL;
        Typeface typefaceSrift = Typeface.DEFAULT;
        String type = sp.getString(getString(R.string.pref_style), "");
        String srift = sp.getString(getString(R.string.pref_srift), "");
        //if (sp.getBoolean(getString(R.string.pref_style_regular),false)) typeface += Typeface.NORMAL;
        if (type.contains("Полужирный")) typefaceStyle += Typeface.BOLD;
        if (type.contains("Курсив") || type.contains("курсив")) typefaceStyle += Typeface.ITALIC;
        if (srift.contains("Monospace")) typefaceSrift = Typeface.MONOSPACE;
        if (srift.contains("Serif")) typefaceSrift = Typeface.SERIF;
        edit.setTypeface(Typeface.create(typefaceSrift, typefaceStyle));

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
        if (color.contains("Сиреневый")) c = ContextCompat.getColor(this, R.color.myLilac);
        if (color.contains("Бежевый")) c = ContextCompat.getColor(this, R.color.myBeige);
        if (color.contains("Фиолетовый")) c = ContextCompat.getColor(this, R.color.myPurple);
        if (color.contains("Голубой")) c = ContextCompat.getColor(this, R.color.myBlue);
        if (color.contains("Зелёный")) c = ContextCompat.getColor(this, R.color.myGreen);
        if (color.contains("Оранжевый")) c = ContextCompat.getColor(this, R.color.myOrange);
        if (color.contains("Корраловый")) c = Color.rgb(255,127,80);
        if (color.contains("Бордовый")) c = ContextCompat.getColor(this, R.color.myVinous);
        edit.setBackgroundColor(c);

        if (sp.getBoolean(getString(R.string.pref_first_litera), false)) {// work please
            edit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        }
        else edit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        edit.setSingleLine(false);

        if (sp.getBoolean(getString(R.string.pref_numeration), false)) {// work please
            MainEditText.isNumbersNeeded = true;
        }
        else MainEditText.isNumbersNeeded = false;
        edit.setPadding(6, 4, 6, 4);


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

        edit.setSelection(cursorPosition);
    }

    private void saveFile(){
        try{
            FileOutputStream outputStream = new FileOutputStream(fileName);
            OutputStreamWriter out = new OutputStreamWriter(outputStream);
            out.write(edit.getText().toString());
            out.close();
            Toast.makeText(getApplicationContext(), getString(R.string.msg_file_saved) , Toast.LENGTH_SHORT).show();
            openFile();
            //edit.setText(fileName);
        }catch(IOException e){
            Toast.makeText(getApplicationContext(), getString(R.string.msg_file_not_saved), Toast.LENGTH_SHORT).show();
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
            currentKey = fileName.substring(fileName.lastIndexOf("/")+1, fileName.contains(".")? fileName.indexOf(".") : fileName.length());
            tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).setText(currentKey);
            //currentKey += "." + tabLayout.getSelectedTabPosition();
        } catch (Throwable t) { Toast.makeText(getApplicationContext(), getString(R.string.msg_file_not_opened) , Toast.LENGTH_SHORT).show();}
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


































