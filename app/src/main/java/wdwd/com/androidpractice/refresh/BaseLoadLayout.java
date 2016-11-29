package wdwd.com.androidpractice.refresh;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import wdwd.com.androidpractice.R;

import static android.view.View.GONE;
import static wdwd.com.androidpractice.refresh.Constant.STATUS_PULL_TO_REFRESH;
import static wdwd.com.androidpractice.refresh.Constant.STATUS_RELEASE_TO_REFRESH;

/**
 * Created by tomchen on 16/11/23.
 */

public abstract class BaseLoadLayout {

    /**
     * 指示箭头
     */
    protected ImageView arrow;
    protected RefreshableView.PullToRefreshListener mListener;
    protected LoadLayoutDelegate loadLayoutDelegate;
    /**
     * 下拉的View
     */
    private View mView;
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

    protected void updateLoadStatus() {
        if (loadLayoutDelegate != null) {
            loadLayoutDelegate.updateLoadStatus();
        }
    }

    /**
     * 根据当前的状态修改界面信息
     *
     * @param currentStatus
     */
    public void updateViewByStatus(int currentStatus) {
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

    /**
     * 如果手指是下滑状态，并且下拉头是完全隐藏的，就屏蔽下拉事件
     *
     * @param distance
     * @return
     */
    protected boolean processEvent(int distance, RefreshStatus refreshStatus) {
        return true;
    }

    protected abstract void showView(RefreshStatus refreshStatus);

    protected abstract void hideView(RefreshStatus refreshStatus);

    protected void rotateArrow(int currentStatus) {

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

    public int getTouchSlop() {
        return touchSlop;
    }

    public View getView() {
        return mView;
    }

    public RefreshableView.PullToRefreshListener getListener() {
        return mListener;
    }

    public BaseLoadLayout setListener(RefreshableView.PullToRefreshListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public LoadLayoutDelegate getLoadLayoutDelegate() {
        return loadLayoutDelegate;
    }

    public BaseLoadLayout setLoadLayoutDelegate(LoadLayoutDelegate loadLayoutDelegate) {
        this.loadLayoutDelegate = loadLayoutDelegate;
        return this;
    }

    public interface LoadLayoutDelegate {
        void updateLoadStatus();
    }
}
