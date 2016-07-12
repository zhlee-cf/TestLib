package com.open.im.app;


import android.app.Activity;

import com.open.im.log.CrashHandler;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.litepal.LitePalApplication;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

public class MyApp extends LitePalApplication {
    /**
     * 在登录注册界面创建的连接对象 登录界面赋值
     */
    public static XMPPTCPConnection connection;
    /**
     * 应用是否在前台
     */
    public static boolean isActive;
    /**
     * 登录后 存储用户昵称
     */
    public static String nickName;
    /**
     * 登录后存储用户名
     */
    public static String username;
    /**
     * 正在聊天的好友的用户名
     */
    public static String friendName;

    /**
     * 头像地址
     */
    public static String avatarUrl;
    private static ArrayList<Activity> activities = new ArrayList<Activity>();;

    @Override
    public void onCreate() {
        super.onCreate();
        // 未捕获的异常都会发送到指定邮箱
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this, "1365260937@qq.com");

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    /**
     * 存放Activity到list中
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void clearActivity() {
        for (Activity activity : activities) {
            activity.finish();
        }
//        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

