package com.rae.cnblogs.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.rae.cnblogs.widget.compat.RaeDragDownCompat;

/**
 * 可拖动的视图
 * Created by ChenRui on 2017/1/9 0009 9:14.
 */
public class RaeDrawerLayout extends FrameLayout {

    private RaeDragDownCompat mDragDownCompat;

    public RaeDrawerLayout(Context context) {
        super(context);
        initView();
    }

    public RaeDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RaeDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        mDragDownCompat = new RaeDragDownCompat(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mDragDownCompat.setDragView(getChildAt(0));
        Log.w("rae", String.format("onLayout,top=%d", top));
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean ret = super.drawChild(canvas, child, drawingTime);
        float scrollPercent = mDragDownCompat.getScrollPercent();
        float op = (1 - scrollPercent);
        if (op > 0) {
            final int mScrimColor = 0x99000000;
            final int baseAlpha = (mScrimColor & 0xff000000) >>> 24;
            final int alpha = (int) (baseAlpha * op);
            final int color = alpha << 24;
            canvas.clipRect(0, 0, getWidth(), mDragDownCompat.getDragViewTop());
            canvas.drawColor(color);
        }
        return ret;
    }

    @Override
    public void computeScroll() {
        mDragDownCompat.computeScroll();
    }

    public boolean dispatchTouchEventSupport(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mDragDownCompat.dispatchTouchEvent(ev);

        return this.dispatchTouchEventSupport(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragDownCompat.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragDownCompat.processTouchEvent(event);
        return true;
    }


    /**
     * 显示/隐藏
     */
    public void toggleSmoothScroll() {
        mDragDownCompat.setDragView(getChildAt(0));
        mDragDownCompat.toggleSmoothScroll();
    }


    /**
     * 设置拖动Touch处理
     */
    public void setDragDownHandler(RaeDragDownCompat.DragDownHandler dragDownHandler) {
        mDragDownCompat.setDragDownHandler(dragDownHandler);
    }
}
