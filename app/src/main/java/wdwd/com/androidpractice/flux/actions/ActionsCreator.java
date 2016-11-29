package wdwd.com.androidpractice.flux.actions;

import wdwd.com.androidpractice.flux.dispatcher.Dispatcher;

/**
 * ActionCreator 是Flux架构中第四个最重要的模块，(Dispatcher,Store,View),这里实际上处理很多工作
 * ，此外，提供有一个有语义的API,构建Action,处理网络请求等
 * <p>
 * 此外提供了一个 sendMessage(String message) ,就像名字按时的那样，这个方法用来发送消息（到 Store）.在方法内部，会创建一个MessageAction
 * 来封装数据和Action 类型，并通过Dispatcher发送到Store
 * <p>
 * Created by tomchen on 16/11/29.
 */

public class ActionsCreator {

    private static ActionsCreator instance;
    final Dispatcher dispatcher;

    ActionsCreator(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public static ActionsCreator get(Dispatcher dispatcher) {
        if (instance == null) {
            instance = new ActionsCreator(dispatcher);
        }
        return instance;
    }

    public void sendMessage(String message) {
        dispatcher.dispatch(new MessageAction(MessageAction.ACTION_NEW_MESSAGE, message));
    }
}
