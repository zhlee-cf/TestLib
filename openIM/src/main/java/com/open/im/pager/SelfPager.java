package com.open.im.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open.im.R;
import com.open.im.activity.ClientActivity;
import com.open.im.activity.MainActivity;
import com.open.im.activity.SettingActivity;
import com.open.im.activity.UserInfoActivity;
import com.open.im.app.MyApp;
import com.open.im.bean.VCardBean;
import com.open.im.db.OpenIMDao;
import com.open.im.utils.MyBitmapUtils;
import com.open.im.utils.MyConstance;
import com.open.im.utils.MyLog;
import com.open.im.utils.MyVCardUtils;
import com.open.im.utils.ThreadUtil;
import com.open.im.view.CircularImage;

public class SelfPager extends BasePager implements View.OnClickListener {

    private static final int QUERY_SUCCESS = 100;
    private static final int REQUEST_INFO = 201;
    private TextView tv_username, tv_desc;
    private CircularImage iv_avatar;
    private VCardBean vCardBean;
    private MyBitmapUtils bitmapUtils;
    private RelativeLayout rl_setting;
    private RelativeLayout rl_client;
    private RelativeLayout rl_self;
    private MainActivity act;
    private OpenIMDao openIMDao;

    public SelfPager(Context ctx) {
        super(ctx);
        act = (MainActivity) ctx;
    }

    @Override
    public View initView() {
        View view = View.inflate(ctx, R.layout.pager_im_self, null);
        tv_username = (TextView) view.findViewById(R.id.tv_username);
        tv_desc = (TextView) view.findViewById(R.id.tv_desc);
        iv_avatar = (CircularImage) view.findViewById(R.id.iv_avatar);
        rl_setting = (RelativeLayout) view.findViewById(R.id.rl_setting);
        rl_client = (RelativeLayout) view.findViewById(R.id.rl_client);
        rl_self = (RelativeLayout) view.findViewById(R.id.rl_self);
        return view;
    }

    @Override
    public void initData() {
        bitmapUtils = new MyBitmapUtils(ctx);
        openIMDao = OpenIMDao.getInstance(ctx);
        queryInfo();
        register();
    }

    public void queryInfo() {
        ThreadUtil.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                String userJid = MyApp.username + "@" + MyConstance.SERVICE_HOST;
                if (openIMDao == null) {
                    openIMDao = OpenIMDao.getInstance(act);
                }
                vCardBean = openIMDao.findSingleVCard(userJid);
                if (vCardBean == null) {
                    vCardBean = MyVCardUtils.queryVCard(userJid);
                    if (vCardBean != null) {
                        vCardBean.setJid(userJid);
                        openIMDao.saveSingleVCard(vCardBean);
                    }
                }
                handler.sendEmptyMessage(QUERY_SUCCESS);
            }
        });
    }

    /**
     * 注册条目点击事件
     */
    private void register() {
        rl_self.setOnClickListener(this);
        rl_setting.setOnClickListener(this);
        rl_client.setOnClickListener(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case QUERY_SUCCESS:
                    MyLog.showLog("vcardBean::" + vCardBean);
                    if (vCardBean.getNick() != null) {
                        tv_username.setText(vCardBean.getNick());
                    } else {
                        tv_username.setText(MyApp.username);
                    }
                    String avatarUrl = vCardBean.getAvatar();
                    if (avatarUrl != null) {
                        iv_avatar.setTag(0);
                        bitmapUtils.display(iv_avatar, avatarUrl);
                    } else {
                        iv_avatar.setImageResource(R.mipmap.ic_launcher);
                    }
                    tv_desc.setText(vCardBean.getDesc());
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_self:
                act.startActivityForResult(new Intent(act, UserInfoActivity.class), REQUEST_INFO);
                break;
            case R.id.rl_setting:
                act.startActivity(new Intent(act, SettingActivity.class));
                break;
            case R.id.rl_client:
                act.startActivity(new Intent(act, ClientActivity.class));
                break;
        }
    }
}
