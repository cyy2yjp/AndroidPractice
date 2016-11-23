package wdwd.com.androidpractice.refresh;

/**
 * Created by tomchen on 16/11/23.
 */

public class Constant {
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
}
