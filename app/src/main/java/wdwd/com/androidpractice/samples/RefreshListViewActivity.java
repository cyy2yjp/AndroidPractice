package wdwd.com.androidpractice.samples;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import wdwd.com.androidpractice.R;
import wdwd.com.androidpractice.refresh.RefreshableView;

public class RefreshListViewActivity extends Activity implements RefreshableView.PullToRefreshListener {

    @BindView(R.id.refresh_list_view)
    RefreshableView refreshListView;
    @BindView(R.id.list_view)
    ListView listView;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_list_view);
        ButterKnife.bind(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, getData());
        listView.setAdapter(adapter);
        refreshListView.setListener(this, 100);
    }

    private ArrayList<String> getData() {
        list.add("180平米的房子");
        list.add("一个勤劳漂亮的老婆");
        list.add("一辆宝马");
        list.add("一个强壮且永不生病的身体");
        list.add("一个喜欢的事业");
        return list;
    }

    @Override
    public void onRefresh() {

        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshListView.onComplete();
            }
        }, 3000);
    }
}

