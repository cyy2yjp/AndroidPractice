package wdwd.com.androidpractice.flux.actions;

/**
 * Action 是简单的POJO类型，只提供两个字段：type和data,分别记录Action的类型和数据。
 * 注意Action一旦创建是不可更改的，所以它的字段类型修饰为final类型
 * <p>
 * Created by tomchen on 16/11/29.
 */

public class Action<T> {
    private final String type;
    private final T data;

    public Action(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public T getData() {
        return data;
    }
}
