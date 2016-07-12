package com.open.im.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.text.TextUtils;

import com.open.im.R;
import com.open.im.activity.ChatActivity;
import com.open.im.app.MyApp;
import com.open.im.bean.MessageBean;
import com.open.im.bean.ReceiveBean;
import com.open.im.bean.VCardBean;
import com.open.im.db.OpenIMDao;
import com.open.im.service.IMService;
import com.open.im.utils.MyBase64Utils;
import com.open.im.utils.MyConstance;
import com.open.im.utils.MyLog;
import com.open.im.utils.MyUtils;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.delay.packet.DelayInformation;

import java.util.Date;

/**
 * 自定义的会话消息接收监听
 *
 * @author Administrator
 */
public class MyChatMessageListener implements ChatMessageListener {

    private IMService ctx;
    private NotificationManager notificationManager;
    private SharedPreferences sp;
    private PowerManager.WakeLock wakeLock;
    private final OpenIMDao openIMDao;
    private final PowerManager pm;

    public MyChatMessageListener(IMService ctx, NotificationManager notificationManager) {
        this.ctx = ctx;
        this.notificationManager = notificationManager;
        openIMDao = OpenIMDao.getInstance(ctx);
        sp = ctx.getSharedPreferences(MyConstance.SP_NAME, 0);
        pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        long msgDate = new Date().getTime();

        // 离线消息包含此extension，里面存储有离线消息的时间
        DelayInformation delayInformation = (DelayInformation) message.getExtension(DelayInformation.NAMESPACE);
        if (delayInformation != null) {
            Date stamp = delayInformation.getStamp();
            msgDate = stamp.getTime();
        }

        String messageBody = message.getBody();
        if (TextUtils.isEmpty(messageBody)) {
            return;
        }
        String from = message.getFrom();
        String friendName = from.substring(0, from.indexOf("@"));
        String friendJid = friendName + "@" + MyConstance.SERVICE_HOST;
        VCardBean vCardBean = openIMDao.findSingleVCard(friendJid);
        if (vCardBean != null) {
            String nickName = vCardBean.getNick();
            String avatarUrl = vCardBean.getAvatar();

            MessageBean msg = new MessageBean();
            int msgType = 0;
            String msgImg = "";
            String msgBody = "";
            try {
                ReceiveBean receiveBean = MyBase64Utils.decodeToBean(messageBody);
                String type = receiveBean.getType();
                if (type.equals("image")) {
                    msgType = 1;
                    msgBody = messageBody.substring(0, messageBody.indexOf("&oim="));
                    msgImg = receiveBean.getProperties().getThumbnail();
                } else if (type.equals("voice")) {
                    msgType = 2;
                    msgBody = messageBody.substring(0, messageBody.indexOf("&oim="));
                    msgImg = "";
                } else if (type.equals("location")) {
                    msgType = 3;
                    msgBody = "location#" + receiveBean.getProperties().getLatitude() + "#" + receiveBean.getProperties().getLongitude() + "#" + receiveBean.getProperties().getDescription() + "#" + receiveBean.getProperties().getManner() + "#" + receiveBean.getProperties().getThumbnail();
                    msgImg = "";
                }
            } catch (Exception e) {
                msgType = 0;
                msgBody = messageBody;
                msgImg = "";
            }

            String username = sp.getString("username", null);
            msg.setFromUser(friendName);
            msg.setStanzaId(message.getStanzaId());
            msg.setToUser(message.getTo().substring(0, message.getTo().indexOf("@")));
            msg.setBody(msgBody);
            msg.setDate(msgDate);
            msg.setIsRead("0"); // 0表示未读 1表示已读
            msg.setType(msgType);
            msg.setThumbnail(msgImg);
            msg.setMark(username + "#" + friendName); // 存个标记 标记是跟谁聊天
            msg.setOwner(username);
            msg.setReceipt("0");  //收到消息
            msg.setNick(nickName);
            msg.setAvatar(avatarUrl);

            openIMDao.saveSingleMessage(msg);
            MyLog.showLog("App::" + MyApp.friendName);
            MyLog.showLog("消息::" + friendName);

            if (MyUtils.isTopActivity(ctx) && pm.isScreenOn() && friendName.equals(MyApp.friendName)) {
                // 通知栏不展示
                newMsgNotifyWhileChatting();
            } else {
                // 通知栏展示消息
                newMsgNotify(msg.getBody(), friendName, nickName);
            }
        }
    }

    /**
     * 震动加响铃通知消息，不在通知栏现实
     */
    private void newMsgNotifyWhileChatting() {
        Notification notification = new Notification();
        // 设置默认声音
        notification.defaults |= Notification.DEFAULT_SOUND;
        // 设定震动(需加VIBRATE权限)
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);
    }

//    /**
//     * 新消息通知
//     *
//     * @param messageBody
//     * @param friendName
//     */
//    private void newMsgNotify(String messageBody, String friendName, String nickName) {
//        CharSequence tickerText = "您有新消息，请注意查收！";
//        // 收到单人消息时，亮屏3秒钟
//        acquireWakeLock();
//        Notification notification = new Notification(R.mipmap.ic_launcher, tickerText, System.currentTimeMillis());
//        // 设置默认声音
//        notification.defaults |= Notification.DEFAULT_SOUND;
//        // 设定震动(需加VIBRATE权限)
//        notification.defaults |= Notification.DEFAULT_VIBRATE;
//        // 点击通知后 通知栏消失
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//
//        Intent intent = new Intent(ctx, ChatActivity.class);
//        intent.putExtra("friendName", friendName);
//        intent.putExtra("friendNick", nickName);
//        // 必须添加
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        notification.setLatestEventInfo(ctx, nickName, messageBody, contentIntent);
//        notificationManager.notify(MyConstance.NOTIFY_ID_MSG, notification);
//    }

    /**
     * 新消息通知
     */
    private void newMsgNotify(String messageBody, String friendName, String nickName) {
        CharSequence tickerText = "您有新消息，请注意查收！";
        // 收到单人消息时，亮屏
        acquireWakeLock();
        Intent intent = new Intent(ctx, ChatActivity.class);
        intent.putExtra("friendName", friendName);
        intent.putExtra("friendNick", nickName);
        // 必须添加
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(ctx)
                .setContentTitle(nickName)
                .setContentText(messageBody)
                .setContentIntent(contentIntent)
                .setTicker(tickerText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        // 设置默认声音
        notification.defaults |= Notification.DEFAULT_SOUND;
        // 设定震动(需加VIBRATE权限)
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.vibrate = new long[]{0, 100, 200, 300};
        // 设置LED闪烁
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.ledARGB = 0xff00ff00;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        // 点击通知后 通知栏消失
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(MyConstance.NOTIFY_ID_MSG, notification);
    }

    /**
     * 方法 点亮屏幕3秒钟 要加权限 <uses-permission
     * android:name="android.permission.WAKE_LOCK"></uses-permission>
     */
    private void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
            // wakeLock = powerManager.newWakeLock(PowerManager., tag)
            wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "lzh");
        }
        wakeLock.acquire(1000);
    }
}
