package wdwd.com.androidpractice.flux.stores;

import com.squareup.otto.Bus;

import wdwd.com.androidpractice.flux.actions.Action;

/**
 * 抽象的Store类，提供了一个主要的虚方法 void onAction(Action action),
 * 这个方法是注册在Dispatcher里面的回调接口，当Dispatcher 有数据派发过来的时候可以在这里处理。
 * <p>
 * Created by tomchen on 16/11/29.
 */

public abstract class Store {
    private static final Bus bus = new Bus();

    public void register(final Object view) {
        bus.register(view);
    }

    public void unregister(final Object view) {
        bus.unregister(view);
    }

    void emitStoreChange() {
        bus.post(changeEvent());
    }

    public abstract StoreChangeEvent changeEvent();

    public abstract void onAction(Action action);


    public class StoreChangeEvent {
    }
}
