package wdwd.com.androidpractice.GestureConflict;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by tomchen on 16/11/15.
 */

public class HorizontalScrollViewEx extends ViewGroup {

    private final static String TAG = "HorizontalScrollViewEx";

    private boolean intercepted;
    private int xDown;
    private int yDown;
    private Scroller mScroller;
    private int childWidth = 0;
    private VelocityTracker mVelocityTracker;
    private int touchSlop;
    private int mChildIndex;
    private int mChildrenSize;
    private int lastInterceptX;
    private int lastInterceptY;

    public HorizontalScrollViewEx(Context context) {
        super(context);
        initView();
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        if (mScroller == null) {
            mScroller = new Scroller(getContext());
        }

        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                childView.layout(i * childView.getMeasuredWidth(), 0, (i + 1) * childView.getMeasuredWidth(), childView.getMeasuredHeight());
            }

            childWidth = getChildAt(0).getWidth();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i(TAG, "onInterceptTouchEvent " + ev.getAction());
        xDown = (int) ev.getX();
        yDown = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    intercepted = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float y = ev.getY();
                float deltaX = x - lastInterceptX;
                float deltaY = y - lastInterceptY;
                // 横向滑动
                intercepted = Math.abs(deltaX) > Math.abs(deltaY);
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
        }

        lastInterceptX = xDown;
        lastInterceptY = yDown;
        return intercepted;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int xMove = (int) event.getX();
        int yMove = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouchEvent down");
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }

                int deltaX = xMove - xDown;
                Log.i(TAG, "onTouchEvent move deltaX " + (-deltaX));
                scrollBy(-deltaX, 0);
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent up scroll x" + getScrollX());
                int scrollX = getScrollX();
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                if (Math.abs(xVelocity) >= touchSlop) {
                    mChildIndex = xVelocity > 0 ? mChildIndex - 1 : mChildIndex + 1;
                } else {
                    mChildIndex = (scrollX + childWidth / 2) / childWidth;
                }
                mChildIndex = Math.max(0, Math.min(mChildIndex, getChildCount() - 1));

                int dx = mChildIndex * childWidth - scrollX;
                smoothScrollBy(dx, 0);
                mVelocityTracker.clear();
                break;
        }

        xDown = xMove;
        return true;
    }

    private void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(getScrollX(), 0, dx, 0, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }
}
