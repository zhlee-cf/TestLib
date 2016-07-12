package com.open.im.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.open.im.R;
import com.open.im.app.MyApp;
import com.open.im.bean.SubBean;
import com.open.im.bean.VCardBean;
import com.open.im.db.OpenIMDao;
import com.open.im.utils.MyBitmapUtils;
import com.open.im.utils.MyConstance;
import com.open.im.utils.MyFileUtils;
import com.open.im.utils.MyLog;
import com.open.im.utils.MyUtils;
import com.open.im.utils.MyVCardUtils;
import com.open.im.utils.ThreadUtil;
import com.open.im.view.CircularImage;
import com.open.im.view.MyDialog;
import com.open.im.wheel.SelectBirthday;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.Date;

/**
 * 用户信息界面
 */
public class UserInfoActivity extends BaseActivity implements OnClickListener {

    TextView tvTitle;
    ImageButton ibBack;
    TextView tvBack;
    ImageView ivFlush;
    ListView mListView;
    Button btn1;
    Button btn2;
    LinearLayout llRoot;

    private static final int QUERY_SUCCESS = 100;
    private static final int SAVE_SUCCESS = 101;
    private UserInfoActivity act;
    private String[] items = {"头像:", "用户:", "昵称:", "性别:", "生日:", "地址:", "邮箱:", "电话:", "签名:"};
    private int[] icons = {R.mipmap.info_camera, R.mipmap.info_user, R.mipmap.info_nick, R.mipmap.info_sex, R.mipmap.info_birth, R.mipmap.info_nick, R.mipmap.info_nick, R.mipmap.info_phone, R.mipmap.info_desc};
    private VCard vCard;
    private String nickName;
    private String homeAddress;
    private String email;
    private String phone;
    private String sex;
    private String desc;
    private String bday;
    protected SelectBirthday birth;
    private VCardManager vCardManager;
    private int type = 0;

    private XMPPTCPConnection connection;
    private MyDialog pd;
    private VCardBean vCardBean;
    private Bitmap bitmap;
    private String avatarUrl;
    private MyBitmapUtils bitmapUtils;
    private String avatarPath;
    private String friendJid;
    private String friendName;
    private int lastPosition;
    private OpenIMDao openIMDao;
    private int response;
    private Intent intent;
    private Roster roster;

    // 创建一个以当前时间为名称的文件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        // 初始化控件
        initView();
        // 初始化数据
        initData();

