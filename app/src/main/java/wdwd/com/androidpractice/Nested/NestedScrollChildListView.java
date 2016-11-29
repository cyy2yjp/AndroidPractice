package wdwd.com.androidpractice.Nested;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by tomchen on 16/11/24.
 */

public class NestedScrollChildListView extends View implements NestedScrollingChild {

    private final static String TAG = "NestedScrollChild";
    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private float mLastX;//手指在频幕上最后的x位置
    private float mLastY;//手指在屏幕上最后的y位置

    private float mDownX;//手指第一次落下的位置（忽略）
    private float mDownY; //手指第一次落下的y位置

    private int[] consumed = new int[2];//消耗的距离
    private int[] offsetInWindow = new int[2]; //窗口偏移
    private int mNestedYOffset = 0;

    public NestedScrollChildListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
        setClickable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mLastX = x;
                mLastY = y;
                mNestedYOffset = 0;
                //当开始滑动的时候，告诉父View
                startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL | ViewCompat.SCROLL_AXIS_VERTICAL);
                break;
            case MotionEvent.ACTION_MOVE:
                int dy = (int) (y - mDownY);
                int dx = (int) (x - mDownX);

                //分发触屏事件给父类处理
                if (dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)) {
                    //校正自己的距离
                    dx -= consumed[0];
                    dy -= consumed[1];

                    if (ev != null) {
                        ev.offsetLocation(0, offsetInWindow[1]);
                        mNestedYOffset += consumed[1];
                    }
                }

                dispatchNestedScroll(dx, dy, dx - consumed[0], dy - consumed[1], offsetInWindow);

                offsetLeftAndRight(dx);
                offsetTopAndBottom(dy);
                break;

            case MotionEvent.ACTION_UP:
                stopNestedScroll();
                break;
        }
        mLastX = x;
        mDownY = y;
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean startNestedScroll(int axes) {
        Log.i(TAG, "startNestedScroll axes");
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        Log.i(TAG, "dispatchNestedScroll");
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        Log.i(TAG, "dispatchNestedPreScroll");
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
