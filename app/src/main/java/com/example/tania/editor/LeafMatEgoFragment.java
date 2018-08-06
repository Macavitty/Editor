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

public class LeafMatEgoFragment extends Fragment {

    private EditText editText;
    private String editStr = "";
    private RootMatEgoFragment rootFragment;

    private String untaughtText = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaf_fragment, container, false);

        editText = view.findViewById(R.id.editText);
        rootFragment = (RootMatEgoFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.boss_fragment);
        editText.addTextChangedListener(new TextWatcher() {
            final Handler handler = new Handler();
            Runnable runnable;

            @Override
            public void afterTextChanged(Editable s) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        Log.d("run", "i am here");

                       /* //Toast.makeText(getApplicationContext(), "***", Toast.LENGTH_SHORT).show();//
                        if (!changesBufferUndo.empty() && MainActivity.isReady) {
                            String s = changesBufferUndo.peek();
                            if (!s.equals(editText.getText().toString())) {
                                //Toast.makeText(getApplicationContext(), "**", Toast.LENGTH_SHORT).show();//
                                changesBufferUndo.add(editText.getText().toString());
                                //Toast.makeText(getApplicationContext(), "***", Toast.LENGTH_SHORT).show();//
                            }
                        } else changesBufferUndo.push(editText.getText().toString());*/
                    }
                };
                handler.postDelayed(runnable, 500);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(runnable);
            }
        });

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

    public String getUntaughtText() {
        return untaughtText;
    }

    public void setUntaughtText(String untaughtText) {
        this.untaughtText = untaughtText;
    }
}
