package com.example.mengxiangda.demo6_iosimageedit;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by mengxiangda on 2018/8/22.
 */

public class MyParentView extends FrameLayout {

    /* 做图片缩放需要的参数 */
    float baseValue;
    float mprefocusX;
    float mprefocusY;

    private MyChildrenView mTargetView;

    private float mSmallScaleY;
    private float mSmallScaleX;
    private float mBigScaleX;
    private float mBigScaleY;
    private int mWidth;
    private int mHeight;


    public MyParentView(Context context) {
        this(context, null);
    }

    public MyParentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyParentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTargetView = (MyChildrenView) getChildAt(0);
        mSmallScaleX = mTargetView.getScaleX();
        mSmallScaleY = mTargetView.getScaleY();
        mBigScaleX = mSmallScaleX * 3;
        mBigScaleY = mSmallScaleY * 3;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getPointerCount() == 2) {
            //当切换为2指的时候, 就判断子view有没有正在执行的动作
            mTargetView.change();
            return true;
        } else if (ev.getPointerCount() == 1) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mprefocusX = 0;
                mprefocusY = 0;
                baseValue = 0;
                break;
            case MotionEvent.ACTION_UP:
                mprefocusX = 0;
                mprefocusY = 0;
                baseValue = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) {
                    float sumX = 0, sumY = 0;
                    final float focusX;
                    final float focusY;
                    float xSpan = event.getX(0) - event.getX(1);
                    float ySpan = event.getY(0) - event.getY(1);
                    float value = (float) Math.sqrt(xSpan * xSpan + ySpan * ySpan);// 计算两点的距离
                    if (baseValue == 0) {
                        baseValue = value;
                    } else {
                        for (int i = 0; i < 2; i++) {
                            sumX += event.getX(i);
                            sumY += event.getY(i);
                        }
                        focusX = sumX / 2;
                        focusY = sumY / 2;

                        if (value - baseValue >= 10 || value - baseValue <= -10) {
                            float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
                            baseValue = value;
                            int left = (int) mTargetView.getTranslationX();
                            int top = (int) mTargetView.getTranslationY();
                            img_scale(scale, focusX - left, focusY - top);  //缩放图片
                        }
                        if (mprefocusX != 0 && mprefocusY != 0) {
                            img_transport(focusX - mprefocusX, focusY - mprefocusY);
                        }

                        mprefocusX = focusX;
                        mprefocusY = focusY;
                    }
                } else if (event.getPointerCount() == 1) {
                    return false;
                }

                break;
        }
        return true;
    }

    private void img_transport(float x, float y) {
        float limitX = (mTargetView.getScaleX() * mWidth - mWidth) / 2;
        float translationX = mTargetView.getTranslationX() + x;

        float limitY = (mTargetView.getScaleY() * mHeight - mHeight) / 2;
        float translationY = mTargetView.getTranslationY() + y;


        if (mTargetView != null) {
            if (Math.abs(translationX) < limitX) {
                mTargetView.setTranslationX(translationX + x);
            } else {
                if (translationX < 0) {
                    mTargetView.setTranslationX(-limitX);
                } else {
                    mTargetView.setTranslationX(limitX);
                }
            }
            if (Math.abs(translationY) < limitY) {
                mTargetView.setTranslationY(translationY + y);
            } else {
                if (translationY < 0) {
                    mTargetView.setTranslationY(-limitY);
                } else {
                    mTargetView.setTranslationY(limitY);
                }
            }
        }
    }

    private void img_scale(float scale, float x, float y) {
        if (mTargetView != null) {
            float scaleX = mTargetView.getScaleX() * scale;
            float scaleY = mTargetView.getScaleY() * scale;

            if (scaleX > mSmallScaleX && scaleX < mBigScaleX) {
                mTargetView.setScaleX(scaleX);
            }
            if (scaleY > mSmallScaleY && scaleY < mBigScaleY) {
                mTargetView.setScaleY(scaleY);
            }

        }
    }

    public void log(String string) {
        Log.d("MyView", string);
    }


}
