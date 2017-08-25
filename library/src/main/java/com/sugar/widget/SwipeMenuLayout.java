package com.sugar.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.sugar.R;

/**
 * Android swipe show menu
 *
 * @author Sugar
 */
public class SwipeMenuLayout extends ViewGroup {

    private final static String TAG = "SwipeMenuLayout";

    private Scroller mScroller;

    private int menuPosition = 0;

    /**
     * menu position is right (default)
     */
    private final static int POSITION_RIGHT = 0;
    private final static int POSITION_LEFT = 1;
    private final static int POSITION_TOP = 2;
    private final static int POSITION_BOTTOM = 3;

    /**
     * menu state open value
     */
    private final static int MENU_OPEN = 0;
    /**
     * menu state
     */
    private final static int MENU_CLOSE = 1;

    private int state = MENU_CLOSE;

    private int menuWidth;
    private int menuHeight;

    private View content;
    private View menu;

    public SwipeMenuLayout(Context context) {
        this(context, null);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mScroller = new Scroller(context);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeMenuLayout);
            menuPosition = a.getInt(R.styleable.SwipeMenuLayout_menu_position, POSITION_RIGHT);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (content == null) {
            content = getChildAt(0);
            menu = getChildAt(1);
        }
        content.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
        switch (menuPosition) {
            case POSITION_RIGHT:
                menu.layout(getMeasuredWidth(), 0, getMeasuredWidth() + menu.getMeasuredWidth(), getMeasuredHeight());
                break;
            case POSITION_LEFT:
                menu.layout(-menu.getMeasuredWidth(), 0, 0, getMeasuredHeight());
                break;
            case POSITION_TOP:
                menu.layout(0, -menu.getMeasuredHeight(), getMeasuredWidth(), 0);
                break;
            case POSITION_BOTTOM:
                menu.layout(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight() + menu.getMeasuredHeight());
                break;
        }
        menuWidth = menu.getMeasuredWidth();
        menuHeight = menu.getMeasuredHeight();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (state == MENU_OPEN) {
                    switch (menuPosition) {
                        case POSITION_RIGHT:
                            if (ev.getX() < getMeasuredWidth() - menuWidth) {
                                return true;
                            }
                            break;
                        case POSITION_LEFT:
                            if (ev.getX() > menuWidth) {
                                return true;
                            }
                            break;
                        case POSITION_TOP:
                            if (ev.getY() > menuHeight) {
                                return true;
                            }
                            break;
                        case POSITION_BOTTOM:
                            if (ev.getY() < getMeasuredHeight() - menuHeight) {
                                return true;
                            }
                            break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private float currentX;
    private float currentY;
    private float downX;
    private float downY;
    private long downTime;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                currentX = event.getX();
                currentY = event.getY();
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                switch (menuPosition) {
                    case POSITION_RIGHT: {
                        int distanceX = -(int) (event.getX() - currentX);
                        if (getScrollX() + distanceX > 0 && getScrollX() + distanceX < menuWidth) {
                            scrollBy(distanceX, 0);
                        } else {
                            if (distanceX > 0) {
                                scrollBy(menuWidth - getScrollX(), 0);
                            } else {
                                scrollBy(-getScrollX(), 0);
                            }
                        }
                        currentX = event.getX();
                    }
                    break;
                    case POSITION_LEFT: {
                        int distanceX = -(int) (event.getX() - currentX);
                        if (getScrollX() + distanceX < 0 && getScrollX() + distanceX > -menuWidth) {
                            scrollBy(distanceX, 0);
                        } else {
                            if (distanceX < 0) {
                                scrollBy(-menuWidth - getScrollX(), 0);
                            } else {
                                scrollBy(-getScrollX(), 0);
                            }
                        }
                        currentX = event.getX();
                    }
                    break;
                    case POSITION_TOP: {
                        int distanceY = -(int) (event.getY() - currentY);
                        if (getScrollY() + distanceY < 0 && getScrollY() + distanceY > -menuHeight) {
                            scrollBy(0, distanceY);
                        } else {
                            if (distanceY < 0) {
                                scrollBy(0, -menuHeight - getScrollY());
                            } else {
                                scrollBy(0, -getScrollY());
                            }
                        }
                        currentY = event.getY();
                    }
                    break;
                    case POSITION_BOTTOM: {
                        int distanceY = -(int) (event.getY() - currentY);
                        if (getScrollY() + distanceY > 0 && getScrollY() + distanceY < menuHeight) {
                            scrollBy(0, distanceY);
                        } else {
                            if (distanceY > 0) {
                                scrollBy(0, menuHeight - getScrollY());
                            } else {
                                scrollBy(0, -getScrollY());
                            }
                        }
                        currentY = event.getY();
                    }
                    break;
                }

                break;
            case MotionEvent.ACTION_UP:
                switch (menuPosition) {
                    case POSITION_RIGHT: {
                        if (getScrollX() > menuWidth / 2) {
                            smoothScroll(menuWidth, 0);
                            state = MENU_OPEN;
                        } else {
                            smoothScroll(0, 0);
                            state = MENU_CLOSE;
                        }
                        if (downX == event.getX() && downY == event.getY() && (System.currentTimeMillis() - downTime) < 100) {
                            if (downX < getMeasuredWidth() - menuWidth) {
                                closeMenu();
                            }
                        }
                    }
                    break;
                    case POSITION_LEFT: {
                        if (getScrollX() < -menuWidth / 2) {
                            smoothScroll(-menuWidth, 0);
                            state = MENU_OPEN;
                        } else {
                            smoothScroll(0, 0);
                            state = MENU_CLOSE;
                        }
                        if (downX == event.getX() && downY == event.getY() && (System.currentTimeMillis() - downTime) < 100) {
                            if (downX > menuWidth) {
                                closeMenu();
                            }
                        }
                    }
                    break;
                    case POSITION_TOP: {
                        if (getScrollY() < -menuHeight / 2) {
                            smoothScroll(0, -menuHeight);
                            state = MENU_OPEN;
                        } else {
                            smoothScroll(0, 0);
                            state = MENU_CLOSE;
                        }
                        if (downX == event.getX() && downY == event.getY() && (System.currentTimeMillis() - downTime) < 100) {
                            if (downY > menuHeight) {
                                closeMenu();
                            }
                        }
                    }
                    break;
                    case POSITION_BOTTOM: {
                        if (getScrollY() > menuHeight / 2) {
                            smoothScroll(0, menuHeight);
                            state = MENU_OPEN;
                        } else {
                            smoothScroll(0, 0);
                            state = MENU_CLOSE;
                        }
                        if (downX == event.getX() && downY == event.getY() && (System.currentTimeMillis() - downTime) < 100) {
                            if (downY < getMeasuredHeight() - menuHeight) {
                                closeMenu();
                            }
                        }
                    }
                    break;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                if (getScrollX() > getChildAt(1).getMeasuredWidth() / 2) {
                    smoothScroll(getChildAt(1).getMeasuredWidth(), 0);
                    state = MENU_OPEN;
                } else {
                    smoothScroll(0, 0);
                    state = MENU_CLOSE;
                }
                break;
        }
        return true;
    }

    private void smoothScroll(int destX, int destY) {
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        int deltaX = destX - scrollX;
        int deltaY = destY - scrollY;
        mScroller.startScroll(scrollX, scrollY, deltaX, deltaY, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {//滚动是否结束，返回true表明滚动还未结束，同时在函数里获取currX和currY的值
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());//滚动到当前位置
            postInvalidate();//强制重新绘制View,在框架里,View 的绘制会重新调用到computeScroll方法，达到循环绘制的目的
        }
    }

    public void closeMenu() {
        smoothScroll(0, 0);
    }

}
