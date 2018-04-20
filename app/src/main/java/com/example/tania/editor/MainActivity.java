package com.example.tania.editor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit = findViewById(R.id.editText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_save:
                saveFile(OpenFileDialog.getFile()); // have no idea how to do it
                return true;
            case R.id.action_open:
                openFile();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setClass(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return true;
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);


        float size = Float.parseFloat(sp.getString(getString(R.string.pref_size), "13"));
        edit.setTextSize(size);

        String norm = sp.getString(getString(R.string.pref_style), "");
        int typeface = Typeface.NORMAL;
        if (norm.contains("@strings.pref_style_regular")) typeface += Typeface.NORMAL;
        if (norm.contains("@strings.pref_style_bold")) typeface += Typeface.BOLD;
        if (norm.contains("@strings.pref_style_italic")) typeface += Typeface.ITALIC;
        edit.setTypeface(null, typeface);

        String colort = sp.getString(getString(R.string.pref_text_color), "");
        int  ct = Color.BLACK;
        if (colort.contains("@strings.pref_color_black")) ct = Color.BLACK;
        if (colort.contains("@strings.pref_color_red")) ct = Color.RED;
        if (colort.contains("@strings.pref_color_white")) ct = Color.WHITE;
        if (colort.contains("@strings.pref_color_green")) ct = Color.rgb(0,128,0);
        if (colort.contains("@strings.pref_color_blue")) ct = Color.BLUE;
        if (colort.contains("@strings.pref_color_yellow")) ct = Color.YELLOW;
        if (colort.contains("@strings.pref_color_gray")) ct = Color.GRAY;
        if (colort.contains("@strings.pref_color_magenta")) ct = Color.MAGENTA;
        edit.setTextColor(ct);


        String color = sp.getString(getString(R.string.pref_back_color), "");
        int  c = Color.WHITE;
        if (color.contains("@strings.pref_color_black")) c = Color.BLACK;
        if (color.contains("@strings.pref_color_white")) c = Color.rgb(255,255,240);
        if (color.contains("@strings.pref_color_yellow")) c = Color.rgb(255,215,0);
        if (color.contains("@strings.pref_color_gray")) c = Color.rgb(220,220,220);
        if (color.contains("@strings.pref_color_magenta")) c = Color.rgb(238,130,238);
        if (color.contains("@strings.pref_color_moccasin")) c = Color.rgb(255,228,181);
        if (color.contains("@strings.pref_color_violet")) c = Color.rgb(138,43,226);
        if (color.contains("@strings.pref_color_blue")) c = Color.rgb(135,206,250);
        if (color.contains("@strings.pref_color_green")) c = Color.rgb(173,255,47);
        if (color.contains("@strings.pref_color_orange")) c = Color.rgb(255,165,0);
        if (color.contains("@strings.pref_color_coral")) c = Color.rgb(255,127,80);
        if (color.contains("@strings.pref_color_maroon")) c = Color.rgb(128,0,0);
        edit.setBackgroundColor(c);

    }

    private void openFile(){
        OpenFileDialog fileDialog = new OpenFileDialog(this)
                .setFilter(".*\\.csv")
                .setOpenDialogListener(new OpenFileDialog.OpenDialogListener() {
                    @Override
                    public void OnSelectedFile(String fileName) {
                        Toast.makeText(getApplicationContext(), fileName, Toast.LENGTH_LONG).show();
                    }
                });
        fileDialog.show();
        try {
            InputStream inputStream = openFileInput(OpenFileDialog.getFile());

            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }

                inputStream.close();
                edit.setText(builder.toString());
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }

    }

    private void saveFile(String fileName){
        try{
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(fileName, 0));
            out.write(edit.getText().toString());
            out.close();
        }catch(IOException e){
            Toast.makeText(getApplicationContext(),"Увы, не получилось.", Toast.LENGTH_LONG).show();
        }
    }

}
