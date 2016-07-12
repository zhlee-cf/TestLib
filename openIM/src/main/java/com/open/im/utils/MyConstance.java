package com.open.im.utils;

import android.net.Uri;

/**
 * 应用中的常量
 *
 * @author Administrator
 */
public class MyConstance {

    /**
     * 长文本上传url
     */
    public static final String UPLOAD_TEXT = "http://openim.top:8080/openstore/api/puttext.php";
    /**
     * 图片上传url
     */
    public static final String UPLOAD_IMAGE = "http://openim.top:8080/openstore/api/putimage.php";
    /**
     * 语音上传url
     */
    public static final String UPLOAD_VOICE = "http://openim.top:8080/openstore/api/putvoice.php";
    /**
     * 位置上传url
     */
    public static final String UPLOAD_LOCATION = "http://openim.top:8080/openstore/api/putlocation.php";
    /**
     * 头像上传url
     */
    public static final String UPLOAD_AVATAR = "http://openim.top:8080/openstore/api/putavastar.php";

    /**
     * sp名
     */
    public static final String SP_NAME = "config";
    /**
     * 消息通知栏
     */
    public static final int NOTIFY_ID_MSG = 8888;
    /**
     * 好友请求通知栏
     */
    public static final int NOTIFY_ID_SUB = 9999;

    /**
     * 服务器地址
     */
    public static final String SERVICE_HOST = "openim.top";
    /**
     * 当前版本客户端下载地址
     */
    public static final String CLIENT_URL = "http://openim.top:8080/apk/OpenIM-1.0.1.apk";
    /**
     * vCard变更通知RUI
     */
    public static final Uri URI_VCARD = Uri.parse("content://com.openim.vcard");
    /**
     * sub变更通知URI
     */
    public static final Uri URI_SUB = Uri.parse("content://com.openim.sub");
    /**
     * msg变更通知RUI
     */
    public static final Uri URI_MSG = Uri.parse("content://com.openim.msg");
    /**
     * Roster 版本号
     */
    public static final String ROSTER_VER = "roster_ver";
    /**
     * 界面可见时发送的广播的action
     */
    public static final String ACT_ONRESUME_ACTION = "com.openim.act.onresume.action";
    /**
     * APP可见时发送的广播
     */
    public static final String APP_FOREGROUND_ACTION = "com.openim.app.foreground.action";
    /**
     * 创建新的connection对象时，发送广播的action
     */
    public static final String NEW_CONNECTION_ACTION = "com.openim.base.activity.new.connection.action";
    /**
     * 应用前台可见时，如果是ping通状态，则会发出广播让初始化离线消息
     */
    public static final String INIT_OFFLINE_MESSAGE_ACTION = "com.openim.init.offline.message.action";
}
