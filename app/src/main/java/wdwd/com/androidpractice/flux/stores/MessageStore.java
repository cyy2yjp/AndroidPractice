package wdwd.com.androidpractice.flux.stores;

import wdwd.com.androidpractice.flux.actions.Action;
import wdwd.com.androidpractice.flux.actions.MessageAction;
import wdwd.com.androidpractice.flux.model.Message;


/**
 * MessageStore类主要用来维护MainActivity的UI状态
 * 在这里实现了 onAction(Action action) 方法，并用一个switch 语句来路由各种不同的Action类型。同时维护了一个结
 * 构Message.java 类，这个类用来记录当前要显示的消息。Store类只能通过Dispatcher来更新（不要提供setter方法），
 * 对外仅暴露各种getter方法来获取UI状态。这里用String getMessage()方法来获取具体的消息
 * <p>
 * Created by tomchen on 16/11/29.
 */

public class MessageStore extends Store {

    private static MessageStore singleton;
    private Message mMessage = new Message();

    public MessageStore() {
        super();
    }

    public String getMessage() {
        return mMessage.getMessage();
    }

    @Override
    public StoreChangeEvent changeEvent() {
        return new StoreChangeEvent();
    }

    @Override
    public void onAction(Action action) {
        switch (action.getType()) {
            case MessageAction.ACTION_NEW_MESSAGE:
                mMessage.setMessage(action.getData().toString());
                break;
            default:
                break;
        }
        emitStoreChange();
    }

}
