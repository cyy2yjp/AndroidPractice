package wdwd.com.androidpractice.refresh;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import wdwd.com.androidpractice.R;

/**
 * Created by tomchen on 16/11/23.
 */

public class RefreshableView extends LinearLayout implements View.OnTouchListener {

    /**
     * 下拉状态
     */
    public static final int STATUS_PULL_TO_REFRESH = 0;
    /**
     * 释放立即刷新状态
     */
    public static final int STATUS_RELEASE_TO_REFRESH = 1;
    /**
     * 正在刷新状态
     */
    public static final int STATUS_REFRESHING = 2;
    /**
     * 刷新完成或未刷新状态
     */
    public static final int STATUS_REFRESH_FINISHED = 3;
    /**
     * 下拉头部回滚速度
     */
    public static final int SCROLL_SPEED = -20;
    /**
     * 一分钟的毫秒值，用于判断上次的更新时间
     */
    public static final int ONE_MINUTE = 60 * 1000;
    /**
     * 一小时的毫秒值，用于判断你上次的更新时间
     */
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    /**
     * 一天的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_DAY = 24 * ONE_HOUR;
    /**
     * 一月的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_MONTH = 30 * ONE_DAY;
    /**
     * 一年的毫秒，用于判断上次的更新时间
     */
    public static final long ONE_YEAR = 12 * ONE_MONTH;
    /**
     * 上次更新时间的字符常量，用于作为SharedPreferences的键值
     */
    public static final String UPDATED_AT = "updated_at";
    private static final String TAG = "RefreshableView";
    /**
     * 用于存储上次更新时间
     */
    private SharedPreferences preferences;

    /**
     * 下拉的View
     */
    private View header;

    /**
     * 指示箭头
     */
    private ImageView arrow;

    /**
     * 需要下拉刷新的ListView
     */
    private ListView listView;

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
    private MarginLayoutParams headerLayoutParams;

    /**
     * 上次更新时间的毫秒值
     */
    private long lastUpdateTime;

    private int mId = -1;

    /**
     * 下拉头的高度
     */
    private int hideHeaderHeight;

    /**
     * 当前处理什么状态，可选址有STATUS_PULL_TO_REFRESH,STATUS_RELEASE_TO_REFRESH
     */
    private int currentStatus = STATUS_REFRESH_FINISHED;

    /**
     * 是否已加载过一次layout，只有listView滚动到头的时候才允许下拉
     */
    private boolean loadOnce;
    /**
     *
     */
    private int touchSlop;

    /**
     * 当前是否可以下拉，只有listView 滚动到头的时候才允许下拉
     */
    private boolean ableToPull;
    /**
     * 记录上一次的状态是什么，避免重复操作
     */
    private int lastStatus = currentStatus;
    private float yDown;

    private PullToRefreshListener mListener;

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
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        header = LayoutInflater.from(getContext()).inflate(R.layout.pull_torefresh, null, true);
        progressBar = (ProgressBar) header.findViewById(R.id.progress_bar);
        arrow = (ImageView) header.findViewById(R.id.arrow);
        description = (TextView) header.findViewById(R.id.description);
        updateAt = (TextView) header.findViewById(R.id.updated_at);
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        refreshUpdatedAtValue();
        setOrientation(VERTICAL);
        addView(header, 0);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setIsAbleToPull(event);
        if (ableToPull) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    yDown = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float yMove = event.getRawY();
                    int distance = (int) (yMove - yDown);
                    //如果手指是下滑状态，并且下拉头是完全隐藏的，就屏蔽下拉事件
                    if (distance <= 0 && headerLayoutParams.topMargin <= hideHeaderHeight) {
                        Log.i(TAG, "topMargin <= hideHeaderHeight");
                        return false;
                    } else if (distance < touchSlop) {
                        Log.i(TAG, "distance <= touchSlop");
                        return false;
                    }

