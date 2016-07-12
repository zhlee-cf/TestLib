package com.open.im.bean;

/**
 * 封装会话消息的bean
 * 
 * @author Administrator
 * 
 */
public class MessageBean extends ProtocalObj {

//	private int msgId;// id
	private String fromUser;// 发送者
	private String nick;  // 昵称
	private String toUser;// 接收者
	private int type;// 信息类型
	private String body;// 信息内容
	private Long date;// 时间 存的是毫秒值 long型
	private String isRead;// 是否已读
	private String mark; // 标记这是跟谁的聊天
	private String stanzaId; // 消息标记id
	private String owner; // 标记这条消息是谁的
	private String thumbnail; // 缩影图
	private int unreadCount; // 未读消息数量
	private String receipt; // 消息的发送状态(0 收到消息 1 发送中 2 已发送 3 已送达 -1 发送失败)
	private String avatar;  // 消息对应人的头像的url

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

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	public int getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public MessageBean() {
	}

	public String getStanzaId() {
		return stanzaId;
	}

	public void setStanzaId(String stanzaId) {
		this.stanzaId = stanzaId;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public String getIsRead() {
		return isRead;
	}

	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

	@Override
	public String toString() {
		return "MessageBean{" +
//				"msgId=" + msgId +
				", fromUser='" + fromUser + '\'' +
				", toUser='" + toUser + '\'' +
				", type=" + type +
				", body='" + body + '\'' +
				", date=" + date +
				", isRead='" + isRead + '\'' +
				", mark='" + mark + '\'' +
				", stanzaId='" + stanzaId + '\'' +
				", owner='" + owner + '\'' +
				", thumbnail='" + thumbnail + '\'' +
				", unreadCount=" + unreadCount +
				", receipt='" + receipt + '\'' +
				'}';
	}
}
