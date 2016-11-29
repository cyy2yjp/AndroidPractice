package wdwd.com.androidpractice.refresh;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import wdwd.com.androidpractice.R;

import static wdwd.com.androidpractice.refresh.Constant.ONE_DAY;
import static wdwd.com.androidpractice.refresh.Constant.ONE_HOUR;
import static wdwd.com.androidpractice.refresh.Constant.ONE_MINUTE;
import static wdwd.com.androidpractice.refresh.Constant.ONE_MONTH;
import static wdwd.com.androidpractice.refresh.Constant.ONE_YEAR;
import static wdwd.com.androidpractice.refresh.Constant.STATUS_PULL_TO_REFRESH;
import static wdwd.com.androidpractice.refresh.Constant.STATUS_REFRESH_FINISHED;
import static wdwd.com.androidpractice.refresh.Constant.STATUS_RELEASE_TO_REFRESH;
import static wdwd.com.androidpractice.refresh.Constant.UPDATED_AT;

/**
 * Created by tomchen on 16/11/23.
 */

public class RefreshableView extends LinearLayout implements View.OnTouchListener {


    private static final String TAG = "RefreshableView";
    /**
     * 用于存储上次更新时间
     */
    private SharedPreferences preferences;

    /**
     * 需要下拉刷新的ListView
     */
    private ListView listView;

    /**
     * 当前处理什么状态，可选址有STATUS_PULL_TO_REFRESH,STATUS_RELEASE_TO_REFRESH
     */
    private RefreshStatus currentStatus = new RefreshStatus(STATUS_REFRESH_FINISHED);

    /**
     * 是否已加载过一次layout，只有listView滚动到头的时候才允许下拉
     */
    private boolean loadOnce;
    /**
     * 上次更新时间的毫秒值
     */
    private long lastUpdateTime;

    private BaseLoadLayout header;
    private int mId = -1;
    /**
     * 当前是否可以下拉，只有listView 滚动到头的时候才允许下拉
     */
    private boolean ableToPull;
    /**
     * 记录上一次的状态是什么，避免重复操作
     */
    private RefreshStatus lastStatus = new RefreshStatus(STATUS_REFRESH_FINISHED);
    private float yDown;

    public RefreshableView(Context context) {
        super(context);
        init();
    }

    public RefreshableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        header = new HeaderLoadLayout();
        header.setLoadLayoutDelegate(new BaseLoadLayout.LoadLayoutDelegate() {
            @Override
            public void updateLoadStatus() {
                updateHeaderView();
            }
        });
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        header.mapView(getContext());

        refreshUpdatedAtValue();
        setOrientation(VERTICAL);
        addView(header.getView(), 0);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setIsAbleToPull(event);
        int value = currentStatus.getValue();
        if (ableToPull) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    yDown = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float yMove = event.getRawY();
                    int distance = (int) (yMove - yDown);
                    if (!header.processEvent(distance, currentStatus)) {
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if (value == STATUS_RELEASE_TO_REFRESH) {
                        //松手时如果是释放立即刷新状态，就去调用正在刷新的任务
                        header.showView(currentStatus);
                    } else if (value == STATUS_PULL_TO_REFRESH) {
                        //松手时如果是下拉状态，就去调用隐藏下拉头的任务
                        onComplete();
                    }
                    break;
            }
            value = currentStatus.getValue();
            if (value == STATUS_PULL_TO_REFRESH || value == STATUS_RELEASE_TO_REFRESH) {
                updateHeaderView();
                //当前正出于下拉或释放状态，要让ListView失去焦点，否则被点击的那一项会一直处于选中状态
                listView.setPressed(false);
                listView.setFocusable(false);
                listView.setFocusableInTouchMode(false);
                lastStatus.setValue(currentStatus.getValue());
                //当前正处于下拉或释放状态，通过返回true屏蔽掉ListView 的滚动事件
                return true;
            }
        }
        return false;
    }

    public void onComplete() {
        currentStatus.setValue(STATUS_REFRESH_FINISHED);
        preferences.edit().putLong(UPDATED_AT + mId, System.currentTimeMillis()).commit();
        header.hideView(currentStatus);
    }

    private void setIsAbleToPull(MotionEvent event) {
        View firstChild = listView.getChildAt(0);
        if (firstChild != null) {
            int firstVisiblePos = listView.getFirstVisiblePosition();
            if (firstVisiblePos == 0 && firstChild.getTop() == 0) {
                if (!ableToPull) {
                    yDown = event.getRawY();
                }
                //如果首个元素的上边缘，距离父布局值为0，就说明listView 滚动到了最顶部，此时应该允许下拉刷新
                ableToPull = true;
            } else {
                header.close();
                ableToPull = false;
            }
        } else {
            //如果listView 中没有元素，也应该允许下拉刷新
            ableToPull = true;
        }
    }

    /**
     * 更新头部
     */
    public void updateHeaderView() {
        Log.i(TAG, lastStatus.getValue() + "+++" + currentStatus.getValue());
        if (lastStatus.getValue() != currentStatus.getValue()) {
            header.updateViewByStatus(currentStatus.getValue());
            refreshUpdatedAtValue();
        }
    }

    /**
     * 进行一些关键性的初始化操作，比如，将下拉头向上偏移进行隐藏，给listView注册touch事件
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            Log.i(TAG, "onLayout()");
            header.initViewSize();
            listView = (ListView) getChildAt(1);
            listView.setOnTouchListener(this);
            loadOnce = true;
        }
    }

    public void refreshUpdatedAtValue() {
        lastUpdateTime = preferences.getLong(UPDATED_AT + mId, -1);
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastUpdateTime;
        long timeIntoFormat;
        String updateAtValue;
        if (lastUpdateTime == -1) {
            updateAtValue = getResources().getString(R.string.not_updated_yet);
        } else if (timePassed < 0) {
            updateAtValue = getResources().getString(R.string.time_error);
        } else if (timePassed < ONE_MINUTE) {
            updateAtValue = getResources().getString(R.string.updated_just_now);
        } else if (timePassed < ONE_HOUR) {
            timeIntoFormat = timePassed / ONE_MINUTE;
            String value = timeIntoFormat + "小时";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_DAY) {
            timeIntoFormat = timePassed / ONE_DAY;
            String value = timeIntoFormat + "天";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_YEAR) {
            timeIntoFormat = timePassed / ONE_MONTH;
            String value = timeIntoFormat + "个月";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else {
            timeIntoFormat = timePassed / ONE_YEAR;
            String value = timeIntoFormat + "年";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        }
        header.setUpdateTimeText(updateAtValue);
    }

    public RefreshableView setListener(PullToRefreshListener mListener, int mId) {
        header.setListener(mListener);
        this.mId = mId;
        return this;
    }

    public interface PullToRefreshListener {

        /**
         * 刷新时会去回调此方法，在方法内编写具体的刷新逻辑。注意次方法是在子线程中调用的，你可以不必另开线程进行耗时操作
         */
        void onRefresh();
    }


}