                    Log.i(TAG, "currentStatus" + currentStatus);
                    if (currentStatus != STATUS_REFRESHING) {

                        if (headerLayoutParams.topMargin > 0) {
                            currentStatus = STATUS_RELEASE_TO_REFRESH;
                        } else {
                            currentStatus = STATUS_PULL_TO_REFRESH;
                        }

                        //通过偏移下拉头的topMargin值，来实现下拉效果
                        headerLayoutParams.topMargin = (distance / 2) + hideHeaderHeight;
                        header.setLayoutParams(headerLayoutParams);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                        //松手时如果是释放立即刷新状态，就去调用正在刷新的任务
                        new RefreshingTask().execute();
                    } else if (currentStatus == STATUS_PULL_TO_REFRESH) {
                        //松手时如果是下拉状态，就去调用隐藏下拉头的任务
                        onComplete();
                    }
                    break;
            }

            if (currentStatus == STATUS_PULL_TO_REFRESH || currentStatus == STATUS_RELEASE_TO_REFRESH) {
                updateHeaderView();
                //当前正出于下拉或释放状态，要让ListView失去焦点，否则被点击的那一项会一直处于选中状态
                listView.setPressed(false);
                listView.setFocusable(false);
                listView.setFocusableInTouchMode(false);
                lastStatus = currentStatus;
                //当前正处于下拉或释放状态，通过返回true屏蔽掉ListView 的滚动事件
                return true;
            }
        }
        return false;
    }

    public void onComplete() {
        currentStatus = STATUS_REFRESH_FINISHED;
        preferences.edit().putLong(UPDATED_AT + mId, System.currentTimeMillis()).commit();
        new HideHeaderTask().execute();
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
                if (headerLayoutParams.topMargin != hideHeaderHeight) {
                    headerLayoutParams.topMargin = hideHeaderHeight;
                    header.setLayoutParams(headerLayoutParams);
                }
                ableToPull = false;
            }
        } else {
            //如果listView 中没有元素，也应该允许下拉刷新
            ableToPull = true;
        }
    }

    private void updateHeaderView() {
        if (lastStatus != currentStatus) {
            if (currentStatus == STATUS_PULL_TO_REFRESH) {
                description.setText(getResources().getString(R.string.pull_to_refresh));
                arrow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                rotateArrow();
            } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                description.setText(getResources().getString(R.string.release_to_refresh));
                arrow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                rotateArrow();
            } else if (currentStatus == STATUS_REFRESHING) {
                description.setText(getResources().getString(R.string.refreshing));
                progressBar.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.setVisibility(GONE);
            }
            refreshUpdatedAtValue();
        }
    }

    private void rotateArrow() {
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
     * 进行一些关键性的初始化操作，比如，将下拉头向上偏移进行隐藏，给listView注册touch事件
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            Log.i(TAG, "onLayout()");
            hideHeaderHeight = -header.getHeight();
            headerLayoutParams = (MarginLayoutParams) header.getLayoutParams();
            headerLayoutParams.topMargin = hideHeaderHeight;
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
        updateAt.setText(updateAtValue);
    }

    public PullToRefreshListener getListener() {
        return mListener;
    }

    public RefreshableView setListener(PullToRefreshListener mListener, int mId) {
        this.mListener = mListener;
        this.mId = mId;
        return this;
    }

    public interface PullToRefreshListener {

        /**
         * 刷新时会去回调此方法，在方法内编写具体的刷新逻辑。注意次方法是在子线程中调用的，你可以不必另开线程进行耗时操作
         */
        void onRefresh();
    }

    private class RefreshingTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= 0) {
                    topMargin = 0;
                    break;
                }
                publishProgress(topMargin);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            currentStatus = STATUS_REFRESHING;
            publishProgress(0);
            if (mListener != null) {
                mListener.onRefresh();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            super.onProgressUpdate(topMargin);
            updateHeaderView();
            headerLayoutParams.topMargin = topMargin[0];
            header.setLayoutParams(headerLayoutParams);
        }
    }

    private class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= hideHeaderHeight) {
                    topMargin = hideHeaderHeight;
                    break;
                }
                publishProgress(topMargin);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            super.onProgressUpdate(topMargin);
            headerLayoutParams.topMargin = topMargin[0];
            header.setLayoutParams(headerLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer topMargin) {
            super.onPostExecute(topMargin);
            headerLayoutParams.topMargin = topMargin;
            header.setLayoutParams(headerLayoutParams);
            currentStatus = STATUS_REFRESH_FINISHED;
        }
    }
}
