package wdwd.com.androidpractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * 大致使用是这样的:
 * 当你需要跟踪触摸时间的速度的时候,使用obtain()方法来获得VelocityTracker类的一个实例对象
 * 在onTouchEvent回调函数中,使用addMovement(MotionEvent) 函数将当前的移动时间传递给VelocityTracker对象
 * <p>
 * 使用computeCurrentVelocity (units) 函数来计算当前的速度,使用getXVelocity(),getYVelocity 函数来获得当前的速度
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


}
