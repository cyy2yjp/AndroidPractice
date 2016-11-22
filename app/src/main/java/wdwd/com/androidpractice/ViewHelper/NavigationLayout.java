package wdwd.com.androidpractice.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;

import wdwd.com.androidpractice.R;

/**
 * Created by tomchen on 16/11/22.
 */

public class NavigationLayout extends FrameLayout {

    private final String TAG = "NavigationLayout";

    private ViewDragHelper mViewDragHelper;
    private VelocityTracker mVelocityTracker;
    private View leftMenu;
    private View mainView;

    private int menu_width;
    //main视图距离在ViewGroup距离左边的距离
    private int mainMarginLeft;
    private int horizontalRange;


    private int leftViewHeight;
    private int leftViewWidth;
    private GestureDetectorCompat mGestureDectory;

    public NavigationLayout(Context context) {
        super(context);
        init();
    }

    public NavigationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavigationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void dispatchDragEvent(int marginLeft) {
        float percent = marginLeft / (float) horizontalRange;
        //根据滑动的距离的比例，进行带有动画的缩小和放大View
        animateView(percent);
        //进行回调的百分比

        //TODO called when menu is close or open
    }

    /**
     * 根据滑动的距离的比例，进行带有动画的缩小和放大view
     *
     * @param percent
     */
    private void animateView(float percent) {
        float f1 = 1 - percent * 0.3f;
        Log.e(TAG, String.format("percent %1s", percent));
        //main_view horizontal axial 根据百分比缩放
        mainView.setScaleX(f1);
        mainView.setScaleY(f1);

        //沿着水平x轴平移
        leftMenu.setTranslationX(-leftViewWidth / 2.3f + leftViewWidth / 2.3f * percent);
        //left menu 缩放
        leftMenu.setScaleY(0.5f + 0.5f * percent);
        leftMenu.setScaleY(0.5f + 0.5f * percent);
        leftMenu.setAlpha(percent);

        Integer colorFilter = evaluate(percent, Color.BLACK, Color.TRANSPARENT);
        getBackground().setColorFilter(colorFilter, PorterDuff.Mode.SRC_OVER);
        mainView.getBackground().setColorFilter(colorFilter, PorterDuff.Mode.SRC_OVER);
    }

    private Integer evaluate(float fraction, Object startValue, Integer endValue) {
        int startInt = (int) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;
        int endInt = endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return ((startA + (int) (fraction * (endA - startA))) << 24)
                | ((startR + (int) (fraction * (endR - startR))) << 16)
                | ((startG + (int) (fraction * (endG - startG))) << 8)
                | ((startB + (int) (fraction * (endB - startB))));
    }

    public void open(boolean animate) {
        if (animate) {
            if (mViewDragHelper.smoothSlideViewTo(mainView, horizontalRange, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            dispatchDragEvent(horizontalRange);
        }
    }

    public void close(boolean animate) {
        if (animate) {
            if (mViewDragHelper.smoothSlideViewTo(mainView, 0, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mainView.layout(0, 0, leftViewWidth, leftViewHeight);
            dispatchDragEvent(0);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            Log.i(TAG, "computeScroll");
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        leftMenu = findViewById(R.id.queen_button);
        mainView = findViewById(R.id.queen_bottom);
        mGestureDectory = new GestureDetectorCompat(getContext(), new YScrollDetector());
    }

    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragCallback());
        mVelocityTracker = VelocityTracker.obtain();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev) && mGestureDectory.onTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0), resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        leftViewHeight = leftMenu.getMeasuredHeight();
        leftViewWidth = leftMenu.getMeasuredWidth();

        horizontalRange = (int) (leftViewWidth * 0.4);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, String.format("main view width %1d  left view height %d", leftViewWidth, leftViewHeight));
        leftMenu.layout(0, 0, leftViewWidth, leftMenu.getMeasuredHeight());
        mainView.layout(mainMarginLeft, 0, mainMarginLeft + mainView.getMeasuredWidth(), leftViewHeight);

    }

    public class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == mainView) {
                mainMarginLeft = left;
            } else {
                mainMarginLeft = mainMarginLeft + left;
            }

            if (mainMarginLeft < 0) {
                mainMarginLeft = 0;
            } else if (mainMarginLeft > horizontalRange) {
                mainMarginLeft = horizontalRange;
            }

            Log.i(TAG, changedView.toString() + "marginleft" + mainMarginLeft);

            dispatchDragEvent(mainMarginLeft);
        }


        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //垂直水平运动
            return 0;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int newLeft = left;
            if (left > horizontalRange) {
                newLeft = horizontalRange;
            } else if (left < 0) {
                newLeft = 0;
            }
            return newLeft;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return horizontalRange;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (xvel > 0) {
                open(true);
            } else if (xvel < 0) {
                close(true);
            } else if (releasedChild == mainView && mainMarginLeft > horizontalRange * 0.3) {
                open(true);
            } else if (releasedChild == leftMenu && mainMarginLeft > horizontalRange * 0.7) {
                open(true);
            } else {
                close(true);
            }

//            Log.i(TAG, String.format("xvel %1d horizontalRange %2f", mainMarginLeft, horizontalRange / 2.0));
//            if (mainMarginLeft < horizontalRange) {
//                settleWidth = 0;
//            } else if (mainMarginLeft > horizontalRange) {
//                settleWidth = leftViewWidth;
//            }
//
//            Log.i(TAG, String.format("settleWidth %1d , top %2d", settleWidth, releasedChild.getTop()));
//            if (mViewDragHelper.settleCapturedViewAt(settleWidth, releasedChild.getTop())) {
//                ViewCompat.postInvalidateOnAnimation(NavigationLayout.this);
//            }
        }
    }

    private class YScrollDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return Math.abs(distanceY) <= Math.abs(distanceX);
        }
    }
}
