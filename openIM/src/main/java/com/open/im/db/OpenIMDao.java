package com.open.im.db;

import android.content.Context;
import android.database.Cursor;

import com.open.im.app.MyApp;
import com.open.im.bean.MessageBean;
import com.open.im.bean.SubBean;
import com.open.im.bean.VCardBean;
import com.open.im.utils.MyConstance;
import com.open.im.utils.MyLog;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 使用LitePal操作数据库
 * Created by Administrator on 2016/4/18.
 */
public class OpenIMDao {

    private static OpenIMDao instance;
    private Context ctx;

    private OpenIMDao(Context ctx) {
        this.ctx = ctx;
        // 创建数据库
        Connector.getDatabase();
    }

    /**
     * 单例模式
     *
     * @return openIMDao对象
     */
    public static synchronized OpenIMDao getInstance(Context ctx) {
        if (instance == null) {
            instance = new OpenIMDao(ctx);
        }
        return instance;
    }

    /**====================================== 操作VCard ==========================================*/

    /**
     * 保存成组的VCard信息
     *
     * @param list 成组的VCardBean
     */
    public void saveAllVCard(Collection<VCardBean> list) {
        if (list != null) {
            deleteAllVCard();
            DataSupport.saveAll(list);
            ctx.getContentResolver().notifyChange(MyConstance.URI_VCARD, null);
        }
    }

    /**
     * 保存一条VCard信息到数据库
     *
     * @param vCardBean
     */
    public synchronized void saveSingleVCard(VCardBean vCardBean) {
        vCardBean.save();
        ctx.getContentResolver().notifyChange(MyConstance.URI_VCARD, null);
        MyLog.showLog("保存铭牌");
    }

    /**
     * 更新单个的VCard信息
     *
     * @param vCardBean
     */
    public void updateSingleVCard(VCardBean vCardBean) {
        VCardBean singleVCard = findSingleVCard(vCardBean.getJid());
        if (singleVCard != null) {
            singleVCard.delete();
        }
        vCardBean.save();
        ctx.getContentResolver().notifyChange(MyConstance.URI_VCARD, null);
    }

    /**
     * 删除指定的一条VCard
     *
     * @param userJid
     */
    public synchronized void deleteSingleVCard(String userJid) {
        VCardBean singleVCard = findSingleVCard(userJid);
        if (singleVCard != null) {
            singleVCard.delete();
            ctx.getContentResolver().notifyChange(MyConstance.URI_VCARD, null);
            MyLog.showLog("删除铭牌");
        }
    }

    /**
     * 删除VCard表中所有的数据
     */
    public void deleteAllVCard() {
        DataSupport.deleteAll(VCardBean.class);
    }

    /**
     * 根据userJid查询相应的VCard信息
     *
     * @param userJid
     * @return
     */
    public VCardBean findSingleVCard(String userJid) {
        List<VCardBean> vCardBeans = DataSupport.where(DBColumns.JID + " = ?", userJid).find(VCardBean.class);
        if (vCardBeans != null && vCardBeans.size() > 0) {
            return vCardBeans.get(0);
        }
        return null;
    }

    /**
     * 查询所有的VCard信息  不包括自己
     *
     * @return
     */
    public List<VCardBean> findAllVCard() {
        return DataSupport.where(DBColumns.JID + " != ?", MyApp.username + "@" + MyConstance.SERVICE_HOST).find(VCardBean.class);
    }

    /**====================================== 操作聊天信息 ==========================================*/


    /**
     * 查询最近50条消息中有没有这个stanzaId
     *
     * @param stanzaId
     * @return
     */
    public boolean isMessageExist(String stanzaId) {
        List<MessageBean> messageBeans = DataSupport.where(DBColumns.STANZA_ID + " = ?", stanzaId).limit(50).find(MessageBean.class);
        return messageBeans != null && messageBeans.size() > 0;
    }

    /**
     * 保存一条聊天信息到数据库
     *
     * @param messageBean
     */
    public void saveSingleMessage(MessageBean messageBean) {
        boolean messageExist = isMessageExist(messageBean.getStanzaId());
        if (!messageExist) {
            messageBean.save();
            // 发出通知，群组数据库发生变化了
            ctx.getContentResolver().notifyChange(MyConstance.URI_MSG, null);
        }
//        else {
//            messageBean.save();
//            // 发出通知，群组数据库发生变化了
//            ctx.getContentResolver().notifyChange(MyConstance.URI_MSG, null);
//        }
    }

    /**
     * 删除指定的聊天信息
     *
     * @param stanzaId
     */
    public void deleteSingleMessage(String stanzaId) {
        MessageBean singleMessage = findSingleMessage(stanzaId);
        if (singleMessage != null) {
            singleMessage.delete();
            // 发出通知，群组数据库发生变化了
            ctx.getContentResolver().notifyChange(MyConstance.URI_MSG, null);
        }
    }

