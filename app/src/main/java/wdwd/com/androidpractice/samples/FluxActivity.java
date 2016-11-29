package wdwd.com.androidpractice.samples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import wdwd.com.androidpractice.R;
import wdwd.com.androidpractice.flux.actions.ActionsCreator;
import wdwd.com.androidpractice.flux.dispatcher.Dispatcher;
import wdwd.com.androidpractice.flux.stores.MessageStore;
import wdwd.com.androidpractice.flux.stores.Store;

/**
 * 这部分代码比较多，首先在onCreate() 方法中初始化了依赖和需要的UI组件。最重要的是onStoreChange() 方法，这个方
 * 法是注册在Store中回调（使用EventBus的 @Subscribe 注解标识）,当Store发生改变会触发这个方法，我们在这里调用
 * render() 方法重绘整个界面。
 * <p>
 * Created by tomchen on 16/11/29.
 */

public class FluxActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.messageEditor)
    EditText messageEditor;
    @BindView(R.id.messageButton)
    Button messageButton;
    @BindView(R.id.tv_message_view)
    TextView tvMessageView;

    private Dispatcher dispatcher;
    private ActionsCreator actionsCreator;
    private MessageStore store;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flux);
        ButterKnife.bind(this);

        initDependencies();
        messageButton.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dispatcher.unRegister(store);
    }

    private void initDependencies() {
        dispatcher = Dispatcher.get();
        actionsCreator = ActionsCreator.get(dispatcher);
        store = new MessageStore();
        dispatcher.register(store);
    }

    private void render(MessageStore store) {
        tvMessageView.setText(store.getMessage());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.messageButton:
                if (!TextUtils.isEmpty(messageEditor.getText().toString())) {
                    actionsCreator.sendMessage(messageEditor.getText().toString());
                    messageEditor.setText(null);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        store.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        store.unregister(this);
    }

    @Subscribe
    public void onStoreChange(Store.StoreChangeEvent event) {
        render(store);
    }
}
