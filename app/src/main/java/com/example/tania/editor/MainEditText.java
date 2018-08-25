package com.example.tania.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

public class MainEditText extends android.support.v7.widget.AppCompatEditText {

    Paint paint = new Paint();
    public static boolean isNumbersNeeded = false;
    int pudding = 100;

    public MainEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isNumbersNeeded) {
            int baseLine = getBaseline();
            paint.setTextSize(MainActivity.textSize - 2);
            paint.setColor(getResources().getColor(R.color.numeration));
            for (int i = 0; i < getLineCount(); i++) {
                canvas.drawText(i + "", 5, baseLine, paint);
                baseLine += getLineHeight();
            }
            if (getLineCount() < 10)
                pudding = (int) ((MainActivity.textSize - 1) * 1.2);
            else if (getLineCount() < 100) {
                pudding = (int) ((MainActivity.textSize - 1) * 1.7);
            } else
                pudding = (int) ((MainActivity.textSize - 1) * 2);
            canvas.drawLine(pudding + 0, 0, pudding + 0, canvas.getHeight() * getLineCount(), paint);
            findViewById(R.id.editText).setPadding(pudding + 5, 4, 4, 6);
        }


    }
}
