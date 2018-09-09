package com.example.tania.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Selection;
import android.util.AttributeSet;
import android.util.Log;

public class MainEditText extends android.support.v7.widget.AppCompatEditText {

    private Paint paintNumbers = new Paint();
    private Paint paintHighlight = new Paint();
    private Rect bounds = new Rect();
    public static boolean isNumbersNeeded = false;
    public static boolean isHighlightingNeeded = false;
    public static boolean startWith0 = true;
    private int pudding = 100;
    private int start = 1;

    public MainEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        /*
         * setting numbers
         */
        if (isNumbersNeeded) {
            int baseLine = getBaseline();
            start = startWith0 ? 0 : 1;
            paintNumbers.setTextSize(MainActivity.textSize - 2);
            paintNumbers.setColor(getResources().getColor(R.color.numeration));
            for (int i = 0; i < getLineCount(); i++) {
                int n = i + (startWith0 ? 0 : 1);
                canvas.drawText(n + "", 5, baseLine, paintNumbers);
                baseLine += getLineHeight();
            }
            if (getLineCount() < 10) {
                pudding = (int) ((MainActivity.textSize - 1) * 1.2);
            } else if (getLineCount() < 100) {
                pudding = (int) ((MainActivity.textSize - 1) * 1.7);
            } else {
                pudding = (int) ((MainActivity.textSize - 1) * 2);
            }
            canvas.drawLine(pudding + 0, 0, pudding + 0, canvas.getHeight() * getLineCount(), paintNumbers);
            findViewById(R.id.editText).setPadding(pudding + 5, 4, 4, 6);
        }

        /*
         * highlighting current line
         */
        if (isHighlightingNeeded) {
            paintHighlight.setColor(getResources().getColor(R.color.line_highlighting));
            int lineNumber = 0;
            try {
                lineNumber = getLayout().getLineForOffset(getSelectionStart());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            getLineBounds(lineNumber, bounds);
            canvas.drawRect(bounds, paintHighlight);
        }

        super.onDraw(canvas);
    }
}
