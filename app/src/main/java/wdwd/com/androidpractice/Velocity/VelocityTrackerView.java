package wdwd.com.androidpractice.Velocity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.Locale;

/**
 * Created by tomchen on 16/11/14.
 */

public class VelocityTrackerView extends View {

    private static final String sFormatStr = "velocityX=%f\nvelocityY=%f";
    private static final String TAG = "VelocityTrackerView";
    private VelocityTracker mVelocityTracker;
    private int mPointerId;
    private int mMaxVelocity;

    public VelocityTrackerView(Context context) {
        super(context);
        initView(context);
    }

    public VelocityTrackerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mMaxVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        setClickable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        Log.e(TAG, String.format(Locale.CHINA, "action %d", action));
        acquireVelocityTracker(event);
        final VelocityTracker velocityTracker = mVelocityTracker;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //求第一个触点的id,
                mPointerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                //求伪瞬时速度
                velocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                final float velocityX = velocityTracker.getXVelocity(mPointerId);
                final float velocityY = velocityTracker.getYVelocity(mPointerId);
                recodeInfo(velocityX, velocityY);//
                break;
            case MotionEvent.ACTION_UP:
                releaseVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                releaseVelocityTracker();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void recodeInfo(final float velocityX, final float velocityY) {
        final String info = String.format(Locale.CHINA, sFormatStr, velocityX, velocityY);
        Log.i(TAG, String.format(Locale.CHINA, "%s", info));
    }

}
