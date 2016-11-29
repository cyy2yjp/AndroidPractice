package wdwd.com.androidpractice.refresh;

import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.RotateAnimation;

import static android.R.attr.value;
import static wdwd.com.androidpractice.refresh.Constant.SCROLL_SPEED;
import static wdwd.com.androidpractice.refresh.Constant.STATUS_PULL_TO_REFRESH;
import static wdwd.com.androidpractice.refresh.Constant.STATUS_REFRESHING;
import static wdwd.com.androidpractice.refresh.Constant.STATUS_REFRESH_FINISHED;
import static wdwd.com.androidpractice.refresh.Constant.STATUS_RELEASE_TO_REFRESH;

/**
 * Created by tomchen on 16/11/23.
 */

public class HeaderLoadLayout extends BaseLoadLayout {
    private final static String TAG = "HeaderLoadLayout";

    @Override
    protected boolean processEvent(int distance, RefreshStatus refreshStatus) {
        if (distance <= 0 && getTopMargin() <= getViewHeight()) {
            Log.i(TAG, "topMargin <= hideHeaderHeight");
            return false;
        } else if (distance < getTouchSlop()) {
            Log.i(TAG, "distance <= touchSlop");
            return false;
        }

        Log.i(TAG, "currentStatus" + refreshStatus.getValue());

        if (value != Constant.STATUS_REFRESHING) {
            if (getTopMargin() > 0) {
                refreshStatus.setValue(STATUS_RELEASE_TO_REFRESH);
            } else {
                refreshStatus.setValue(STATUS_PULL_TO_REFRESH);
            }
            //通过偏移下拉头的topMargin值，来实现下拉效果
            updateViewMargin((distance / 2) + getViewHeight());
        }
        return true;
    }

    @Override
    protected void showView(RefreshStatus refreshStatus) {
        new RefreshingTask(refreshStatus).execute();
    }

    @Override
    protected void hideView(RefreshStatus refreshStatus) {
        new HideHeaderTask(refreshStatus).execute();
    }

    @Override
    protected void rotateArrow(int currentStatus) {
        super.rotateArrow(currentStatus);
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

    private class RefreshingTask extends AsyncTask<Void, Integer, Void> {

        public RefreshStatus refreshStatus;

        public RefreshingTask(RefreshStatus refreshStatus) {
            this.refreshStatus = refreshStatus;
        }

        @Override
        protected Void doInBackground(Void... params) {
            int topMargin = getTopMargin();
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

            refreshStatus.setValue(STATUS_REFRESHING);
            publishProgress(0);
            if (mListener != null) {
                mListener.onRefresh();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            super.onProgressUpdate(topMargin);
            updateViewMargin(topMargin[0]);
            updateLoadStatus();
        }
    }

    private class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

        RefreshStatus refreshStatus;

        public HideHeaderTask(RefreshStatus refreshStatus) {
            this.refreshStatus = refreshStatus;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int topMargin = getTopMargin();
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= getViewHeight()) {
                    topMargin = getViewHeight();
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

            updateViewMargin(topMargin[0]);
        }

        @Override
        protected void onPostExecute(Integer topMargin) {
            super.onPostExecute(topMargin);
            updateViewMargin(topMargin);
            refreshStatus.setValue(STATUS_REFRESH_FINISHED);
        }
    }
}
