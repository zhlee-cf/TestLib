package com.open.im.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.open.im.utils.MyLog;

import cn.jpush.android.api.JPushInterface;

/**
 * JPush接收者
 * Created by lzh12 on 2016/6/2.
 */
public class MyPushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        MyLog.showLog("收到JPush推送::" + intent.getAction());
        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            String notification = bundle.getString(JPushInterface.EXTRA_ALERT);
            MyLog.showLog("JPush通知的ID: " + notificationId);
            MyLog.showLog("[JPush通知内容: " + notification);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            MyLog.showLog("JPush消息内容:" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            MyLog.showLog("JPush连接变化::" + bundle.getBoolean(JPushInterface.EXTRA_CONNECTION_CHANGE));
        }
    }
}
