package wdwd.com.androidpractice.flux.dispatcher;

import java.util.ArrayList;
import java.util.List;

import wdwd.com.androidpractice.flux.actions.Action;
import wdwd.com.androidpractice.flux.stores.Store;

/**
 * Dispatcher 对外仅暴露3个公有方法
 * register(final Store store); 用来注册每个Store 的回调接口
 * unRegister(final Store store) 用来解除Store的回调接口
 * dispatch(Action action) 用来触发Store的注册回调接口
 * <p>
 * 这里仅仅用一个ArrayList 来管理Stores ,对于一个更复杂的App可
 * 能需要精心设计数据结构来管理Stores组织和相互间的依赖关系
 * <p>
 * Created by tomchen on 16/11/29.
 */

public class Dispatcher {
    private static Dispatcher instance;
    private final List<Store> stores = new ArrayList<>();

    Dispatcher() {
    }

    public static Dispatcher get() {
        if (instance == null) {
            instance = new Dispatcher();
        }
        return instance;
    }

    public void register(final Store store) {
        stores.add(store);
    }

    public void unRegister(final Store store) {
        stores.remove(store);
    }

    public void dispatch(Action aciton) {
        post(aciton);
    }

    private void post(final Action action) {
        for (Store store : stores) {
            store.onAction(action);
        }
    }
}
