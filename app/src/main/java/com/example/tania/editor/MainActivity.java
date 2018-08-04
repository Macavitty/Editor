package com.example.tania.editor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Arrays;
import java.util.Map;
import java.util.Stack;


public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private EditText editText;
    private ViewPagerAdapter pagerAdapter;

    private RootMatEgoFragment rootMatEgoFragment;
    private LeafMatEgoFragment leafMatEgoFragment;

    private Button addButton;
    private Button removeButton;

    private boolean goToOpen = false;
    private boolean gotoSaveAs = false;

    private String buffer = ""; // make it private at the end
    /*private Stack<String> bufferUndoStack;
    private Stack<String> bufferRedoStack;
    private Map<Integer, Stack<String>> bufferUndoMap;
    private Map<Integer, Stack<String>> bufferRedoMap;*/

    static int typefaceStyle = Typeface.NORMAL;
    static int textColor = Color.rgb(0, 128, 0);
    static int backgroundColor = Color.rgb(255, 225, 90);
    static float textSize;
    static boolean capLitera = false;
    static boolean isReady = true; // kostyl for handling textChangesListener
    static boolean canceled = false;
    static String fileName = "", directory = "", newFile = "";
    static Typeface typefaceFont = Typeface.DEFAULT;
    static boolean isPageDeleted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submain);

        final String DEFAULT_PAGE_NAME = getString(R.string.default_file_name);

        rootMatEgoFragment = (RootMatEgoFragment) this.getSupportFragmentManager().findFragmentById(R.id.boss_fragment);
        tabLayout = findViewById(R.id.sliding_tabs);
        pagerAdapter = rootMatEgoFragment.getAdapter();
        addButton = findViewById(R.id.add_button);
        removeButton = findViewById(R.id.remove_button);

        rootMatEgoFragment.addTab(DEFAULT_PAGE_NAME, pagerAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerAdapter = rootMatEgoFragment.getAdapter();
                rootMatEgoFragment.addTab(DEFAULT_PAGE_NAME, pagerAdapter);
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pagerAdapter.getCount() > 1) {
                    rootMatEgoFragment.deleteTab(rootMatEgoFragment.getCurrentTab());
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_submain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int currentPagePosition = rootMatEgoFragment.getCurrentTab();
        leafMatEgoFragment = (LeafMatEgoFragment) pagerAdapter.getItem(currentPagePosition);
        editText = leafMatEgoFragment.getEditText();
        Intent intent = new Intent();

        switch (item.getItemId()) {

            case R.id.action_save:
                if (!fileName.equals("")) saveFile();
                else {
                    //saveTextToBuffer();
                    gotoSaveAs = true;
                    intent.setClass(this, SaveAsActivity.class);
                    startActivity(intent);
                }
                return true;

            case R.id.action_save_as:
                //saveTextToBuffer();
                gotoSaveAs = true;
                intent.setClass(this, SaveAsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_open:
                //saveTextToBuffer();
                goToOpen = true;
                intent.setClass(this, OpenFileActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_settings:
                intent.setClass(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_clear:
                editText.setText("");
                return true;

            // ***
            // It works correctly!
            // if smth wrong look somewhere else
            case R.id.action_undo:
                /*leafMatEgoFragment.removeTextWatcher();
                if (!bufferUndoStack.empty()) {
                    isReady = false;
                    Log.e("uundo" + currentPagePosition + ": ", Arrays.deepToString(bufferUndoStack.toArray()));
                    /*if (bufferUndoStack.size() != 1) */
                    /*String fuckThisStacks = bufferUndoStack.pop();
                    //bufferUndoStack.pop();
                    //bufferRedoStack.push(fuckThisStacks);
                    Log.e("poped" + currentPagePosition + ": ", fuckThisStacks);
                    Log.e("uundo" + currentPagePosition + ": ", Arrays.deepToString(bufferUndoStack.toArray()));

                    String s = bufferUndoStack.peek();
                    rootMatEgoFragment.setBufferRedoMap(bufferRedoMap);
                    rootMatEgoFragment.setBufferUndoMap(bufferUndoMap);
                    editText.setText(s);
                    //editText.setSelection(cursorPosition);
                    isReady = true;
                }
                leafMatEgoFragment.setTextWatcher();*/
                return true;

            case R.id.action_redo:
                /*leafMatEgoFragment.removeTextWatcher();
                bufferUndoMap = rootMatEgoFragment.getBufferUndoMap();
                bufferRedoMap = rootMatEgoFragment.getBufferRedoMap();
                bufferRedoStack = bufferRedoMap.get(currentPagePosition);
                bufferUndoStack = bufferUndoMap.get(currentPagePosition);
                if (!bufferRedoStack.empty()) {
                    isReady = false;
                    Log.e("redo", currentPagePosition+"");
                    String s = bufferRedoStack.pop();
                    rootMatEgoFragment.setBufferRedoMap(bufferRedoMap);
                    rootMatEgoFragment.setBufferUndoMap(bufferUndoMap);
                    editText.setText(s);
                    editText.setSelection(cursorPosition);
                    isReady = true;
                }
                leafMatEgoFragment.setTextWatcher();*/
                return true;
            // ***

            default:
                return true;
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        // After SettingsActivity  //

        textSize = Float.parseFloat(sp.getString(getString(R.string.pref_size), "20"));
        if (textSize < 10) textSize = 10;
        else if (textSize > 600) textSize = 600;


        String type = sp.getString(getString(R.string.pref_style), "");
        String srift = sp.getString(getString(R.string.pref_srift), "");
        //if (sp.getBoolean(getString(R.string.pref_style_regular),false)) typeface += Typeface.NORMAL;
        if (type.contains(getString(R.string.pref_style_bold))) typefaceStyle += Typeface.BOLD;
        if (type.contains(getString(R.string.pref_style_italic)) || type.contains(getString(R.string.italic_low_case)))
            typefaceStyle += Typeface.ITALIC;
        if (srift.contains(getString(R.string.pref_style_monospace)))
            typefaceFont = Typeface.MONOSPACE;
        if (srift.contains(getString(R.string.pref_style_serif))) typefaceFont = Typeface.SERIF;

        String tcolor = sp.getString(getString(R.string.pref_text_color), "");
        if (tcolor.contains(getString(R.string.pref_color_black))) textColor = Color.BLACK;
        if (tcolor.contains(getString(R.string.pref_color_red))) textColor = Color.RED;
        if (tcolor.contains(getString(R.string.pref_color_white))) textColor = Color.WHITE;
        if (tcolor.contains(getString(R.string.pref_color_green))) textColor = Color.rgb(0, 128, 0);
        if (tcolor.contains(getString(R.string.pref_color_blue))) textColor = Color.BLUE;
        if (tcolor.contains(getString(R.string.pref_color_gray))) textColor = Color.GRAY;
        if (tcolor.contains(getString(R.string.pref_color_magenta))) textColor = Color.MAGENTA;

        String color = sp.getString(getString(R.string.pref_back_color), "");
        if (color.contains(getString(R.string.pref_color_black))) backgroundColor = Color.BLACK;
        if (color.contains(getString(R.string.pref_color_white)))
            backgroundColor = Color.rgb(255, 255, 240);
        if (color.contains(getString(R.string.pref_color_yellow)))
            backgroundColor = Color.rgb(255, 225, 90);
        if (color.contains(getString(R.string.pref_color_gray)))
            backgroundColor = Color.rgb(220, 220, 220);
        if (color.contains(getString(R.string.pref_color_magenta)))
            backgroundColor = ContextCompat.getColor(this, R.color.myLilac);
        if (color.contains(getString(R.string.pref_color_moccasin)))
            backgroundColor = ContextCompat.getColor(this, R.color.myBeige);
        if (color.contains(getString(R.string.pref_color_violet)))
            backgroundColor = ContextCompat.getColor(this, R.color.myPurple);
        if (color.contains(getString(R.string.pref_color_light_blue)))
            backgroundColor = ContextCompat.getColor(this, R.color.myBlue);
        if (color.contains(getString(R.string.pref_color_green)))
            backgroundColor = ContextCompat.getColor(this, R.color.myGreen);
        if (color.contains(getString(R.string.pref_color_orange)))
            backgroundColor = ContextCompat.getColor(this, R.color.myOrange);
        if (color.contains(getString(R.string.pref_color_coral)))
            backgroundColor = Color.rgb(255, 127, 80);
        if (color.contains(getString(R.string.pref_color_maroon)))
            backgroundColor = ContextCompat.getColor(this, R.color.myVinous);

        if (sp.getBoolean(getString(R.string.pref_first_litera), false)) // work please
            capLitera = true;
        else capLitera = false;

        if (sp.getBoolean(getString(R.string.pref_numeration), false)) {// work please
            MainEditText.isNumbersNeeded = true;
        } else MainEditText.isNumbersNeeded = false;

        //After OpenFileActivity  //
        if (goToOpen) {
            goToOpen = false;
            if (!canceled) {
                if (!fileName.equals("")) openFile();
            } else {
                canceled = false;
                //textFromBufferIsNeeded = true;
            }
        }

        //  After SaveAsActivity  //
        else if (gotoSaveAs) {
            gotoSaveAs = false;
            if (!canceled) {
                if (!fileName.equals("")) saveFile();
            } else {
                canceled = false;
                //textFromBufferIsNeeded = true;
            }
        }
        //else openFile();

    }

    /*
    * it do not updates text in others tabs with the same file opened
    * (but should it?)*/
    private void saveFile() {
        leafMatEgoFragment = (LeafMatEgoFragment) pagerAdapter.getItem(rootMatEgoFragment.getCurrentTab());
        editText = leafMatEgoFragment.getEditText();

        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            OutputStreamWriter out = new OutputStreamWriter(outputStream);
            out.write(editText.getText().toString());
            out.close();
            Toast.makeText(getApplicationContext(), getString(R.string.msg_file_saved), Toast.LENGTH_SHORT).show();
            openFile();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_file_not_saved), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void openFile() {
        leafMatEgoFragment = (LeafMatEgoFragment) pagerAdapter.getItem(rootMatEgoFragment.getCurrentTab());
        editText = leafMatEgoFragment.getEditText();
        String title;
        try {

            //InputStream inputStream = openFileInput(file); // for internal
            FileInputStream inputStream = new FileInputStream(new File(fileName));
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            //leafMatEgoFragment = new LeafMatEgoFragment();

            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();
            title = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            editText.setText(builder.toString());
            pagerAdapter.setPageTitle(rootMatEgoFragment.getCurrentTab(), title);
            tabLayout.getTabAt(rootMatEgoFragment.getCurrentTab()).setText(title);
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_file_not_opened), Toast.LENGTH_SHORT).show();
            t.printStackTrace();
        }
    }

    private void createFile() {
        new File(directory, newFile);
        fileName = directory + "/" + newFile;
        saveFile();
    }

    private void saveTextToBuffer() {
        leafMatEgoFragment = (LeafMatEgoFragment) pagerAdapter.getItem(rootMatEgoFragment.getCurrentTab());
        editText = leafMatEgoFragment.getEditText();
        buffer = editText.getText().toString();
        Toast.makeText(getApplicationContext(), buffer, Toast.LENGTH_SHORT).show();
    }

    public void quitDialogue() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle(getString(R.string.dlg_quit_app));
    }

}


































