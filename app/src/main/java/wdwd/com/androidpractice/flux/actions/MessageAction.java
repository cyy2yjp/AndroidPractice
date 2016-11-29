package wdwd.com.androidpractice.flux.actions;

/**
 * 这个实现非常简单，仅仅多定义了一个Action 类型字段： public static final String ACTION_NEW_MESSAGE = "new_message";
 * 如你所见，action都是这个简单的，不包含任何业务逻辑。
 * <p>
 * Created by tomchen on 16/11/29.
 */

public class MessageAction extends Action<String> {

    public static final String ACTION_NEW_MESSAGE = "new_message";

    MessageAction(String type, String data) {
        super(type, data);
    }
}
