package wdwd.com.androidpractice.Nested;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;


/**
 * 初步看 NestedScrollingParent 和 NestedScrollingChild 配对使用, 子view 调用startNestedScroll 回去寻找NestedScrollParent传入自己滑动的方向 并询问是否可以
 * 滑动，如果可以嵌套滑动 调用帮助类
 * <p>/**
 * Created by tomchen on 16/11/24.
 */
public class NestedParent extends FrameLayout implements NestedScrollingParent {

    private final static String TAG = "NestedParent";

    private NestedScrollingParentHelper mNestedScrollingParentHelper;

    public NestedParent(Context context) {
        super(context);
        init();
    }

    public NestedParent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedParent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        //没有禁用 并且是垂直滑动
        return isEnabled() && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    /**
     * 如果允许嵌套滑动调用这个方法
     *
     * @param child
     * @param target
     * @param nestedScrollAxes
     */
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        //记录滑动的方向
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }


    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
    }

    /**
     * @param target   NestedScrollChild
     * @param dx       横向滑动的距离
     * @param dy       垂直滑动的距离
     * @param consumed 消费 默认都未0  如果parent 需要消费可以设置为0
     */
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        final View child = target;
        if (dx > 0) {
            if (child.getRight() + dx > getWidth()) {
                dx = child.getRight() + dx - getWidth(); // 多出来的
                offsetLeftAndRight(dx);
                consumed[0] += dx; //父亲消耗
            }
        } else {
            if (child.getLeft() + dx < 0) {
                dx = dx + child.getLeft();
                offsetLeftAndRight(dx);

                consumed[0] += dx; //父亲消耗
            }
        }

        if (dy > 0) {
            if (child.getBottom() + dy > getHeight()) {
                dy = child.getBottom() + dy - getHeight();
                offsetTopAndBottom(dy);
                consumed[1] += dy;
            }
        } else {
            if (child.getTop() + dy < 0) {
                dy = dy + child.getTop();
                offsetTopAndBottom(dy);
                Log.d(TAG, "dy:" + dy);
                consumed[1] += dy;//父亲消耗
            }
        }
        Log.i(TAG, "onNestedPreScroll" + dx + " dy" + dy + "consumed " + consumed[0] + "+++" + consumed[1]);
    }

    // 参数target:同上
    // 参数dxConsumed:表示target已经消费的x方向的距离
    // 参数dyConsumed:表示target已经消费的x方向的距离
    // 参数dxUnconsumed:表示x方向剩下的滑动距离
    // 参数dyUnconsumed:表示y方向剩下的滑动距离
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        Log.d(TAG, "----父布局onNestedScroll----------------");
        Log.i(TAG, "onNestedScroll  dxConsumed" + dxConsumed + "dyConsumed" + dyConsumed + "dxUnconsumed " + dxUnconsumed + "dyUnconsumed" + dyUnconsumed);
    }


    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.d(TAG, "----父布局onNestedFling----------------");
        Log.i(TAG, "onNestedFling  velocityX" + velocityX + "velocityY" + velocityY + "consumed " + consumed);
        return true;
    }


    public int getNestedScrollAxes() {
        Log.d(TAG, "----父布局getNestedScrollAxes----------------");
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

}
