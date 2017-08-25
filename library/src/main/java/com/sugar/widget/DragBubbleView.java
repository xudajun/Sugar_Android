package com.sugar.widget;

import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * 拖拽气泡
 *
 * @author Sugar
 */
public class DragBubbleView extends View {

    private final static String TAG = "DragBubbleView";

    private float bubble_radius;//小球半径
    private int bubble_color;//小球颜色
    private String bubble_text;//小球显示字
    private int bubble_textColor; //字体颜色
    private float bubble_textSize;//字体大小

    /**
     * 静止气泡和动态气泡相关
     */
    private float mBubStillRadius;//不动气泡的半径
    private float mBubMoveableRadius;//可动气泡的半径
    private PointF mBubStillCenter;//不动气泡的圆心
    private PointF mBubMoveableCenter;//可动气泡的圆心
    private Paint mBubblePaint;//气泡的画笔

    /**
     * 贝塞尔曲线path相关
     */
    private Path mBezierPath;

    public DragBubbleView(Context context) {
        super(context);
        init(context, null);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mBubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bubble_radius = 24;
        bubble_color = Color.RED;
        bubble_text = "2";
        bubble_textColor = Color.WHITE;
        bubble_textSize = 24;
        mBubStillRadius = bubble_radius;
        mBubMoveableRadius = 24;
        mBezierPath = new Path();
    }

    private void initUI(int w, int h) {
        //设置两气泡圆心初始坐标
        if (mBubStillCenter == null) {
            mBubStillCenter = new PointF(w / 2, h / 2);
        } else {
            mBubStillCenter.set(w / 2, h / 2);
        }
        //设置动点坐标
        if (mBubMoveableCenter == null) {
            mBubMoveableCenter = new PointF(w / 2, h / 2);
        } else {
            mBubMoveableCenter.set(w / 2, h / 2);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initUI(w, h);
    }

    private float bubbleDistance; //气泡间距

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mBubStillCenter.x, mBubStillCenter.y, mBubStillRadius, mBubblePaint);
        canvas.drawCircle(mBubMoveableCenter.x, mBubMoveableCenter.y, mBubMoveableRadius, mBubblePaint);
        mBezierPath.reset();
        //2.相连曲线---贝塞尔曲线
        //计算控制点坐标---两个球圆心的中点.
        int iAnchorX = (int) ((mBubStillCenter.x + mBubMoveableCenter.x) / 2);
        int iAnchorY = (int) ((mBubStillCenter.y + mBubMoveableCenter.y) / 2);

        //计算 三角函数
        float cosTheta = (mBubStillCenter.x - mBubMoveableCenter.x) / bubbleDistance;
        float sinTheta = (mBubStillCenter.y - mBubMoveableCenter.y) / bubbleDistance;

        //B点动坐标计算
        float mBubMoveBX = mBubMoveableCenter.x - mBubMoveableRadius * sinTheta;
        float mBubMoveBY = mBubMoveableCenter.y + mBubMoveableRadius * cosTheta;

        //A点静坐标计算
        float mBubStillAX = mBubStillCenter.x - mBubStillRadius * sinTheta;
        float mBubStillAY = mBubStillCenter.y + mBubStillRadius * cosTheta;

        mBezierPath.moveTo(mBubStillAX, mBubStillAY);
        mBezierPath.quadTo(iAnchorX, iAnchorY, mBubMoveBX, mBubMoveBY);
        //画下半弧 C-->D-------------

        //C点动坐标计算
        float mBubMoveCX = mBubMoveableCenter.x + mBubMoveableRadius * sinTheta;
        float mBubMoveCY = mBubMoveableCenter.y - mBubMoveableRadius * cosTheta;

        //D点静坐标计算
        float mBubStillDX = mBubStillCenter.x + mBubStillRadius * sinTheta;
        float mBubStillDY = mBubStillCenter.y - mBubStillRadius * cosTheta;

        mBezierPath.lineTo(mBubMoveCX, mBubMoveCY);
        mBezierPath.quadTo(iAnchorX, iAnchorY, mBubStillDX, mBubStillDY);

        //释放资源,并画----------
        mBezierPath.close();
        canvas.drawPath(mBezierPath, mBubblePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                bubbleDistance = (float) Math.hypot(event.getX() - mBubStillCenter.x,
                        event.getY() - mBubStillCenter.y);
                mBubMoveableCenter.set(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                startResetAnimation();
                break;
        }
        return true;
    }

    private void startResetAnimation() {
        ValueAnimator animator = ValueAnimator.ofObject(
                new PointFEvaluator(),
                new PointF(mBubMoveableCenter.x, mBubMoveableCenter.y),
                new PointF(mBubStillCenter.x, mBubStillCenter.y));
        animator.setDuration(200);
        animator.setInterpolator(new OvershootInterpolator(2f));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBubMoveableCenter = (PointF) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }
}