    /**
     * 通过消息的stanzaId查找指定的消息
     *
     * @param stanzaId
     * @return
     */
    public MessageBean findSingleMessage(String stanzaId) {
        List<MessageBean> messageBeans = DataSupport.where(DBColumns.STANZA_ID + " = ?", stanzaId).find(MessageBean.class);
        if (messageBeans != null && messageBeans.size() > 0) {
            return messageBeans.get(0);
        }
        return null;
    }

    /**
     * 通过mark查询聊天信息  每次查询5条
     *
     * @param offset 查询偏移量
     * @param mark
     * @return
     */
    public List<MessageBean> findMessageByMark(String mark, int offset) {
        List<MessageBean> messageBeans = DataSupport.where(DBColumns.MARK + " = ?", mark).order(DBColumns.ID + " desc").limit(10).offset(offset).find(MessageBean.class);
        if (messageBeans != null) {
            Collections.reverse(messageBeans);
        }
        return messageBeans;
    }

    /**
     * 通过mark删除与指定人的聊天消息
     *
     * @param mark
     */
    public void deleteMessageByMark(String mark) {
        DataSupport.deleteAll(MessageBean.class, DBColumns.MARK + " = ?", mark);
        // 发出通知，群组数据库发生变化了
        ctx.getContentResolver().notifyChange(MyConstance.URI_MSG, null);
    }

    /**
     * 通过mark删除当前用户的所有的聊天信息
     *
     * @param owner
     */
    public void deleteMessageByOwner(String owner) {
        DataSupport.deleteAll(MessageBean.class, DBColumns.OWNER + " = ?", owner);
        // 发出通知，群组数据库发生变化了
        ctx.getContentResolver().notifyChange(MyConstance.URI_MSG, null);
    }

    /**
     * 删除所有的聊天消息
     */
    public void deleteAllMessage() {
        DataSupport.deleteAll(MessageBean.class);
        // 发出通知，群组数据库发生变化了
        ctx.getContentResolver().notifyChange(MyConstance.URI_MSG, null);
    }

    /**
     * 去重查询  正在聊天的会话
     */
    public List<MessageBean> queryConversation(String owner) {
        List<MessageBean> list = new ArrayList<MessageBean>();
        Cursor cursor = DataSupport.findBySQL("select distinct * from " + DBColumns.TABLE_MSG + " where " + DBColumns.OWNER + " = ? group by " + DBColumns.MARK + " order by " + DBColumns.ID + " desc", owner);
        while (cursor.moveToNext()) {
            MessageBean bean = new MessageBean();
            bean.setFromUser(cursor.getString(cursor.getColumnIndex(DBColumns.FROM_USER)));
            bean.setToUser(cursor.getString(cursor.getColumnIndex(DBColumns.TO_USER)));
            bean.setDate(cursor.getLong(cursor.getColumnIndex(DBColumns.DATE)));
            bean.setBody(cursor.getString(cursor.getColumnIndex(DBColumns.BODY)));
            bean.setType(cursor.getInt(cursor.getColumnIndex(DBColumns.TYPE)));
            bean.setReceipt(cursor.getString(cursor.getColumnIndex(DBColumns.RECEIPT)));
            bean.setNick(cursor.getString(cursor.getColumnIndex(DBColumns.NICK)));
            bean.setAvatar(cursor.getString(cursor.getColumnIndex(DBColumns.AVATAR)));
            bean.setMark(cursor.getString(cursor.getColumnIndex(DBColumns.MARK)));
//            bean.setOwner(cursor.getString(cursor.getColumnIndex(DBColumns.OWNER)));
//            bean.setIsRead(cursor.getString(cursor.getColumnIndex(DBColumns.ISREAD)));
//            bean.setStanzaId(cursor.getString(cursor.getColumnIndex(DBColumns.STANZA_ID)));
            list.add(bean);
        }
        return list;
    }

    /**
     * 修改与指定好友的聊天消息存储的用户头像
     *
     * @param mark
     * @param avatarUrl
     */
    public void updateMessageAvatar(String mark, String avatarUrl) {
        MessageBean messageBean = new MessageBean();
        messageBean.setAvatar(avatarUrl);
        messageBean.updateAll(DBColumns.MARK + " = ?", mark);
        // 发出通知，群组数据库发生变化了
        ctx.getContentResolver().notifyChange(MyConstance.URI_MSG, null);
    }

    /**
     * 修改与指定好友的聊天消息为已读状态
     *
     * @param mark
     */
    public void updateMessageRead(String mark) {
        MessageBean messageBean = new MessageBean();
        messageBean.setIsRead("1");
        messageBean.updateAll(DBColumns.MARK + " = ?", mark);
        // 发出通知，群组数据库发生变化了
        ctx.getContentResolver().notifyChange(MyConstance.URI_MSG, null);
    }

