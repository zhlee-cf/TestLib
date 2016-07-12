package com.open.im.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.open.im.R;
import com.open.im.activity.SubscribeActivity;
import com.open.im.app.MyApp;
import com.open.im.bean.SubBean;
import com.open.im.bean.VCardBean;
import com.open.im.db.OpenIMDao;
import com.open.im.service.IMService;
import com.open.im.utils.MyConstance;
import com.open.im.utils.MyLog;
import com.open.im.utils.MyVCardUtils;
import com.open.im.utils.ThreadUtil;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.Date;

/**
 * 监听收到好友请求
 *
 * @author Administrator
 */
public class MyAddFriendStanzaListener implements StanzaListener {

    private final NotificationManager notificationManager;
    private IMService imService;
    private XMPPTCPConnection connection;
    private PowerManager.WakeLock wakeLock;
    private final OpenIMDao openIMDao;
    private final Roster roster;

    public MyAddFriendStanzaListener(IMService imService, NotificationManager notificationManager) {
        this.imService = imService;
        connection = MyApp.connection;
        this.notificationManager = notificationManager;
        openIMDao = OpenIMDao.getInstance(imService);
        roster = Roster.getInstanceFor(connection);
    }

    @Override
    public void processPacket(Stanza packet) throws NotConnectedException {
        if (packet instanceof Presence) {
            final Presence presence = (Presence) packet;
            final String msgFrom = presence.getFrom();
            final String msgTo = presence.getTo();
            final String from = msgFrom.substring(0, msgFrom.indexOf("@"));
            final String to = msgTo.substring(0, msgTo.indexOf("@"));
            Type type = presence.getType();
            MyLog.showLog("type::" + type + "=========msgFrom::" + msgFrom);
            if (type.equals(Presence.Type.subscribe)) { // 收到添加好友申请
                MyLog.showLog("收到好友邀请:" + msgFrom);
                boolean isContains = roster.contains(msgFrom);
                MyLog.showLog("是否包含该好友::" + isContains);
                if (isContains) {
                    RosterEntry entry = roster.getEntry(msgFrom);
                    RosterPacket.ItemType itemType = entry.getType();
                    MyLog.showLog("itemType::" + itemType.name());
                    if ("both".equals(itemType.name())) {
                        return;
                    } else if ("to".equals(itemType.name()) || "none".equals(itemType.name())) {
                        Presence response = new Presence(Presence.Type.subscribed);
                        response.setTo(msgFrom);
                        connection.sendStanza(response);
                        //如果对方同意了好友请求，则创建好友，并且回复对方同意添加对方为好友
                        return;
                    }
                }
                ThreadUtil.runOnBackThread(new Runnable() {
                    @Override
                    public void run() {
                        VCardBean vCardBean = MyVCardUtils.queryVCard(msgFrom);
                        if (vCardBean != null) {
                            SubBean subBean = new SubBean();
                            subBean.setFromUser(msgFrom);
                            subBean.setAvatar(vCardBean.getAvatar());
                            subBean.setNick(vCardBean.getNick());
                            subBean.setToUser(msgTo);
                            subBean.setOwner(to);
                            subBean.setMsg(presence.getStatus());
                            subBean.setDate(new Date().getTime());
                            subBean.setState("0");
                            subBean.setMark(to + "#" + from);
                            openIMDao.saveSingleSub(subBean);
                            newMsgNotify(subBean.getMsg(), from);
                        }
                    }
                });

            } else if (type.equals(Type.subscribed)) {
                if ("3".equals(openIMDao.findSingleSub(to + "#" + from).getState())) {
                    openIMDao.updateSubByMark(to + "#" + from, "4");
                    openIMDao.deleteSingleSub(to + "#" + from);
                }
                try {
                    //如果对方同意了好友请求，则创建好友，并且回复对方同意添加对方为好友
                    roster.createEntry(msgFrom, msgFrom.substring(0, msgFrom.indexOf("@")), null);
                    Presence response = new Presence(Type.subscribe);
                    response.setTo(msgFrom);
                    connection.sendStanza(response);
                    ThreadUtil.runOnBackThread(new Runnable() {
                        @Override
                        public void run() {
                            VCardBean vCardBean = MyVCardUtils.queryVCard(msgFrom);
                            if (vCardBean != null) {
                                vCardBean.setJid(msgFrom);
                                openIMDao.saveSingleVCard(vCardBean);
                            }
                        }
                    });
                } catch (NotLoggedInException e) {
                    e.printStackTrace();
                } catch (NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPErrorException e) {
                    e.printStackTrace();
                }
            } else if (type.equals(Type.unsubscribed)) {  // 拒绝

                ThreadUtil.runOnBackThread(new Runnable() {
                    @Override
                    public void run() {
                        VCardBean singleVCard = openIMDao.findSingleVCard(msgFrom);
                        if (singleVCard == null) {
                            openIMDao.updateSubByMark(to + "#" + from, "5");
                            newMsgNotify(from + "已拒绝您的好友申请", "");
                            MyLog.showLog("对方已拒绝");
                        }
                    }
                });
            }
        }
    }

//    /**
//     * 新消息通知
//     *
//     * @param messageBody
//     * @param friendName
//     */
//    private void newMsgNotify(String messageBody, String friendName) {
//        CharSequence tickerText = "新的好友通知！";
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
//        Intent intent = new Intent(imService, SubscribeActivity.class);
//        // 必须添加
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent contentIntent = PendingIntent.getActivity(imService, 88, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        notification.setLatestEventInfo(imService, friendName, messageBody, contentIntent);
//        notificationManager.notify(MyConstance.NOTIFY_ID_SUB, notification);
//    }

    /**
     * 新消息通知
     */
    private void newMsgNotify(String messageBody, String friendName) {
        CharSequence tickerText = "新的好友通知！";
        // 收到单人消息时，亮屏
        acquireWakeLock();
        Intent intent = new Intent(imService, SubscribeActivity.class);
        // 必须添加
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(imService, 88, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(imService)
                .setContentTitle(friendName)
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
        notificationManager.notify(MyConstance.NOTIFY_ID_SUB, notification);
    }

    /**
     * 方法 点亮屏幕3秒钟 要加权限 <uses-permission
     * android:name="android.permission.WAKE_LOCK"></uses-permission>
     */
    private void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) imService.getSystemService(Context.POWER_SERVICE);
            // wakeLock = powerManager.newWakeLock(PowerManager., tag)
            wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "lzh");
        }
        wakeLock.acquire(1000);
    }
}

