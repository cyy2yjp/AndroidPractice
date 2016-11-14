package wdwd.com.androidpractice.GestureDetector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * 如果是双击使用GestureDetector
 * <p>
 * Created by tomchen on 16/11/14.
 */

public class GestureDetectorView extends View implements GestureDetector.OnGestureListener {

    private GestureDetector mGestureDetector;

    public GestureDetectorView(Context context) {
        super(context);
        initView();
    }

    public GestureDetectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mGestureDetector = new GestureDetector(getContext(), this);
        mGestureDetector.setIsLongpressEnabled(false);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        //手指轻轻触摸频幕的一瞬间,由一个ACTION_DOWN 触发
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        //手指轻轻触摸频,尚未松开或拖动,由一个Action_DOWN 触发 ,注意和 onDown(）的区别,它强调的是没有松开或者拖动的状态
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        //手指 轻轻触摸频幕松开,伴随着一个motionEvent Action_UP 而触发,这是单击行为
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //手指按下屏幕并拖动,有1个Action down ,多个Action_Move 触发,这是拖动行为
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        //用户长久按着频幕不放,即长按
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //用户按下触摸屏,快速滑动松开,有一个ACTION_DOWN,多个ACTION_MOVE 和1个ACTION_UP 触发,这是快速滑动行为
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean consume = mGestureDetector.onTouchEvent(event);
        return consume;
    }
}
