package com.sugar.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.sugar.R;

/**
 * @author Suagr
 */
public class SugarEditText extends AppCompatEditText {

    private int bgDefaultColor;
    private int borderColor;
    private int borderColorFocused;
    private int borderWidth;
    private float corners;

    public SugarEditText(Context context) {
        super(context);
    }

    public SugarEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SugarEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SugarEditText);
            bgDefaultColor = a.getColor(R.styleable.SugarEditText_et_bgDefault, ContextCompat.getColor(context, R.color.bg_default));
            borderColor = a.getColor(R.styleable.SugarEditText_et_borderColor, ContextCompat.getColor(context, android.R.color.transparent));
            borderColorFocused = a.getColor(R.styleable.SugarEditText_et_borderColorFocused, ContextCompat.getColor(context, android.R.color.transparent));
            corners = a.getDimension(R.styleable.SugarEditText_et_corners, 0);
            borderWidth = (int) a.getDimension(R.styleable.SugarEditText_et_borderWidth, 0);
            setBackgroundDrawable(getBgDrawable());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    private StateListDrawable getBgDrawable() {
        GradientDrawable defaultGd = new GradientDrawable();
        defaultGd.setColor(bgDefaultColor);
        defaultGd.setShape(GradientDrawable.RECTANGLE);
        defaultGd.setStroke(borderWidth, borderColor);
        defaultGd.setCornerRadius(corners);
        GradientDrawable pressedGd = new GradientDrawable();
        pressedGd.setColor(bgDefaultColor);
        pressedGd.setShape(GradientDrawable.RECTANGLE);
        pressedGd.setStroke(borderWidth, borderColorFocused);
        pressedGd.setCornerRadius(corners);

        StateListDrawable stateListDrawable = new StateListDrawable();
        LayerDrawable defaultLayer = new LayerDrawable(new Drawable[]{defaultGd});
        LayerDrawable activeLayer = new LayerDrawable(new Drawable[]{pressedGd});

        if (Build.VERSION.SDK_INT >= 14) {
            stateListDrawable.addState(new int[]{android.R.attr.state_hovered}, activeLayer);
        }

        stateListDrawable.addState(new int[]{android.R.attr.state_activated}, activeLayer);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, activeLayer);
        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, activeLayer);
        stateListDrawable.addState(new int[]{}, defaultLayer);

        return stateListDrawable;
    }

    public void setBgDefaultColor(int colorResId) {
        bgDefaultColor = ContextCompat.getColor(getContext(), colorResId);
        invalidate();
    }
}
