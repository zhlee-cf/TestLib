package com.open.im.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

/**
 * Created by Administrator on 2016/4/14.
 * 动画相关的工具类
 */
public class MyAnimationUtils {

    public static void rotate(View view) {
        Animation a = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        a.setDuration(300);
//        a.setInterpolator(new AccelerateInterpolator());
//        a.setFillAfter(true);
        // 保持动画最后执行完的状态
        view.startAnimation(a);
    }
}
