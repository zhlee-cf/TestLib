package com.open.im.utils;

import com.open.im.app.MyApp;
import com.open.im.bean.VCardBean;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

/**
 * 账户信息工具类
 * Created by Administrator on 2016/3/21.
 */
public class MyVCardUtils {

    public static VCardBean queryVCard(String friendJid) {
        VCardManager vCardManager = VCardManager.getInstanceFor(MyApp.connection);
        final VCardBean bean = new VCardBean();
        final VCard vCard;
        try {
            if (friendJid == null) {
                vCard = vCardManager.loadVCard();
                friendJid = MyApp.username + "@" + MyConstance.SERVICE_HOST;
            } else {
                vCard = vCardManager.loadVCard(friendJid);
            }

            String nickName = vCard.getNickName();
            String homeAddress = vCard.getField("HOME_ADDRESS");
            String email = vCard.getEmailHome();
            String phone = vCard.getField("PHONE");
            String sex = vCard.getField("SEX");
            String desc = vCard.getField("DESC");
            String bday = vCard.getField("BDAY");
            String avatarUrl = vCard.getField("AVATAR_URL");
            bean.setAvatar(avatarUrl);
            if (nickName != null) {
                bean.setNick(nickName);
            } else {
                String nick = friendJid.substring(0, friendJid.indexOf("@"));
                bean.setNick(nick);
                vCard.setNickName(nick);
                vCardManager.saveVCard(vCard);
            }
            if (homeAddress != null) {
                bean.setAddress(homeAddress);
            } else {
                bean.setAddress("未填写");
            }
            if (email != null) {
                bean.setEmail(email);
            } else {
                bean.setEmail("未填写");
            }
            if (phone != null) {
                bean.setPhone(phone);
            } else {
                bean.setPhone("未填写");
            }
            if (sex != null) {
                bean.setSex(sex);
            } else {
                bean.setSex("未填写");
            }
            if (desc != null) {
                bean.setDesc(desc);
            } else {
                bean.setDesc("未填写");
            }
            if (bday != null) {
                bean.setBday(bday);
            } else {
                bean.setBday("未填写");
            }
            return bean;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
