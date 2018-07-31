package com.example.tania.editor;

import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Map;
import java.util.Stack;

public class LeafMatEgoFragment extends Fragment {

    private String title;
    private EditText editText;
    private MainPagerAdapter pagerAdapter;

    private Map<Integer, Stack<String>> bufferUndoMap;
    private Map<Integer, Stack<String>> bufferRedoMap;
    private Stack<String> bufferUndoStack;
    private Stack<String> bufferRedoStack;
    private String editStr = "";
    private RootMatEgoFragment rootMatEgoFragment;
    private UndoRedoTextWatcher textWatcher;

    static {
        Log.e("leaf load: ", "true");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaf_fragment, container, false);
        Bundle bundle = getArguments();
        title = bundle.getString("data");

        textWatcher = new UndoRedoTextWatcher();

        editText = view.findViewById(R.id.editText);
        rootMatEgoFragment = (RootMatEgoFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.boss_fragment);
        pagerAdapter = rootMatEgoFragment.getAdapter();
        bufferUndoMap = rootMatEgoFragment.getBufferUndoMap();
        bufferRedoMap = rootMatEgoFragment.getBufferRedoMap();

        setTextWatcher();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        editText.setTextSize(MainActivity.textSize);
        editText.setTypeface(Typeface.create(MainActivity.typefaceFont, MainActivity.typefaceStyle));
        editText.setTextColor(MainActivity.textColor);
        editText.setBackgroundColor(MainActivity.backgroundColor);
        editText.setPadding(6, 4, 6, 4);
        editText.setSelection(MainActivity.cursorPosition);

        if (MainActivity.capLitera) editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        else editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        editText.setSingleLine(false);

        /*if (MainActivity.textFromBufferIsNeeded) {
            editText.setText("buffer");
            MainActivity.textFromBufferIsNeeded = false;
        }*/

    }

    public EditText getEditText() {
        return editText;
    }

    private class UndoRedoTextWatcher implements TextWatcher{
        final Handler handler = new Handler();
        Runnable runnable;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            handler.removeCallbacks(runnable);
        }

        @Override
        public void afterTextChanged(Editable s) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (MainActivity.isReady) {
                        Log.e("run", "i am here");
                        if (bufferUndoMap.get(rootMatEgoFragment.getCurrentTab()) != null) {
                            editStr = editText.getText().toString();
                            bufferUndoStack = bufferUndoMap.get(rootMatEgoFragment.getCurrentTab());
                            bufferRedoStack = bufferRedoMap.get(rootMatEgoFragment.getCurrentTab());
                            String str = bufferUndoStack.empty() ? "" : bufferUndoStack.peek();

                            if (!str.equals(editStr) && !MainActivity.isPageDeleted) {
                                bufferUndoStack.push(editStr);
                                Log.e("push: " + rootMatEgoFragment.getCurrentTab(), editStr);
                                if (MainActivity.pageJustHasBeenAdded)
                                    MainActivity.updatesNumberLeft--;
                                Log.e("Left: ", MainActivity.updatesNumberLeft + "");
                            }
                            if (MainActivity.pageJustHasBeenAdded && MainActivity.updatesNumberLeft == -1) {
                                bufferUndoStack.clear();
                                Log.e("clear: ", "done");
                                bufferUndoStack.push("");
                                Log.e("push space: ", "done");
                                MainActivity.pageJustHasBeenAdded = false;
                            }

                            if (MainActivity.isPageDeleted) {
                                editText.setText(bufferUndoStack.peek());
                                MainActivity.isPageDeleted = false;
                            }
                        }
                    }

                }
            };
            handler.postDelayed(runnable, 500);
        }
    }

    public void removeTextWatcher(){
        if (!editText.equals(null) && !textWatcher.equals(null)) editText.removeTextChangedListener(textWatcher);
    }
    public void setTextWatcher(){
        if (editText != null && textWatcher != null) editText.addTextChangedListener(textWatcher);
    }
}
