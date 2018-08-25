package com.example.tania.editor;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;


public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private EditText editText;
    private ViewPagerAdapter pagerAdapter;

    private RootMatEgoFragment rootFragment;
    private LeafMatEgoFragment leafFragment;

    private boolean goToOpen = false;
    private boolean goToSaveAs = false;

    private String buffer = ""; // make it private at the end
    private Deque<String> undoStack;
    private Deque<String> redoStack;

    static int typefaceStyle = Typeface.NORMAL;
    static int textColor = Color.rgb(0, 128, 0);
    static int backgroundColor = Color.rgb(255, 225, 90);
    static int cursorPosition = 0;
    static float textSize;
    static boolean capLitera = false;
    static boolean isReady = true; // kostyl for handling textChangesListener
    static boolean canceled = false;
    static String fileName = "", directory = "", newFile = "";
    static Typeface typefaceFont = Typeface.DEFAULT;

    //    int permissionWriteStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//    int permissionReadStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submain);

        final String DEFAULT_PAGE_NAME = this.getString(R.string.default_file_name);

        rootFragment = (RootMatEgoFragment) this.getSupportFragmentManager().findFragmentById(R.id.boss_fragment);
        refreshPagerAdapter();
        tabLayout = findViewById(R.id.sliding_tabs);

        rootFragment.addTab(DEFAULT_PAGE_NAME);

        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshPagerAdapter();
                rootFragment.addTab(DEFAULT_PAGE_NAME);
            }
        });

        Button removeButton = findViewById(R.id.remove_button);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                refreshPagerAdapter();
                if (pagerAdapter.getCount() > 1) {
                    final int CURRENT_TAB = rootFragment.getCurrentTab();
                    refreshLeafFragment();
                    if (!leafFragment.getEditText().getText().toString().equals(leafFragment.getUntaughtText())) {

                        AlertDialog.Builder saveDialog = new AlertDialog.Builder(MainActivity.this);
                        saveDialog.setTitle(getString(R.string.dlg_save_changes));

                        saveDialog.setPositiveButton(getString(R.string.dlg_positive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (pagerAdapter.getTitle(CURRENT_TAB).equals(DEFAULT_PAGE_NAME))
                                    goToSaveAs();
                                else
                                    saveFile(false);
                                rootFragment.deleteTab(CURRENT_TAB);
                            }
                        });

                        saveDialog.setNegativeButton(getString(R.string.dlg_negative), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                rootFragment.deleteTab(CURRENT_TAB);
                            }
                        });
                        saveDialog.show();
                    } else
                        rootFragment.deleteTab(CURRENT_TAB);
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
        refreshPagerAdapter();
        refreshLeafFragment();
        editText = leafFragment.getEditText();
        if (editText == null) cursorPosition = 0;
        else cursorPosition = editText.getSelectionEnd();
        Intent intent = new Intent();

        switch (item.getItemId()) {

            case R.id.action_save:
                if (!fileName.equals("")) saveFile(true);
                else {
                    //saveTextToBuffer();
                    goToSaveAs();
                }
                return true;

            case R.id.action_save_as:
                //saveTextToBuffer();
                goToSaveAs();
                return true;

            case R.id.action_open:
                goToOpen();
                //saveTextToBuffer();
                return true;

            case R.id.action_settings:
                intent.setClass(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_clear:
                editText.setText("");
                return true;

            case R.id.action_undo:
                isReady = false;
                undoStack = leafFragment.getUndoStack();
                redoStack = leafFragment.getRedoStack();
                if (undoStack.size() > 0) {
                    Log.d("undo", Arrays.deepToString(undoStack.toArray()));
                    if (!undoStack.peek().equals(redoStack.peek()))
                        redoStack.push(undoStack.pop());
                    Log.d("redo", Arrays.deepToString(redoStack.toArray()));
                    editText.setText(undoStack.peek());
                    try {
                        editText.setSelection(cursorPosition);
                    } catch (IndexOutOfBoundsException e) {
                        editText.setSelection(editText.length());
                    }
                    leafFragment.setRedoStack(redoStack);
                    leafFragment.setUndoStack(undoStack);
                    isReady = true;
                }
                return true;

            case R.id.action_redo:
                isReady = false;
                undoStack = leafFragment.getUndoStack();
                redoStack = leafFragment.getRedoStack();
                if (redoStack.size() > 0) {
                    if (editText.getText().toString().equals(redoStack.peek())) redoStack.pop();
                    if (redoStack.size() > 0) editText.setText(redoStack.pop());
                    try {
                        editText.setSelection(cursorPosition);
                    } catch (IndexOutOfBoundsException e) {
                        editText.setSelection(editText.length());
                    }
                    leafFragment.setUndoStack(undoStack);
                    leafFragment.setRedoStack(redoStack);
                    isReady = true;
                }
                return true;

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
        if (tcolor.contains(getString(R.string.pref_color_yellow)))
            textColor = ContextCompat.getColor(this, R.color.myYellow);
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
        if (color.contains(getString(R.string.pref_color_pink)))
            backgroundColor = ContextCompat.getColor(this, R.color.myPink);
        if (color.contains(getString(R.string.pref_color_navy)))
            backgroundColor = ContextCompat.getColor(this, R.color.myNavy);

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
                if (!fileName.equals("")) openFile(false);
            } else {
                canceled = false;
                //textFromBufferIsNeeded = true;
            }
        }

        //  After SaveAsActivity  //
        else if (goToSaveAs) {
            goToSaveAs = false;
            if (!canceled) {
                if (!fileName.equals("")) saveFile(true);
            } else {
                canceled = false;
                //textFromBufferIsNeeded = true;
            }
        }
        //else openFile();

    }

    private void goToSaveAs() {
        goToSaveAs = true;
        Intent intent = new Intent();
        intent.setClass(this, SaveAsActivity.class);
        startActivity(intent);
    }

    private void goToOpen() {

        final String DEFAULT_PAGE_NAME = this.getString(R.string.default_file_name);
        goToOpen = true;
        final Intent intent = new Intent();
        intent.setClass(this, OpenFileActivity.class);

        final int currentTab = rootFragment.getCurrentTab();
        refreshPagerAdapter();
        refreshLeafFragment();
        // ! по почти неизвестным причинам иногда вылезает null !
        if (!leafFragment.getEditText().getText().toString().equals(leafFragment.getUntaughtText())) {

            AlertDialog.Builder saveDialog = new AlertDialog.Builder(MainActivity.this);
            saveDialog.setTitle(getString(R.string.dlg_save_changes));

            saveDialog.setPositiveButton(getString(R.string.dlg_positive), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (pagerAdapter.getTitle(currentTab).equals(DEFAULT_PAGE_NAME) || pagerAdapter.getTitle(currentTab).contains("\u2739 ")) {
                        // ПОРЯДОК ВАЖЕН !
                        startActivity(intent);
                        goToSaveAs();
                    } else {
                        saveFile(false);
                        startActivity(intent);
                    }
                }
            });

            saveDialog.setNegativeButton(getString(R.string.dlg_negative), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(intent);

                }
            });
            saveDialog.show();
        } else
            startActivity(intent);
    }


    /*
     * it don`t updates text in others tabs with the same file opened
     * (but should it?)*/
    private void saveFile(boolean needToOpenFile) {
        refreshPagerAdapter();
        refreshLeafFragment();
        editText = leafFragment.getEditText();

        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            OutputStreamWriter out = new OutputStreamWriter(outputStream);
            out.write(editText.getText().toString());
            out.close();
            Toast.makeText(getApplicationContext(), getString(R.string.msg_file_saved), Toast.LENGTH_SHORT).show();
            if (needToOpenFile) openFile(true);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_file_not_saved), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void openFile(boolean blef) {
        refreshPagerAdapter();
        refreshLeafFragment();
        editText = leafFragment.getEditText();
        String title;
        try {

            //InputStream inputStream = openFileInput(file); // for internal
            FileInputStream inputStream = new FileInputStream(new File(fileName));
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();

            title = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            editText.setText(builder.toString());
            leafFragment.setUntaughtText(builder.toString());
            refreshPagerAdapter();
            pagerAdapter.setPageTitle(rootFragment.getCurrentTab(), fileName);
            tabLayout.getTabAt(rootFragment.getCurrentTab()).setText(title);
            if (!blef) {
                leafFragment.setUndoStack(new ArrayDeque<String>());
                leafFragment.setRedoStack(new ArrayDeque<String>());
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_file_not_opened), Toast.LENGTH_SHORT).show();
            t.printStackTrace();
        }
    }

    private void saveTextToBuffer() {
        refreshLeafFragment();
        editText = leafFragment.getEditText();
        buffer = editText.getText().toString();
        Toast.makeText(getApplicationContext(), buffer, Toast.LENGTH_SHORT).show();
    }

    private void refreshPagerAdapter() {
        pagerAdapter = rootFragment.getAdapter();
    }

    private void refreshLeafFragment() {
        leafFragment = (LeafMatEgoFragment) pagerAdapter.getItem(rootFragment.getCurrentTab());
    }

    private long backPressed;

    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), getString(R.string.msg_press_again),
                    Toast.LENGTH_SHORT).show();
        backPressed = System.currentTimeMillis();
    }

}


