        register();

    }

    /**
     * 注册条目点击事件
     */
    private void register() {

        btn2.setOnClickListener(this);
        btn1.setOnClickListener(this);
        ibBack.setOnClickListener(this);
        ivFlush.setOnClickListener(this);
        tvBack.setOnClickListener(this);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lastPosition = view.getFirstVisiblePosition();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(act, UserInfoUpdateActivity.class);
                int vCardType;
                switch (position) {
                    case 0: // 头像
                        vCardType = 0;
                        Intent avatarIntent = new Intent(act, AvatarActivity.class);
                        avatarIntent.putExtra("type", type);
                        avatarIntent.putExtra("avatarUrl", avatarUrl);
                        avatarIntent.putExtra("nickName", nickName);
                        startActivityForResult(avatarIntent, vCardType);

                        break;
                    case 2: // 昵称
                        if (type == 0) {
                            vCardType = 2;
                            intent.putExtra("info", nickName);
                            intent.putExtra("type", vCardType);
                            startActivityForResult(intent, vCardType);
                        }
                        break;
                    case 3: // 性别
                        if (type == 0) {
                            vCardType = 3;
                            Intent sexIntent = new Intent(act, UserSexUpdateActivity.class);
                            sexIntent.putExtra("sex", sex);
                            startActivityForResult(sexIntent, vCardType);
                        }
                        break;
                    case 4: // 生日
                        if (type == 0) {
                            birth = new SelectBirthday(act, bday);
                            birth.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0);
                            birth.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    MyLog.showLog("Birth:" + birth.getBirthday());
                                    if (birth.getBirthday() != null) {
                                        bday = birth.getBirthday();
                                        vCard.setField("BDAY", bday);
                                        vCardBean.setBday(bday);
                                        TextView tv_info = (TextView) mListView.getChildAt(position).findViewById(R.id.tv_info);
                                        tv_info.setText(bday);
                                    }
                                }
                            });
                        }
                        break;
                    case 5: // 地址
                        if (type == 0) {
                            vCardType = 5;
                            intent.putExtra("info", homeAddress);
                            intent.putExtra("type", vCardType);
                            startActivityForResult(intent, vCardType);
                        }
                        break;
                    case 6: // 邮箱
                        if (type == 0) {
                            vCardType = 6;
                            intent.putExtra("info", email);
                            intent.putExtra("type", vCardType);
                            startActivityForResult(intent, vCardType);
                        }
                        break;
                    case 7: // 电话
                        if (type == 0) {
                            vCardType = 7;
                            intent.putExtra("info", phone);
                            intent.putExtra("type", vCardType);
                            startActivityForResult(intent, vCardType);
                        }
                        break;
                    case 8: // 签名
                        if (type == 0) {
                            vCardType = 8;
                            intent.putExtra("info", desc);
                            intent.putExtra("type", vCardType);
                            startActivityForResult(intent, vCardType);
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String info;

        MyUtils.showToast(act, vCard + "----VCard回调");
        MyUtils.showToast(act, vCardManager + "----VCardManager回调");

        if (data != null && requestCode != 11 && vCard != null) {
            info = data.getDataString();
            switch (requestCode) {
                case 0:
                    bitmap = BitmapFactory.decodeFile(info);
                    avatarPath = info;
                    ImageView iv_avatar = (ImageView) mListView.getChildAt(requestCode - lastPosition).findViewById(R.id.iv_avatar);
                    iv_avatar.setImageBitmap(bitmap);
                    break;
                case 2:
                    if (!TextUtils.isEmpty(info)) {
                        vCardBean.setNick(info);
                        TextView tv_info = (TextView) mListView.getChildAt(requestCode - lastPosition).findViewById(R.id.tv_info);
                        tv_info.setText(info);
                        vCard.setNickName(info);
                    }
                    break;
                case 3:
                    if (!TextUtils.isEmpty(info)) {
                        vCardBean.setSex(info);
                        TextView tv_info = (TextView) mListView.getChildAt(requestCode - lastPosition).findViewById(R.id.tv_info);
                        tv_info.setText(info);
                        vCard.setField("SEX", info);
                    }
                    break;
                case 4:
                    break;
                case 5:
                    if (!TextUtils.isEmpty(info)) {
                        vCardBean.setAddress(info);
                        TextView tv_info = (TextView) mListView.getChildAt(requestCode - lastPosition).findViewById(R.id.tv_info);
                        tv_info.setText(info);
                        vCard.setField("HOME_ADDRESS", info);
                    }
                    break;
                case 6:
                    if (!TextUtils.isEmpty(info)) {
                        vCardBean.setEmail(info);
                        TextView tv_info = (TextView) mListView.getChildAt(requestCode - lastPosition).findViewById(R.id.tv_info);
                        tv_info.setText(info);
                        vCard.setEmailHome(info);
                    }
                    break;
                case 7:
                    if (!TextUtils.isEmpty(info)) {
                        vCardBean.setPhone(info);
                        TextView tv_info = (TextView) mListView.getChildAt(requestCode - lastPosition).findViewById(R.id.tv_info);
                        tv_info.setText(info);
                        vCard.setField("PHONE", info);
                    }
                    break;
                case 8:
                    if (!TextUtils.isEmpty(info)) {
                        vCardBean.setDesc(info);
                        /**
                         * lv.getChildAt只能获取到可见的view 有可能会越界  bug已解决
                         */
                        TextView tv_info = (TextView) mListView.getChildAt(requestCode - lastPosition).findViewById(R.id.tv_info);
                        tv_info.setText(info);
                        vCard.setField("DESC", info);
                    }
                    break;
            }
        }
    }

    private void pdDismiss() {
        if (pd != null && pd.isShowing() && act != null) {
            pd.dismiss();
        }
    }

    /**
     * 初始化数据 查询VCard信息
     */
    private void initData() {
        pd = new MyDialog(act);
        bitmapUtils = new MyBitmapUtils(act);
        openIMDao = OpenIMDao.getInstance(act);
        intent = getIntent();
        friendJid = intent.getStringExtra("friendJid");
        type = getIntent().getIntExtra("type", 0);

        roster = Roster.getInstanceFor(connection);

        if (friendJid == null) {
            if (connection != null && connection.isAuthenticated()) {
                vCardManager = VCardManager.getInstanceFor(connection);
            }
            ThreadUtil.runOnBackThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (vCardManager != null) {
                            vCard = vCardManager.loadVCard();
                        }
                    } catch (NoResponseException | XMPPErrorException | NotConnectedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            friendName = friendJid.substring(0, friendJid.indexOf("@"));
        }
        queryVCard();
    }

    /**
     * 方法 查询VCard信息
     */
    private void queryVCard() {
        pd.show();
        ThreadUtil.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                if (type == 0) {  // 个人信息修改界面
                    friendJid = MyApp.username + "@" + MyConstance.SERVICE_HOST;
                    vCardBean = openIMDao.findSingleVCard(friendJid);
                    if (vCardBean == null) {
                        vCardBean = MyVCardUtils.queryVCard(null);
                        if (vCardBean != null) {
                            vCardBean.setJid(friendJid);
                            openIMDao.saveSingleVCard(vCardBean);
                        }
                    }
                } else if (type == 1 || type == 3 || type == 4) {  // 查询的陌生人
                    vCardBean = openIMDao.findSingleVCard(friendJid);
                    if (vCardBean == null) {
                        vCardBean = MyVCardUtils.queryVCard(friendJid);
                        if (vCardBean != null) {
                            vCardBean.setJid(friendJid);
                        }
                    }
                } else if (type == 2) {  // 从通讯录进入
                    vCardBean = openIMDao.findSingleVCard(friendJid);
                    if (vCardBean == null) {
                        vCardBean = MyVCardUtils.queryVCard(friendJid);
                        if (vCardBean != null) {
                            vCardBean.setJid(friendJid);
                            openIMDao.saveSingleVCard(vCardBean);
                        }
                    }
                }
                if (vCardBean != null) {
                    nickName = vCardBean.getNick();
                    homeAddress = vCardBean.getAddress();
                    email = vCardBean.getEmail();
                    phone = vCardBean.getPhone();
                    sex = vCardBean.getSex();
                    desc = vCardBean.getDesc();
                    bday = vCardBean.getBday();
                    avatarUrl = vCardBean.getAvatar();
                    handler.sendEmptyMessage(QUERY_SUCCESS);
                } else {
                    pdDismiss();
                    MyUtils.showToast(act, "您已离线");
                    btn1.setVisibility(View.GONE);
                    btn2.setVisibility(View.GONE);
                    tvTitle.setText("离线");
                    tvBack.setText("返回");
                }
            }
        });
    }

    private void initView() {

        llRoot = (LinearLayout) findViewById(R.id.ll_root);
        mListView = (ListView) findViewById(R.id.lv_userinfo);
        btn2 = (Button) findViewById(R.id.btn_2);
        btn1 = (Button) findViewById(R.id.btn_1);
        ivFlush = (ImageView) findViewById(R.id.iv_flush);
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvBack = (TextView) findViewById(R.id.tv_back);

        act = this;
        connection = MyApp.connection;
        lastPosition = 0;
    }

    /**
     * 方法 发出好友请求
     */
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setMessage("添加为好友？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ThreadUtil.runOnBackThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            /**
                             * 添加好友不再是直接创建好友了，而是先发出一个订阅请求，对方同意后，才创建好友
                             */
                            Presence presence = new Presence(Presence.Type.subscribe);
                            presence.setTo(friendJid);
                            //在此处可以设置请求好友时发送的验证信息
                            presence.setStatus("您好，我是...");
                            connection.sendStanza(presence);

                            SubBean subBean = new SubBean();
                            subBean.setOwner(MyApp.username);
                            subBean.setFromUser(MyApp.username + "@" + MyConstance.SERVICE_HOST);
                            subBean.setToUser(friendJid);
                            subBean.setState("3");  // 3 表示发出好友申请
                            subBean.setDate(new Date().getTime());
                            subBean.setNick(nickName);
                            subBean.setAvatar(avatarUrl);
                            subBean.setMsg(presence.getStatus());
                            subBean.setMark(MyApp.username + "#" + friendName);

                            openIMDao.saveSingleSub(subBean);

                            finish();
                        } catch (NotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.ib_back || i == R.id.tv_back) {
            act.setResult(response, null);
            finish();
//                Intent mainIntent = new Intent(act, MainActivity.class);
//                if (type == 0) {
//                    mainIntent.putExtra("selection",3);
//                } else {
//                    mainIntent.putExtra("selection",2);
//                }
//                startActivity(mainIntent);

        } else if (i == R.id.btn_1) {
            if (type == 2) {
                /**
                 * 打开聊天界面
                 */
                Intent intent = new Intent(act, ChatActivity.class);
                intent.putExtra("friendName", friendName);
                intent.putExtra("friendNick", nickName);
                intent.putExtra("avatarUrl", avatarUrl);
                startActivity(intent);
                act.finish();
            } else if (type == 3) {
                try {
                    Presence response = new Presence(Presence.Type.subscribed);
                    response.setTo(friendJid);
                    connection.sendStanza(response);
                    Presence subscribe = new Presence(Presence.Type.subscribe);
                    subscribe.setTo(friendJid);
                    connection.sendStanza(subscribe);
                    openIMDao.updateSubByMark(MyApp.username + "#" + friendName, "1");
                    openIMDao.deleteSingleSub(MyApp.username + "#" + friendName);

                    roster.createEntry(friendJid, friendJid.substring(0, friendJid.indexOf("@")), null);
                    ThreadUtil.runOnBackThread(new Runnable() {
                        @Override
                        public void run() {
                            VCardBean vCardBean = MyVCardUtils.queryVCard(friendJid);
                            if (vCardBean != null) {
                                vCardBean.setJid(friendJid);
                                openIMDao.saveSingleVCard(vCardBean);
                            }
                        }
                    });
                    MyLog.showLog("同意");
                    finish();
                } catch (NotConnectedException | SmackException.NotLoggedInException | XMPPErrorException | NoResponseException e) {
                    e.printStackTrace();
                }

            } else if (type == 4) {  // 此处不显示
                MyUtils.showToast(act, "新朋友1");
            }

        } else if (i == R.id.btn_2) {
            if (type == 0) {
                pd.show();
                ThreadUtil.runOnBackThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (avatarPath != null) {
                                String resolution = bitmap.getWidth() + "*" + bitmap.getHeight();
                                String avatarResult = MyFileUtils.uploadAvatar(avatarPath, resolution);
                                if (avatarResult != null) {
                                    /**
                                     * 头像设置的是我上传的原图
                                     */
                                    avatarUrl = avatarResult.substring(0, avatarResult.indexOf("&oim="));
                                    vCard.setField("AVATAR_URL", avatarUrl);
                                    vCardBean.setAvatar(avatarUrl);
                                }
                            }
                            if (vCardManager != null && vCard != null) {
                                vCardManager.saveVCard(vCard);
                                openIMDao.saveSingleVCard(vCardBean);
                                MyApp.avatarUrl = avatarUrl;
                                handler.sendEmptyMessage(SAVE_SUCCESS);
                            }
                        } catch (NoResponseException | XMPPErrorException | NotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else if (type == 1) {
                /**
                 * 显示添加好友框
                 */
                showAddDialog();
            } else if (type == 2) {
                try {
                    // 删除好友
                    deleteFriend();
                    openIMDao.deleteSingleVCard(friendJid);
                    openIMDao.deleteMessageByMark(MyApp.username + "#" + friendName);
                    MyUtils.showToast(act, "删除好友成功");
                    finish();
                } catch (NotConnectedException e) {
                    MyUtils.showToast(act, "删除好友失败");
                    e.printStackTrace();
                }
            } else if (type == 3) {
                try {
                    Presence response = new Presence(Presence.Type.unsubscribed);
                    response.setTo(friendJid);
                    connection.sendStanza(response);
                    openIMDao.updateSubByMark(MyApp.username + "#" + friendName, "2");
                    openIMDao.deleteSingleSub(MyApp.username + "#" + friendName);
                    MyLog.showLog("拒绝");
                    finish();
                } catch (NotConnectedException e) {
                    e.printStackTrace();
                }
            } else if (type == 4) {
                MyUtils.showToast(act, "撤销好友申请");
            }

        } else if (i == R.id.iv_flush) {
            MyUtils.showToast(act, "刷新个人信息");
            if (connection != null && connection.isAuthenticated()) {
                if (friendJid != null) {
                    ThreadUtil.runOnBackThread(new Runnable() {
                        @Override
                        public void run() {
                            vCardBean = MyVCardUtils.queryVCard(friendJid);
                            if (vCardBean != null) {
                                vCardBean.setJid(friendJid);
                                if (type == 0 || type == 2) {  // 自己 和 通讯录好友保存VCard信息
                                    openIMDao.updateSingleVCard(vCardBean);
                                    if (type == 2 && vCardBean.getAvatar() != null) {
                                        openIMDao.updateMessageAvatar(MyApp.username + "#" + friendName, vCardBean.getAvatar());
                                    }
                                }
                                nickName = vCardBean.getNick();
                                homeAddress = vCardBean.getAddress();
                                email = vCardBean.getEmail();
                                phone = vCardBean.getPhone();
                                sex = vCardBean.getSex();
                                desc = vCardBean.getDesc();
                                bday = vCardBean.getBday();
                                avatarUrl = vCardBean.getAvatar();
                                handler.sendEmptyMessage(QUERY_SUCCESS);
                            }
                        }
                    });
                }
            }

        }
    }

    /**
     * 方法 发个包过去  删除好友
     *
     * @throws NotConnectedException
     */
    private void deleteFriend() throws NotConnectedException {
        RosterPacket packet = new RosterPacket();
        packet.setType(IQ.Type.set);
        RosterPacket.Item item = new RosterPacket.Item(friendJid, null);
        // Set the item type as REMOVE so that the server will delete the entry
        item.setItemType(RosterPacket.ItemType.remove);
        packet.addRosterItem(item);
        connection.sendStanza(packet);
    }

    private class ViewHolder {
        public TextView item;
        public TextView info;
        public ImageView icon;
        public CircularImage avatar;
        public ImageView back;
        public View bar;
    }

    private ArrayAdapter<String> mAdapter;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_SUCCESS:
                    switch (type) {
                        case 0:  //个人修改信息界面
                            btn1.setVisibility(View.GONE);
                            btn2.setVisibility(View.VISIBLE);
                            btn2.setText("保 存");
                            tvTitle.setText("我的信息");
                            btn2.setBackgroundResource(R.drawable.btn_login_selector);
                            tvBack.setText("自己");
                            break;
                        case 1:  // 陌生好友
                            btn1.setVisibility(View.GONE);
                            btn2.setVisibility(View.VISIBLE);
                            btn2.setText("添加新朋友");
                            tvTitle.setText(nickName);
                            btn2.setBackgroundResource(R.drawable.btn_login_selector);
                            tvBack.setText("新朋友");
                            break;
                        case 2:  // 通讯录进入
                            btn1.setVisibility(View.VISIBLE);
                            btn2.setVisibility(View.VISIBLE);
                            btn2.setText("删除朋友");
                            tvTitle.setText(nickName);
                            btn2.setBackgroundResource(R.drawable.btn_delete_selector);
                            tvBack.setText("朋友");
                            break;
                        case 3:  // 陌生人申请进入  未同意
                            btn1.setVisibility(View.VISIBLE);
                            btn2.setVisibility(View.VISIBLE);
                            btn1.setText("同意并添加为新朋友");
                            btn2.setText("拒绝请求");
                            tvTitle.setText(nickName);
                            btn2.setBackgroundResource(R.drawable.btn_delete_selector);
                            tvBack.setText("陌生人");
                            break;
                        case 4:  // 添加新好友进入 未同意
                            btn1.setVisibility(View.GONE);
                            btn2.setVisibility(View.VISIBLE);
                            btn2.setText("撤销添加新朋友申请");
                            tvTitle.setText(nickName);
                            btn2.setBackgroundResource(R.drawable.btn_delete_selector);
                            tvBack.setText("陌生人");
                            break;
                    }

                    // 为listView设置数据
                    mAdapter = new ArrayAdapter<String>(act, 0, items) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            ViewHolder vh;
                            if (convertView == null) {
                                convertView = View.inflate(act, R.layout.list_item_userinfo, null);
                                vh = new ViewHolder();
                                vh.item = (TextView) convertView.findViewById(R.id.tv_item);
                                vh.info = (TextView) convertView.findViewById(R.id.tv_info);
                                vh.avatar = (CircularImage) convertView.findViewById(R.id.iv_avatar);
                                vh.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                                vh.back = (ImageView) convertView.findViewById(R.id.iv_right_back);
                                vh.bar = convertView.findViewById(R.id.v_bar);
                                convertView.setTag(vh);
                            } else {
                                vh = (ViewHolder) convertView.getTag();
                            }
                            vh.item.setText(items[position]);
                            vh.icon.setImageResource(icons[position]);
                            if (position == 0) {
                                vh.avatar.setVisibility(View.VISIBLE);
                                vh.info.setVisibility(View.GONE);
                            } else {
                                vh.avatar.setVisibility(View.GONE);
                                vh.info.setVisibility(View.VISIBLE);
                            }
                            switch (position) {
                                case 0:
                                    if (avatarUrl != null) {
                                        vh.avatar.setTag(position);
                                        bitmapUtils.display(vh.avatar, avatarUrl);
                                    } else {
                                        vh.avatar.setImageResource(R.mipmap.ic_launcher);
                                    }
                                    vh.bar.setVisibility(View.GONE);
                                    vh.back.setVisibility(View.VISIBLE);
                                    break;
                                case 1:
                                    if (TextUtils.isEmpty(friendJid)) {
                                        vh.info.setText("未填写");
                                    } else {
                                        vh.info.setText(friendJid.substring(0, friendJid.indexOf("@")));
                                    }
                                    vh.bar.setVisibility(View.VISIBLE);
                                    vh.back.setVisibility(View.GONE);
                                    break;
                                case 2:
                                    if (TextUtils.isEmpty(nickName)) {
                                        vh.info.setText("未填写");
                                    } else {
                                        vh.info.setText(nickName);
                                    }
                                    vh.bar.setVisibility(View.GONE);
                                    vh.back.setVisibility(View.VISIBLE);
                                    break;
                                case 3:
                                    if (TextUtils.isEmpty(sex)) {
                                        vh.info.setText("未填写");
                                    } else {
                                        vh.info.setText(sex);
                                    }
                                    vh.bar.setVisibility(View.VISIBLE);
                                    vh.back.setVisibility(View.VISIBLE);
                                    break;
                                case 4:
                                    if (TextUtils.isEmpty(bday)) {
                                        vh.info.setText("未填写");
                                    } else {
                                        vh.info.setText(bday);
                                    }
                                    vh.bar.setVisibility(View.VISIBLE);
                                    vh.back.setVisibility(View.VISIBLE);
                                    break;
                                case 5:
                                    if (TextUtils.isEmpty(homeAddress)) {
                                        vh.info.setText("未填写");
                                    } else {
                                        vh.info.setText(homeAddress);
                                    }
                                    vh.bar.setVisibility(View.GONE);
                                    vh.back.setVisibility(View.VISIBLE);
                                    break;
                                case 6:
                                    if (TextUtils.isEmpty(email)) {
                                        vh.info.setText("未填写");
                                    } else {
                                        vh.info.setText(email);
                                    }
                                    vh.bar.setVisibility(View.VISIBLE);
                                    vh.back.setVisibility(View.VISIBLE);
                                    break;
                                case 7:
                                    if (TextUtils.isEmpty(phone)) {
                                        vh.info.setText("未填写");
                                    } else {
                                        vh.info.setText(phone);
                                    }
                                    vh.bar.setVisibility(View.GONE);
                                    vh.back.setVisibility(View.VISIBLE);
                                    break;
                                case 8:
                                    if (TextUtils.isEmpty(desc)) {
                                        vh.info.setText("未填写");
                                    } else {
                                        vh.info.setText(desc);
                                    }
                                    vh.bar.setVisibility(View.VISIBLE);
                                    vh.back.setVisibility(View.VISIBLE);
                                    break;
                            }
                            if (type != 0) {
                                if (position != 0) {
                                    vh.back.setVisibility(View.GONE);
                                }
                            }
                            return convertView;
                        }
                    };
                    mListView.setAdapter(mAdapter);
                    pdDismiss();
                    break;
                case SAVE_SUCCESS:
                    if (connection != null && connection.isAuthenticated()) {
                        queryVCard();
                        response = 201;
                    }
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        act.setResult(response, null);
        super.onBackPressed();
//        Intent intent = new Intent(act, MainActivity.class);
//        if (type == 0) {
//            intent.putExtra("selection",3);
//        } else {
//            intent.putExtra("selection",2);
//        }
//        startActivity(intent);
    }
}
