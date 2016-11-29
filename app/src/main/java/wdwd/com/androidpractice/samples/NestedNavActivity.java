package wdwd.com.androidpractice.samples;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wdwd.com.androidpractice.R;

/**
 * Created by tomchen on 16/11/28.
 */

public class NestedNavActivity extends FragmentActivity {

    @BindView(R.id.id_stickynavlayout_topview)
    RelativeLayout idStickynavlayoutTopview;
    @BindView(R.id.id_stickynavlayout_indicator)
    TextView idStickynavlayoutIndicator;
    @BindView(R.id.id_stickynavlayout_viewpager)
    ViewPager idStickynavlayoutViewpager;

    private FragmentPagerAdapter mAdapter;
    private TabFragment[] mFragments = new TabFragment[4];
    private String[] mTitle = new String[]{"nihao", "tamen", "shide", "haha"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nested_viewpager);
        ButterKnife.bind(this);

        initDatas();
        initEvents();
    }


    private void initDatas() {
        for (int i = 0; i < 4; i++) {
            mFragments[i] = TabFragment.newInstance(mTitle[i]);
        }
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }
        };

        idStickynavlayoutViewpager.setAdapter(mAdapter);
    }

    private void initEvents() {
        idStickynavlayoutViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


}
