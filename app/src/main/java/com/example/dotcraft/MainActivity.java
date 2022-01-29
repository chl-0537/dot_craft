package com.example.dotcraft;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final ImageView[] containerViewArr = new ImageView[9];
    private final ImageView[] dotViewArr = new ImageView[9];

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

        initDotViews();
        initContainerViews();

        initLevel();

        restart();
        nextLevel();
    }

    private void initDotViews() {
        dotViewArr[0] = findViewById(R.id.dot0);
        dotViewArr[1] = findViewById(R.id.dot1);
        dotViewArr[2] = findViewById(R.id.dot2);
        dotViewArr[3] = findViewById(R.id.dot3);
        dotViewArr[4] = findViewById(R.id.dot4);
        dotViewArr[5] = findViewById(R.id.dot5);
        dotViewArr[6] = findViewById(R.id.dot6);
        dotViewArr[7] = findViewById(R.id.dot7);
        dotViewArr[8] = findViewById(R.id.dot8);
        backupDot = findViewById(R.id.backup_dot);
    }

    private void initContainerViews() {
        containerViewArr[0] = findViewById(R.id.container0);
        containerViewArr[1] = findViewById(R.id.container1);
        containerViewArr[2] = findViewById(R.id.container2);
        containerViewArr[3] = findViewById(R.id.container3);
        containerViewArr[4] = findViewById(R.id.container4);
        containerViewArr[5] = findViewById(R.id.container5);
        containerViewArr[6] = findViewById(R.id.container6);
        containerViewArr[7] = findViewById(R.id.container7);
        containerViewArr[8] = findViewById(R.id.container8);
    }

    private void initLevel() {
        level = new Level1();
        refreshView();
    }


    // 下一关
    private void nextLevel() {
        Button button = findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level = new Level2();
                refreshView();
            }
        });
    }

    // 重新开始
    private void restart() {
        Button button = findViewById(R.id.restart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshView();
            }
        });
    }

    // 刷新页面
    private void refreshView() {
        int[] containerArr = level.getContainerArray();
        int[] dotArr = level.getDotArray();
        for (int i = 0; i < 9; i++) {
            if (containerArr[i] == 1) {
                containerViewArr[i].setImageResource(R.drawable.shape_ring_white);
            } else {
                containerViewArr[i].setImageResource(0);
            }
        }
        for (int i = 0; i < 9; i++) {
            if (dotArr[i] == 1) {
                dotViewArr[i].setImageResource(R.drawable.shape_dot_white);
            } else {
                dotViewArr[i].setImageResource(R.drawable.shape_dot_black);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // 手指按下
            case MotionEvent.ACTION_DOWN: {
                state = STATE_IDLE;
                lastMotionX = event.getRawX();
                lastMotionY = event.getRawY();
                touchIndex = getTouchViewIndex(lastMotionX, lastMotionY);
                if (touchIndex != -1) {
                    state = STATE_WAITING_DRAG;
                }
                break;
            }
            // 手指拖动
            case MotionEvent.ACTION_MOVE: {
                float deltaX = event.getRawX() - lastMotionX;
                float deltaY = event.getRawY() - lastMotionY;
                if (state == STATE_WAITING_DRAG) {
                    // 超过一定距离才认为有效滑动
                    if (Math.abs(deltaX) >= touchSlop || Math.abs(deltaY) >= touchSlop) {
                        state = Math.abs(deltaX) > Math.abs(deltaY) ? STATE_HORIZONTAL_DRAG : STATE_VERTICAL_DRAG;
                    }
                }
                if (state == STATE_HORIZONTAL_DRAG) {
                    // 横向滑动
                    horizontalDragging(touchIndex / 3, deltaX);
                } else if (state == STATE_VERTICAL_DRAG) {
                    // 纵向滑动
                    verticalDragging(touchIndex % 3, deltaY);
                }
                lastMotionX = event.getRawX();
                lastMotionY = event.getRawY();
                break;
            }
            // 手指抬起
            case MotionEvent.ACTION_UP: {
                if (state == STATE_HORIZONTAL_DRAG) {
                    // 横向滑动
                    horizontalDragEnd(touchIndex / 3);
                } else if (state == STATE_VERTICAL_DRAG) {
                    // 纵向滑动
                    verticalDragEnd(touchIndex % 3);
                }
                touchIndex = -1;
                state = STATE_IDLE;
                break;
            }
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private int getTouchViewIndex(float x, float y) {
        for (int i = 0; i < 9; i++) {
            ImageView dotView = dotViewArr[i];
            int[] location = new int[2];
            dotView.getLocationOnScreen(location);
            int left = location[0];
            int top = location[1];
            int right = left + dotView.getWidth();
            int bottom = top + dotView.getHeight();
            if (x >= left && x <= right && y >= top && y <= bottom) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 横向滑动中，实现循环滑动
     */
    private void horizontalDragging(int rowIndex, float delta) {
        ImageView leftDot = dotViewArr[rowIndex * 3];
        ImageView middleDot = dotViewArr[rowIndex * 3 + 1];
        ImageView rightDot = dotViewArr[rowIndex * 3 + 2];
        float translationX = getValidTranslation(leftDot.getTranslationX() + delta);
        leftDot.setTranslationX(translationX);
        middleDot.setTranslationX(translationX);
        rightDot.setTranslationX(translationX);
        if (backupDot.getVisibility() != View.VISIBLE) {
            backupDot.setVisibility(View.VISIBLE);
        }
        if (translationX > 0) {
            // 向右滑，backup在左边出现
            backupDot.setTranslationX(translationX - backupDot.getWidth());
            backupDot.setImageDrawable(rightDot.getDrawable());
        } else {
            // 向左滑，backup在右边出现
            backupDot.setTranslationX(backupDot.getWidth() * 3 + translationX);
            backupDot.setImageDrawable(leftDot.getDrawable());
        }
        backupDot.setTranslationY(backupDot.getHeight() * rowIndex);
    }

    /**
     * 横向滑动结束，判断滑动距离是否足够触发移动。如果触发移动后，刷新页面展示，并判断关卡是否通过
     */
    private void horizontalDragEnd(int rowIndex) {
        ImageView leftDot = dotViewArr[rowIndex * 3];
        ImageView middleDot = dotViewArr[rowIndex * 3 + 1];
        ImageView rightDot = dotViewArr[rowIndex * 3 + 2];
        float targetTranslationX = leftDot.getTranslationX();
        leftDot.setTranslationX(0.0f);
        middleDot.setTranslationX(0.0f);
        rightDot.setTranslationX(0.0f);
        backupDot.setVisibility(View.INVISIBLE);
        backupDot.setTranslationX(0.0f);
        backupDot.setTranslationY(0.0f);
        if (Math.abs(targetTranslationX) < backupDot.getWidth() * 1.0f / 2) {
            return;
        }
        boolean toRight = targetTranslationX > backupDot.getWidth() * 1.0f / 2;
        LevelUtils.horizontalDragLevel(level, toRight, rowIndex);
        refreshView();
        if (LevelUtils.hasSuccess(level)) {
            congratulations();
        }
    }

    /**
     * 纵向滑动中，实现循环滑动
     */
    private void verticalDragging(int columnIndex, float delta) {
        ImageView topDot = dotViewArr[columnIndex];
        ImageView middleDot = dotViewArr[columnIndex + 3];
        ImageView bottomDot = dotViewArr[columnIndex + 6];
        float translationY = getValidTranslation(topDot.getTranslationY() + delta);
        topDot.setTranslationY(translationY);
        middleDot.setTranslationY(translationY);
        bottomDot.setTranslationY(translationY);
        if (backupDot.getVisibility() != View.VISIBLE) {
            backupDot.setVisibility(View.VISIBLE);
        }
        backupDot.setTranslationX(backupDot.getWidth() * columnIndex);
        if (translationY > 0) {
            // 向下滑，backup在上边出现
            backupDot.setTranslationY(translationY - backupDot.getHeight());
            backupDot.setImageDrawable(bottomDot.getDrawable());
        } else {
            // 向上滑，backup在下边出现
            backupDot.setTranslationY(backupDot.getHeight() * 3 + translationY);
            backupDot.setImageDrawable(topDot.getDrawable());
        }
    }

    /**
     * 纵向滑动结束，判断滑动距离是否足够触发移动。如果触发移动后，刷新页面展示，并判断关卡是否通过
     */
    private void verticalDragEnd(int columnIndex) {
        ImageView topDot = dotViewArr[columnIndex];
        ImageView middleDot = dotViewArr[columnIndex + 3];
        ImageView bottomDot = dotViewArr[columnIndex + 6];
        float targetTranslationY = topDot.getTranslationY();
        topDot.setTranslationY(0.0f);
        middleDot.setTranslationY(0.0f);
        bottomDot.setTranslationY(0.0f);
        backupDot.setVisibility(View.INVISIBLE);
        backupDot.setTranslationX(0.0f);
        backupDot.setTranslationY(0.0f);
        if (Math.abs(targetTranslationY) < backupDot.getWidth() * 1.0f / 2) {
            return;
        }
        boolean toTop = targetTranslationY < backupDot.getWidth() * -1.0f / 2;
        LevelUtils.verticalDragLevel(level, toTop, columnIndex);
        refreshView();
        if (LevelUtils.hasSuccess(level)) {
            congratulations();
        }
    }

    /**
     * 限制一次只能滑动一格
     */
    private float getValidTranslation(float translation) {
        return Math.max(backupDot.getWidth() * -1, Math.min(translation, backupDot.getWidth()));
    }

    // 提示过关
    private void congratulations() {
        Toast.makeText(this, "恭喜过关", Toast.LENGTH_SHORT).show();
    }

}