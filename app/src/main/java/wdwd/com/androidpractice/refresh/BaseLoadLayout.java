package wdwd.com.androidpractice.refresh;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import wdwd.com.androidpractice.R;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static wdwd.com.androidpractice.refresh.Constant.STATUS_PULL_TO_REFRESH;
import static wdwd.com.androidpractice.refresh.Constant.STATUS_RELEASE_TO_REFRESH;

/**
 * Created by tomchen on 16/11/23.
 */

public class BaseLoadLayout {

    /**
     * 下拉的View
     */
    private View mView;

    /**
     * 指示箭头
     */
    private ImageView arrow;

    /**
     * 刷新时显示的进度条
     */
    private ProgressBar progressBar;

    /**
     * 指示下拉和释放的文字描述
     */
    private TextView description;

    /**
     * 上次更新的文字描述
     */
    private TextView updateAt;

    /**
     * 下拉头的布局参数
     */
    private ViewGroup.MarginLayoutParams mLayoutParams;

    /**
     * 下拉头的高度
     */
    private int viewHeight;
    private int touchSlop;
    private Context mContext;

    public void mapView(Context context) {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.pull_torefresh, null, true);
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        arrow = (ImageView) mView.findViewById(R.id.arrow);
        description = (TextView) mView.findViewById(R.id.description);
        updateAt = (TextView) mView.findViewById(R.id.updated_at);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    public View getView() {
        return mView;
    }

    /**
     * 如果手指是下滑状态，并且下拉头是完全隐藏的，就屏蔽下拉事件
     *
     * @param distance
     * @return
     */
    public boolean processEvent(int distance) {
        if (distance <= 0 && mLayoutParams.topMargin <= viewHeight) {
            Log.i(TAG, "topMargin <= hideHeaderHeight");
            return false;
        } else if (distance < touchSlop) {
            Log.i(TAG, "distance <= touchSlop");
            return false;
        }
        return true;
    }

    public void updateView(int currentStatus) {
        Resources resource = mContext.getResources();
        if (currentStatus == STATUS_PULL_TO_REFRESH) {
            description.setText(resource.getString(R.string.pull_to_refresh));
            arrow.setVisibility(View.VISIBLE);
            progressBar.setVisibility(GONE);
            rotateArrow(currentStatus);
        } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
            description.setText(resource.getString(R.string.release_to_refresh));
            arrow.setVisibility(View.VISIBLE);
            progressBar.setVisibility(GONE);
            rotateArrow(currentStatus);
        } else if (currentStatus == Constant.STATUS_REFRESHING) {
            description.setText(resource.getString(R.string.refreshing));
            progressBar.setVisibility(View.VISIBLE);
            arrow.clearAnimation();
            arrow.setVisibility(GONE);
        }
    }

    private void rotateArrow(int currentStatus) {
        float pivotX = arrow.getWidth() / 2f;
        float pivotY = arrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (currentStatus == STATUS_PULL_TO_REFRESH) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }

        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        arrow.startAnimation(animation);
    }

    /**
     * 初始化header的高度
     */
    public void initViewSize() {
        viewHeight = -mView.getHeight();
        mLayoutParams = (ViewGroup.MarginLayoutParams) mView.getLayoutParams();
        mLayoutParams.topMargin = viewHeight;
    }

    public void updateViewMargin(Integer topMargin) {
        mLayoutParams.topMargin = topMargin;
        mView.setLayoutParams(mLayoutParams);
    }

    public int getTopMargin() {
        return mLayoutParams.topMargin;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setUpdateTimeText(String updateAtValue) {
        updateAt.setText(updateAtValue);
    }

    public void close() {
        if (mLayoutParams.topMargin != viewHeight) {
            updateViewMargin(viewHeight);
        }
    }
}
