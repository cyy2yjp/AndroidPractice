package wdwd.com.androidpractice.refresh;

/**
 * Created by tomchen on 16/11/23.
 */

public class RefreshStatus {

    private int value;

    public RefreshStatus(int status) {
        this.value = status;
    }


    public int getValue() {
        return value;
    }

    public RefreshStatus setValue(int value) {
        this.value = value;
        return this;
    }
}
