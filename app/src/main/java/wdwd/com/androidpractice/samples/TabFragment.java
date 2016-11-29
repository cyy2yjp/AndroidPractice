package wdwd.com.androidpractice.samples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import wdwd.com.androidpractice.R;

/**
 * Created by tomchen on 16/11/28.
 */

public class TabFragment extends Fragment {

    private static final String TITLE = "title";
    RecyclerView mRecyclerView;
    private String mTitle = "Default Value";
    private List<String> mDatas = new ArrayList<>();

    public static TabFragment newInstance(String title) {

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        TabFragment fragment = new TabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_stickynavlayout_innerscrollview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        for (int i = 0; i < 50; i++) {
            mDatas.add(mTitle + "-->" + i);
        }

        mRecyclerView.setAdapter(new RecyclerView.Adapter<ViewHolder>() {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(inflater.inflate(R.layout.item, parent, false));
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                holder.textView.setText(mDatas.get(position));
            }

            @Override
            public int getItemCount() {
                return mDatas.size();
            }
        });

        ButterKnife.bind(this, view);
        return view;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.id_info)
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
