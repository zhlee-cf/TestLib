package com.open.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;

import com.open.im.R;
import com.open.im.app.MyApp;
import com.open.im.service.IMService;
import com.open.im.utils.MyBase64Utils;
import com.open.im.utils.MyConstance;
import com.open.im.utils.MyLog;
import com.open.im.utils.MyUtils;
import com.open.im.utils.ThreadUtil;
import com.open.im.utils.XMPPConnectionUtils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;

public class SplashActivity extends Activity {

    private SharedPreferences sp;
    private final int GO_LOGIN = 101;
    private final int GO_RE_LOGIN = 103;
    private final int GO_MAIN = 102;
    private SplashActivity act;
    private XMPPTCPConnection connection;
    private Intent service;

    /**
     * 登录状态
     */
    private static final int LOGIN_SUCCESS = 0;
    private static final int FAIL_PASSWORD_ERROR = 1;
    private static final int FAIL_UNKNOWN_USER = 2;
    private static final int INTERNET_ERROR = 3;
    private static final int LOGIN_FAIL = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        act = this;
        sp = getSharedPreferences(MyConstance.SP_NAME, 0);
        login();
    }

    @Override
    protected void onResume() {
        JPushInterface.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        JPushInterface.onPause(this);
        super.onPause();
    }

    private void login() {
        ThreadUtil.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                // 手机从开机到现在的毫秒值
                long startTime = SystemClock.uptimeMillis();
                String username = sp.getString("username", "");
                String password = sp.getString("password", "");
                boolean isIMServiceRunning = MyUtils.isServiceRunning(act, "com.open.im.service.IMService");
                if (isIMServiceRunning) {  // 判断IM主服务是否在运行
                    handler.sendEmptyMessage(GO_MAIN);
                } else {
                    if (TextUtils.isEmpty(username)) {
                        handler.sendEmptyMessage(GO_LOGIN);
                    } else {
                        if (TextUtils.isEmpty(password)) {
                            handler.sendEmptyMessage(GO_RE_LOGIN);
                        } else {
                            login(username, password);
                        }
                    }
                }
                long endTime = SystemClock.uptimeMillis();
                long passTime = endTime - startTime; // 联网的用时
                if (passTime < 1000) { // 联网很快，2秒内，就完成了
                    SystemClock.sleep(1000 - passTime);
                }
            }
        });
    }

    /**
     * 方法 登录
     */
    private void login(final String username, final String password) {
        XMPPConnectionUtils.initXMPPConnection(act);
        connection = MyApp.connection;
        ThreadUtil.runOnBackThread(new Runnable() {

            @Override
            public void run() {
                MyLog.showLog(Thread.currentThread().getName() + "正在执行。。。");
                try {
                    if (!connection.isConnected()) {
                        connection.connect();
                    }
                    connection.setPacketReplyTimeout(60 * 1000);
                    connection.login(username, MyBase64Utils.decode(password));

                    MyApp.username = username;

                    service = new Intent(act, IMService.class);
                    act.startService(service);

                    Intent intent = new Intent(act, MainActivity.class);
                    act.startActivity(intent);
                    finish();
                    handler.sendEmptyMessage(LOGIN_SUCCESS);
                } catch (SmackException e) {
                    handler.sendEmptyMessage(INTERNET_ERROR);
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    if (e.getMessage().contains("not-authorized")) {
                        // 未注册异常
                        handler.sendEmptyMessage(FAIL_UNKNOWN_USER);
                    } else if (e.getMessage().contains("bad-auth")) {
                        // 密码错误异常
                        handler.sendEmptyMessage(FAIL_PASSWORD_ERROR);
                    } else {
                        handler.sendEmptyMessage(LOGIN_FAIL);
                    }
                    e.printStackTrace();
                }
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GO_LOGIN:
                    // 跳转至登录页面
                    startActivity(new Intent(act, LoginActivity.class));
                    finish();// 结束当前activity
                    break;
                case GO_RE_LOGIN:
                    // 跳转至重新登录页面
                    startActivity(new Intent(act, ReLoginActivity.class));
                    finish();// 结束当前activity
                    break;
                case GO_MAIN:
                    // 跳转至主页面
                    startActivity(new Intent(act, MainActivity.class));
                    finish();// 结束当前activity
                    break;
                case LOGIN_SUCCESS:
                    MyUtils.showToast(act, "登录成功");
                    break;
                case INTERNET_ERROR:
                    MyUtils.showToast(act, "网络错误，请检查您的网络");
                    sp.edit().putString("password","").apply();
                    // 跳转到重新登录界面
                    act.startActivity(new Intent(act, ReLoginActivity.class));
                    act.finish();
                    break;
                case FAIL_UNKNOWN_USER:
                    MyUtils.showToast(act, "用户未注册");
                    // 跳转至登录页面
                    startActivity(new Intent(act, LoginActivity.class));
                    finish();// 结束当前activity
                    break;
                case FAIL_PASSWORD_ERROR:
                    MyUtils.showToast(act, "密码错误");
                    sp.edit().putString("password","").apply();
                    // 跳转到重新登录界面
                    act.startActivity(new Intent(act, ReLoginActivity.class));
                    act.finish();
                    break;
                case LOGIN_FAIL:
                    MyUtils.showToast(act, "登录失败");
                    sp.edit().putString("password","").apply();
                    // 跳转到重新登录界面
                    act.startActivity(new Intent(act, ReLoginActivity.class));
                    act.finish();
                    break;
            }
        }

        ;
    };
}
