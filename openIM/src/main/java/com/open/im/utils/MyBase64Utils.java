package com.open.im.utils;

import android.util.Base64;

import com.open.im.bean.ReceiveBean;

/**
 * Base64编码与解码工具类
 * Created by Administrator on 2016/4/7.
 */
public class MyBase64Utils {

    /**
     * 方法 编码
     * @param str
     * @return
     */
    public static String encodeToString(String str) {
        return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
    }

    /**
     * 方法 解码
     * @param str
     * @return
     */
    public static String decode(String str) {
        return new String(Base64.decode(str, Base64.DEFAULT));
    }

    /**
     * 直接将上传的结果解码成bean
     * @param str
     * @return
     */
    public static ReceiveBean decodeToBean(String str){
        if (str != null){
            int start = str.indexOf("&oim=") + 5;
            String substring = str.substring(start);
            String decode = decode(substring);
            ReceiveBean receiveBean = new ReceiveBean();
            receiveBean = (ReceiveBean) receiveBean.fromJson(decode);
            return receiveBean;
        }
        return null;
    }
}
