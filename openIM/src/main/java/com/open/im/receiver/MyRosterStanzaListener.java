package com.open.im.receiver;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Xml;

import com.open.im.app.MyApp;
import com.open.im.bean.SubBean;
import com.open.im.bean.VCardBean;
import com.open.im.db.OpenIMDao;
import com.open.im.utils.MyConstance;
import com.open.im.utils.MyLog;
import com.open.im.utils.MyVCardUtils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;

/**
 * 通讯录监听
 * Created by Administrator on 2016/4/25.
 */
public class MyRosterStanzaListener implements StanzaListener {

    private OpenIMDao openIMDao;
    private XmlPullParser xmlPullParser;
    private ArrayList<VCardBean> list;
    private SharedPreferences sp;

    public MyRosterStanzaListener(Context ctx) {
        openIMDao = OpenIMDao.getInstance(ctx);
        xmlPullParser = Xml.newPullParser();
        sp = ctx.getSharedPreferences(MyConstance.SP_NAME, 0);
    }

    @Override
    public void processPacket(Stanza stanza) throws SmackException.NotConnectedException {
        if (stanza instanceof IQ) {
            IQ iq = (IQ) stanza;
            if (RosterPacket.NAMESPACE.equals(iq.getChildElementNamespace())) {
                String receive = iq.getChildElementXML().toString();
                MyLog.showLog("receive::" + receive);
                String rosterVer;
                String userJid;
                String subType;
                String user;
                try {
                    xmlPullParser.setInput(new StringReader(receive));
                    while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                            String tagName = xmlPullParser.getName();
                            if ("query".equals(tagName)) {
                                rosterVer = xmlPullParser.getAttributeValue(0);
                                sp.edit().putString(MyConstance.ROSTER_VER, rosterVer).apply();
                                list = new ArrayList<>();
                                MyLog.showLog("rosterVer::_Receive" + rosterVer);
                            } else if ("item".equals(tagName)) {
                                userJid = xmlPullParser.getAttributeValue(0);
                                subType = xmlPullParser.getAttributeValue(null, "subscription");
                                user = userJid.substring(0, userJid.indexOf("@"));
                                VCardBean vCardBean = MyVCardUtils.queryVCard(userJid);
                                if ("both".equals(subType)) {
                                    if (vCardBean != null) {
                                        vCardBean.setJid(userJid);
                                        list.add(vCardBean);
                                    }
                                } else if ("to".equals(subType)) {
                                    if (vCardBean != null) {
                                        SubBean subBean = new SubBean();
                                        subBean.setFromUser(MyApp.username + "@" + MyConstance.SERVICE_HOST);
                                        subBean.setAvatar(vCardBean.getAvatar());
                                        subBean.setNick(vCardBean.getNick());
                                        subBean.setToUser(userJid);
                                        subBean.setOwner(user);
                                        subBean.setMsg("您订阅了他");
                                        subBean.setDate(new Date().getTime());
                                        subBean.setState("3");
                                        subBean.setMark(MyApp.username + "#" + user);
                                        openIMDao.saveSingleSub(subBean);
                                    }
                                } else if ("from".equals(subType)) {
                                    if (vCardBean != null) {
                                        SubBean subBean = new SubBean();
                                        subBean.setFromUser(userJid);
                                        subBean.setAvatar(vCardBean.getAvatar());
                                        subBean.setNick(vCardBean.getNick());
                                        subBean.setToUser(MyApp.username + "@" + MyConstance.SERVICE_HOST);
                                        subBean.setOwner(MyApp.username);
                                        subBean.setMsg("他订阅了您");
                                        subBean.setDate(new Date().getTime());
                                        subBean.setState("0");
                                        subBean.setMark(MyApp.username + "#" + user);
                                        openIMDao.saveSingleSub(subBean);
                                    }
                                }
                                MyLog.showLog("userJid::" + userJid);
                                MyLog.showLog("subType::" + subType);
                            }
                        }
                        xmlPullParser.next();
                    }
                    MyLog.showLog("list::" + list);
                    openIMDao.saveAllVCard(list);
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
