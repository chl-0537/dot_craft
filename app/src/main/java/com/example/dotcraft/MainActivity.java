package com.example.dotcraft;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import java.util.logging.Level;

public class MainActivity extends AppCompatActivity {

    private final ImageView[] dotViewArr = new ImageView[9];
    private final ImageView[] containerViewArr = new ImageView[9];

    private Level level;

    private ImageView backupDot;

    private float lastMotionX;
    private float lastMotionY;
    private int touchIndex;

    private static final int STATE_IDLE = 0;
    private static final int STATE_WAITING_DRAG = 1;
    private static final int STATE_HORIZONTAL_DRAG = 2;
    private static final int STATE_VERTICAL_DRAG = 3;
    private int state = STATE_IDLE;

    private int touchSlop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        // 初始化
        initDotViews();
        initContainerView();
    }

    public void initDotViews() {

    }

    public void initContainerView() {

    }
}
