package com.open.im.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.open.im.service.IMService;
import com.open.im.utils.MyLog;
import com.open.im.utils.MyUtils;

/**
 * 收到广播时，判断IMService是否在运行中，若不在则启动服务
 */
public class TickAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MyLog.showLog("OpenIM收到Alarm广播");
//		if(!MyNetUtils.isNetworkConnected(context)){
//			return;
//		}
        boolean isIMServiceRunning = MyUtils.isServiceRunning(context, "com.open.im.service.IMService");
        if (!isIMServiceRunning) {
            context.startService(new Intent(context, IMService.class));
        }
    }
}
