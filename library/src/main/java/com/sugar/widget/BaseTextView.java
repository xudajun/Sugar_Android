package com.sugar.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * TextView Base
 *
 * @author Sugar
 */
public class BaseTextView extends AppCompatTextView {

    public BaseTextView(Context context) {
        this(context, null);
    }

    public BaseTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BaseTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
