package com.open.im.bean;

/**
 * 好友申请bean
 * Created by Administrator on 2016/3/24.
 */
public class SubBean extends ProtocalObj{
    private String fromUser;
    private String toUser;
    private long date;
    private String msg;
    private String state;   // 0 收到请求  1 同意对方请求 2 发出请求  3 对方同意请求
    private String avatar;
    private String nick;
    private String owner;
    private String mark;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "SubBean{" +
                "fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", date=" + date +
                ", msg='" + msg + '\'' +
                ", state='" + state + '\'' +
                ", avatar='" + avatar + '\'' +
                ", nick='" + nick + '\'' +
                ", owner='" + owner + '\'' +
                ", mark='" + mark + '\'' +
                '}';
    }
}