    /**
     * 修改指定消息的回执状态
     *
     * @param stanzaId
     * @param receiptState
     */
    public void updateMessageReceipt(String stanzaId, String receiptState) {
        MessageBean messageBean = new MessageBean();
        messageBean.setReceipt(receiptState);
        messageBean.updateAll(DBColumns.STANZA_ID + " = ?", stanzaId);
        // 发出通知，群组数据库发生变化了
        ctx.getContentResolver().notifyChange(MyConstance.URI_MSG, null);
    }

    /**
     * 查询与指定好友的未读消息个数
     *
     * @param mark
     * @return
     */
    public int queryUnreadMessageCount(String mark) {
        return DataSupport.where(DBColumns.ISREAD + " = ? and " + DBColumns.MARK + " = ?", "0", mark).count(MessageBean.class);
    }

    /**
     * 查询指定消息的回执状态
     *
     * @param stanzaId 消息唯一标识
     * @return 消息状态   0 收到消息  1发送中 2已发送 3已送达 4发送失败
     */
    public String queryMessageReceipt(String stanzaId) {
        List<MessageBean> messageBeans = DataSupport.where(DBColumns.STANZA_ID + " = ?", stanzaId).select(DBColumns.RECEIPT).find(MessageBean.class);
        if (messageBeans != null && messageBeans.size() > 0) {
            return messageBeans.get(0).getReceipt();
        }
        return null;
    }


    /**====================================== 操作好友申请 ==========================================*/

    /**
     * 保存一条好友订阅信息到数据库
     *
     * @param subBean
     */
    public void saveSingleSub(SubBean subBean) {
        SubBean singleSub = findSingleSub(subBean.getMark());
        if (singleSub != null) {
            singleSub.delete();
        }
        subBean.save();
        ctx.getContentResolver().notifyChange(MyConstance.URI_SUB, null);
    }

    /**
     * 根据mark查询sub信息
     *
     * @param mark
     * @return
     */
    public SubBean findSingleSub(String mark) {
        List<SubBean> subBeans = DataSupport.where(DBColumns.MARK + " = ?", mark).find(SubBean.class);
        if (subBeans != null && subBeans.size() > 0) {
            return subBeans.get(0);
        }
        return null;
    }

    /**
     * 根据mark删除指定的sub信息
     *
     * @param mark
     */
    public void deleteSingleSub(String mark) {
        DataSupport.deleteAll(SubBean.class, DBColumns.MARK + " = ?", mark);
        ctx.getContentResolver().notifyChange(MyConstance.URI_SUB, null);
    }

    /**
     * 删除所有的好友申请 清空表
     */
    public void deleteAllSub() {
        DataSupport.deleteAll(SubBean.class);
        ctx.getContentResolver().notifyChange(MyConstance.URI_SUB, null);
    }

    /**
     * 删除指定用户对应的所有的订阅申请
     *
     * @param owner
     */
    public void deleteSubByOwner(String owner) {
        DataSupport.deleteAll(SubBean.class, DBColumns.OWNER + " = ?", owner);
        ctx.getContentResolver().notifyChange(MyConstance.URI_SUB, null);
    }

    /**
     * 根据mark修改好友的订阅状态
     *
     * @param mark
     * @param subState 0 收到请求  1 同意对方请求 2 发出请求  3 对方同意请求
     */
    public void updateSubByMark(String mark, String subState) {
        SubBean subBean = new SubBean();
        subBean.setState(subState);
        subBean.updateAll(DBColumns.MARK + " = ?", mark);
        ctx.getContentResolver().notifyChange(MyConstance.URI_SUB, null);
    }

    /**
     * 倒序查询最新的指定条数的好友订阅信息
     *
     * @param owner
     * @param offset
     * @param limit
     * @return
     */
    public List<SubBean> findSubByOwner(String owner, int limit, int offset) {
        List<SubBean> subBeans = DataSupport.where(DBColumns.OWNER + " = ?", owner).order(DBColumns.ID + " desc").limit(limit).offset(offset).find(SubBean.class);
        if (subBeans != null) {
            Collections.reverse(subBeans);
        }
        return subBeans;
    }

    /**
     * 查找owner的最近的三条好友申请
     *
     * @param owner
     * @param limit
     * @return
     */
    public List<String> findSubByOwner4Avatar(String owner, int limit) {
        ArrayList<String> avatars = new ArrayList<String>();
        List<SubBean> subBeans = DataSupport.where(DBColumns.OWNER + " = ? and " + DBColumns.STATE + " = ?", owner, "0").order(DBColumns.ID + " desc").limit(limit).find(SubBean.class);
        if (subBeans != null && subBeans.size() > 0) {
            for (SubBean bean : subBeans) {
                avatars.add(bean.getAvatar());
            }
        }
        return avatars;
    }
}
