package wdwd.com.androidpractice.ViewHelper;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import wdwd.com.androidpractice.R;

/**
 * Created by tomchen on 16/11/17.
 */

public class QuterLayout extends FrameLayout {

    private static final int MIN_FLING_VELOCITY = 1000; //dips per second
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;
    private ViewDragHelper mViewDragHelper;
    private int mDraggingState = 0;
    private int mDraggingBorder;
    private int mVerticalRange;
    private boolean mIsOpen;

    private View headerView;
    private View bottomView;
    private int mTop;
    private int mDragRange;

    //    private View rootView;
    public QuterLayout(Context context) {
        super(context);
        init();
    }

    public QuterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void onStopDraggingToClosed() {
        //To be implemented
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0), resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onFinishInflate() {
        headerView = findViewById(R.id.queen_button);
        bottomView = findViewById(R.id.queen_bottom);
        final float density = getContext().getResources().getDisplayMetrics().density;
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback(this));
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        mViewDragHelper.setMinVelocity(MIN_FLING_VELOCITY * density);
        mIsOpen = false;
        super.onFinishInflate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mVerticalRange = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init() {

    }

    private boolean isQueenTarget(MotionEvent event) {
        int[] queenLocation = new int[2];
        headerView.getLocationOnScreen(queenLocation);
        int upperLimit = queenLocation[1] + headerView.getMeasuredHeight();
        int lowerLimit = queenLocation[1];
        int y = (int) event.getRawY();
        return (y > lowerLimit && y < upperLimit);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mViewDragHelper.cancel();
            return false;
        }

        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    private boolean isMoving() {
        return (mDraggingState == ViewDragHelper.STATE_DRAGGING || mDraggingState == ViewDragHelper.STATE_SETTLING);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public class DragHelperCallback extends ViewDragHelper.Callback {
        private ViewGroup self;
        private float mDragOffset;

        public DragHelperCallback(ViewGroup self) {
            this.self = self;
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (state == mDraggingState) {
                return;
            }
            if ((mDraggingState == ViewDragHelper.STATE_DRAGGING || mDraggingState == ViewDragHelper.STATE_SETTLING) && state == ViewDragHelper.STATE_IDLE) {
                //the view stopped from moving.

                if (mDraggingBorder == 0) {
                    onStopDraggingToClosed();
                } else if (mDraggingBorder == mVerticalRange) {
                    mIsOpen = true;
                }
            }

            if (state == ViewDragHelper.STATE_DRAGGING) {
                onStartDragging();
            }

            mDraggingState = state;
        }

        private void onStartDragging() {
//            Toast.makeText(getContext(), "onStartDragging", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mTop = top;
            mDraggingBorder = top;
            mDragOffset = (float) top / mDragRange;
//            headerView.setPivotX(headerView.getWidth());
//            headerView.setPivotY(headerView.getHeight());
//            headerView.setScaleX(1 - mDragOffset / 2);
//            headerView.setScaleY(1 - mDragOffset / 2);
//            requestLayout();
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            final float rangeToCheck = mVerticalRange;
            if (mDraggingBorder == 0) {
                mIsOpen = false;
                return;
            }

            if (mDraggingBorder == rangeToCheck) {
                mIsOpen = true;
                return;
            }

            boolean settleToOpen = false;
            if (yvel > AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = true;
            } else if (yvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = false;
            } else if (mDraggingBorder > rangeToCheck / 2) {
                settleToOpen = true;
            } else if (mDraggingBorder < rangeToCheck / 2) {
                settleToOpen = false;
            }

            final int settleDestY = settleToOpen ? mVerticalRange : 0;

            if (mViewDragHelper.settleCapturedViewAt(0, settleDestY)) {
                ViewCompat.postInvalidateOnAnimation(QuterLayout.this);
            }
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);

        }

        @Override
        public boolean onEdgeLock(int edgeFlags) {
            return super.onEdgeLock(edgeFlags);
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);
//            mViewDragHelper.captureChildView(findViewById(R.id.lv_root), pointerId);
        }

        @Override
        public int getOrderedChildIndex(int index) {
            return super.getOrderedChildIndex(index);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return super.getViewHorizontalDragRange(child);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Log.d("DragLayout", "clampViewPositionHorizontal" + left + "," + dx);
            return 0;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int newTop = top > 0 ? top : 0;
            return newTop;
        }
    }
}
