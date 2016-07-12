package com.open.im.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;

import com.open.im.app.MyApp;
import com.open.im.utils.MyConstance;
import com.open.im.utils.MyLog;
import com.open.im.utils.MyNetUtils;
import com.open.im.utils.MyUtils;
import com.open.im.utils.ThreadUtil;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.ping.PingManager;

import java.util.List;

public class BaseActivity extends FragmentActivity {
    private BaseActivity act;
    private boolean isFocus = true;
    private BroadcastReceiver newConnectReceiver;
    private XMPPTCPConnection connection;
    private BroadcastReceiver mHomeKeyDownReceiver;
    private PowerManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        MyApp.addActivity(this);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        connection = MyApp.connection;

        newConnectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                doNewConnection();
            }
        };
        IntentFilter filter = new IntentFilter(MyConstance.NEW_CONNECTION_ACTION);
        registerReceiver(newConnectReceiver, filter);

//        registerHomeKeyDownListener();
    }

    /**
     * 接收创建新的connection对象的广播里的方法，子类如需使用需要重写
     */
    protected void doNewConnection() {
    }

    @Override
    protected void onResume() {
        connection = MyApp.connection;
        ThreadUtil.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                if (connection != null) {
                    MyLog.showLog("应用可见_connected::" + connection.isConnected());
                    MyLog.showLog("应用可见_auth::" + connection.isAuthenticated());
                    MyLog.showLog("应用可见_socket_closed::" + connection.isSocketClosed());
                }
                if (connection == null || !connection.isConnected() || !connection.isAuthenticated()) {
//                    MyUtils.showToast(act, "应用已断开链接" + connection.isAuthenticated());
                    if (MyNetUtils.isNetworkConnected(act) && isFocus) {
                        sendBroadcast(new Intent(MyConstance.ACT_ONRESUME_ACTION));
                    }
                } else {
                    boolean isReachable = isServerReachable();
                    MyLog.showLog("isReachable::" + isReachable);
                    MyUtils.showToast(act, "应用可见,ping结果::" + isReachable);
                    if (!isReachable) {
                        if (isFocus) {
                            sendBroadcast(new Intent(MyConstance.APP_FOREGROUND_ACTION));
                        }
                    }
                    if (!MyApp.isActive ) {
                        MyApp.isActive = true;
                        MyLog.showLog("程序处于前台");
                        if (isReachable){
                            sendBroadcast(new Intent(MyConstance.INIT_OFFLINE_MESSAGE_ACTION));
                        }
                    }
                }
            }
        });
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        isFocus = hasFocus;
    }

    @Override
    protected void onDestroy() {
        if (newConnectReceiver != null) {
            unregisterReceiver(newConnectReceiver);
        }
        if (mHomeKeyDownReceiver != null) {
            unregisterReceiver(mHomeKeyDownReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ThreadUtil.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                if (!isAppOnForeground()) {
                    MyApp.isActive = false;
                    MyLog.showLog("程序处于后台");
//                    // 发送离开状态  这时收不到消息，别人发来的消息全部转成离线消息
//                    if (connection != null && connection.isConnected()) {
////                        Presence presence = new Presence(Presence.Type.available, null, -1, Presence.Mode.away);
//                        Presence presence = new Presence(Presence.Type.unavailable);
//                        try {
//                            connection.sendStanza(presence);
//                        } catch (SmackException.NotConnectedException e) {
//                            e.printStackTrace();
//                        }
//                    }
                }
            }
        });
    }

    /**
     * 程序是否在前台运行
     *
     * @return true 前台  false 后台
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 监听的是 系统发出的取消Dialog的广播  反正点击home键会发出 当应用在前台时，锁屏键也会发出
     * 勉强可以用来处理home键点击事件
     */
    private void registerHomeKeyDownListener() {

        mHomeKeyDownReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MyLog.showLog("收到广播");
                if (pm.isScreenOn()) {
                    finish();
                }
            }
        };
        registerReceiver(mHomeKeyDownReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    /**
     * ping服务器，10秒未收到回执则认为已掉线
     *
     * @return
     */
    private boolean isServerReachable() {
        PingManager pingManager = PingManager.getInstanceFor(connection);
        try {
            return pingManager.pingMyServer(false, 10 * 1000);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        }
    }
}