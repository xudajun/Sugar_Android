package com.sugar.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.sugar.R;

/**
 * Awesome TextView
 *
 * @author Sugar
 */
public class SugarTextView extends BaseTextView {

    private int bgDefaultColor;
    private int bgPressedColor;
    private int bgDisableColor;
    private int borderColor;
    private int borderWidth;
    private float corners;
    private int tvDefaultColor;
    private int tvDisabledColor;
    private int tvPressedColor;

    public SugarTextView(Context context) {
        this(context, null);
    }

    public SugarTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SugarTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SugarTextView);
            bgDefaultColor = a.getColor(R.styleable.SugarTextView_bgDefault, ContextCompat.getColor(context, R.color.bg_default));
            bgPressedColor = a.getColor(R.styleable.SugarTextView_bgPressed, ContextCompat.getColor(context, R.color.bg_pressed));
            bgDisableColor = a.getColor(R.styleable.SugarTextView_bgDisable, ContextCompat.getColor(context, R.color.bg_disable));
            borderColor = a.getColor(R.styleable.SugarTextView_borderColor, ContextCompat.getColor(context, android.R.color.transparent));
            corners = a.getDimension(R.styleable.SugarTextView_corners, 0);
            borderWidth = (int) a.getDimension(R.styleable.SugarTextView_borderWidth, 0);
            tvDefaultColor = a.getColor(R.styleable.SugarTextView_textColorDefault, ContextCompat.getColor(context, R.color.textColor_default));
            tvDisabledColor = a.getColor(R.styleable.SugarTextView_textColorDisable, ContextCompat.getColor(context, R.color.textColor_light));
            tvPressedColor = a.getColor(R.styleable.SugarTextView_textColorPressed, ContextCompat.getColor(context, R.color.textColor_gray));
            setBackgroundDrawable(getBgDrawable());
            setTextColor(getTextColor());
        }
    }

    private StateListDrawable getBgDrawable() {
        GradientDrawable defaultGd = new GradientDrawable();
        defaultGd.setColor(bgDefaultColor);
        defaultGd.setShape(GradientDrawable.RECTANGLE);
        defaultGd.setStroke(borderWidth, borderColor);
        defaultGd.setCornerRadius(corners);
        GradientDrawable pressedGd = new GradientDrawable();
        pressedGd.setColor(bgPressedColor);
        pressedGd.setShape(GradientDrawable.RECTANGLE);
        pressedGd.setStroke(borderWidth, borderColor);
        pressedGd.setCornerRadius(corners);
        GradientDrawable disabledGd = new GradientDrawable();
        disabledGd.setColor(bgDisableColor);
        disabledGd.setStroke(borderWidth, borderColor);
        disabledGd.setShape(GradientDrawable.RECTANGLE);
        disabledGd.setCornerRadius(corners);

        StateListDrawable stateListDrawable = new StateListDrawable();
        LayerDrawable defaultLayer = new LayerDrawable(new Drawable[]{defaultGd});
        LayerDrawable activeLayer = new LayerDrawable(new Drawable[]{pressedGd});
        LayerDrawable disabledLayer = new LayerDrawable(new Drawable[]{disabledGd});

        if (Build.VERSION.SDK_INT >= 14) {
            stateListDrawable.addState(new int[]{android.R.attr.state_hovered}, activeLayer);
        }

        stateListDrawable.addState(new int[]{android.R.attr.state_activated}, activeLayer);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, activeLayer);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, activeLayer);
        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, activeLayer);
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, disabledLayer);
        stateListDrawable.addState(new int[]{}, defaultLayer);

        return stateListDrawable;
    }

    private ColorStateList getTextColor() {
        return new ColorStateList(getStateList(), getColorList(tvDefaultColor, tvPressedColor, tvDisabledColor));
    }

    private int[] getColorList(int defaultColor, int activeColor, int disabledColor) {
        if (Build.VERSION.SDK_INT >= 14) {
            return new int[]{activeColor, activeColor, activeColor, activeColor, activeColor,
                    activeColor, disabledColor, defaultColor};
        } else {
            return new int[]{activeColor, activeColor, activeColor, activeColor, activeColor,
                    disabledColor, defaultColor};
        }
    }

    private static int[][] getStateList() {
        if (Build.VERSION.SDK_INT >= 14) {
            return new int[][]
                    {new int[]{android.R.attr.state_hovered}, new int[]{android.R.attr.state_activated},
                            new int[]{android.R.attr.state_focused}, new int[]{android.R.attr.state_selected},
                            new int[]{android.R.attr.state_pressed}, new int[]{android.R.attr.state_hovered},
                            new int[]{-android.R.attr.state_enabled}, new int[]{}};
        } else {
            return new int[][]
                    {new int[]{android.R.attr.state_activated}, new int[]{android.R.attr.state_focused},
                            new int[]{android.R.attr.state_selected}, new int[]{android.R.attr.state_pressed},
                            new int[]{android.R.attr.state_hovered}, new int[]{-android.R.attr.state_enabled},
                            new int[]{}};
        }
    }
    public void setBgDefaultColor(int colorResId) {
        bgDefaultColor = ContextCompat.getColor(getContext(), colorResId);
        setBackgroundDrawable(getBgDrawable());
    }

}
