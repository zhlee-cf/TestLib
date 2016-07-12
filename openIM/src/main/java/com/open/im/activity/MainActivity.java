package com.open.im.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.view.PagerAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.open.im.R;
import com.open.im.app.MyApp;
import com.open.im.db.OpenIMDao;
import com.open.im.pager.BasePager;
import com.open.im.pager.ContactPager;
import com.open.im.pager.NewsPager;
import com.open.im.pager.SelfPager;
import com.open.im.service.IMService;
import com.open.im.utils.MyAnimationUtils;
import com.open.im.utils.MyLog;
import com.open.im.utils.MyNetUtils;
import com.open.im.utils.MyUtils;
import com.open.im.view.MyViewPager;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements OnClickListener {

    TextView tvTitle;
    ImageView ivLoading;
    RelativeLayout rlState;
    ImageView ivMinus;
    ImageView ivAdd;
    LinearLayout llNet;
    MyViewPager viewPager;
    ImageButton ibNews;
    ImageButton ibContact;
    ImageButton ibSetting;


    private static final int CONNECTING = 100;
    private static final int CONNECTION_SUCCESS = 101;
    private MainActivity act;
    private List<BasePager> pagers;
    private int lastPosition = 0;
    private BroadcastReceiver netReceiver;
    private ConnectionListener connectionListener;
    private XMPPTCPConnection connection;
    private AnimationDrawable an;
    private OpenIMDao openIMDao;
    private BroadcastReceiver mHomeKeyDownReceiver;
    private PowerManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        act = this;

        initView();

        initData();

        register();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 201:  // 个人信息界面有修改返回
                if (lastPosition == 3) {
                    SelfPager selfPager = (SelfPager) pagers.get(2);
                    selfPager.queryInfo();
                    MyLog.showLog("个人信息修改");
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (lastPosition == 1) {
            ContactPager contactPager = (ContactPager) pagers.get(1);
            contactPager.queryFriends();
        }
    }

    /**
     * 注册点击监听
     */
    private void register() {

        ibNews.setOnClickListener(this);
        ibContact.setOnClickListener(this);
        ibSetting.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        ivMinus.setOnClickListener(this);

        /**
         * 注册网络连接监听
         */
        netReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                    boolean isConnected = MyNetUtils.isNetworkConnected(context);
                    if (isConnected) {
                        llNet.setVisibility(View.GONE);
                        tvTitle.setVisibility(View.VISIBLE);
                        rlState.setVisibility(View.GONE);
                        an.stop();
                    } else {
                        llNet.setVisibility(View.VISIBLE);
                        tvTitle.setVisibility(View.GONE);
                        rlState.setVisibility(View.VISIBLE);
                        an.start();
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netReceiver, filter);
        //  连接状态监听
        if (connection != null) {
            connectionListener = new ConnectionListener() {
                @Override
                public void connected(XMPPConnection connection) {

                }

                @Override
                public void authenticated(XMPPConnection connection, boolean resumed) {

                }

                @Override
                public void connectionClosed() {

                }

                @Override
                public void connectionClosedOnError(Exception e) {
                    MyLog.showLog("主界面异常掉线");
                }

                @Override
                public void reconnectionSuccessful() {
                    handler.sendEmptyMessage(CONNECTION_SUCCESS);
                    MyLog.showLog("主界面连接成功");
                }

                @Override
                public void reconnectingIn(int seconds) {
                    if (connection != null && !connection.isConnected()) {
                        handler.sendEmptyMessage(CONNECTING);
                    }
                }

                @Override
                public void reconnectionFailed(Exception e) {

                }
            };
            connection.addConnectionListener(connectionListener);
        }

//        registerHomeKeyDownListener();
    }

    /**
     * 初始化数据
     */
    private void initData() {

        connection = MyApp.connection;

        openIMDao = OpenIMDao.getInstance(act);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        pagers = new ArrayList<>();
        pagers.add(new NewsPager(act));
        pagers.add(new ContactPager(act));
        pagers.add(new SelfPager(act));

        MyAdapter adapter = new MyAdapter();

        viewPager.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null) {
            int selection = intent.getIntExtra("selection", 0);
            if (selection == 3) {
                ibSetting.setEnabled(false);
                tvTitle.setText("自己");
                ivAdd.setVisibility(View.GONE);
                ivMinus.setVisibility(View.GONE);
                viewPager.setCurrentItem(3);
                lastPosition = 3;
            } else if (selection == 2) {
                ibContact.setEnabled(false);
                tvTitle.setText("朋友");
                ivAdd.setVisibility(View.VISIBLE);
                ivMinus.setVisibility(View.GONE);
                viewPager.setCurrentItem(1);
                lastPosition = 2;
            } else {
                ibNews.setEnabled(false);
                tvTitle.setText("聊天");
                ivAdd.setVisibility(View.GONE);
                ivMinus.setVisibility(View.VISIBLE);
                // 默认显示消息列表页面
                viewPager.setCurrentItem(0);
                lastPosition = 0;
            }
        } else {
            MyUtils.showToast(act, "intent为null");
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {

        viewPager = (MyViewPager) findViewById(R.id.viewPager);
        ibNews = (ImageButton) findViewById(R.id.ib_news);
        ibContact = (ImageButton) findViewById(R.id.ib_contact);
        ibSetting = (ImageButton) findViewById(R.id.ib_setting);
        ivLoading = (ImageView) findViewById(R.id.iv_loading);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        rlState = (RelativeLayout) findViewById(R.id.rl_state);
        ivAdd = (ImageView) findViewById(R.id.iv_add);
        ivMinus = (ImageView) findViewById(R.id.iv_minus);

        llNet = (LinearLayout) findViewById(R.id.ll_net);



        an = (AnimationDrawable) ivLoading.getDrawable();
        llNet.setVisibility(View.GONE);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_news:
                if (0 != lastPosition) {
                    showPager(0, false, true, true);
                    ivAdd.setVisibility(View.GONE);
                    ivMinus.setVisibility(View.VISIBLE);
                    if (MyNetUtils.isNetworkConnected(act)) {
                        rlState.setVisibility(View.GONE);
                        tvTitle.setVisibility(View.VISIBLE);
                        tvTitle.setText("聊天");
                    }
                }
                break;
            case R.id.ib_contact:
                if (1 != lastPosition) {
                    showPager(1, true, false, true);
                    ivAdd.setVisibility(View.VISIBLE);
                    ivMinus.setVisibility(View.GONE);
                    if (MyNetUtils.isNetworkConnected(act)) {
                        rlState.setVisibility(View.GONE);
                        tvTitle.setVisibility(View.VISIBLE);
                        tvTitle.setText("朋友");
                    }
                }
                break;
            case R.id.ib_setting:
                if (3 != lastPosition) {
                    showPager(3, true, true, false);
                    ivAdd.setVisibility(View.GONE);
                    ivMinus.setVisibility(View.GONE);
                    if (MyNetUtils.isNetworkConnected(act)) {
                        rlState.setVisibility(View.GONE);
                        tvTitle.setVisibility(View.VISIBLE);
                        tvTitle.setText("自己");
                    }
                }
                break;
            case R.id.iv_add:
                act.startActivity(new Intent(act, AddFriendActivity.class));
                break;
            case R.id.iv_minus:
                MyAnimationUtils.rotate(ivMinus);
                openIMDao.deleteMessageByOwner(MyApp.username);
                break;
        }
    }

    /**
     * viewPager设置的adapter 填充四个自定义pager
     *
     * @author Administrator
     */
    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pagers.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View view = pagers.get(position).initView();
            pagers.get(position).initData();
            container.addView(view);

            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // super.destroyItem(container, position, object);
            container.removeView((View) object);
        }

    }

    /**
     * 根据点击位置 设置显示的pager
     *
     * @param item
     * @param b1
     * @param b2
     * @param b3
     */
    private void showPager(int item, boolean b1, boolean b2, boolean b3) {
        viewPager.setCurrentItem(item);
        lastPosition = item;
        ibNews.setEnabled(b1);
        ibContact.setEnabled(b2);
        ibSetting.setEnabled(b3);
    }

    @Override
    protected void onDestroy() {
        if (netReceiver != null) {
            unregisterReceiver(netReceiver);
        }

        if (mHomeKeyDownReceiver != null) {
            unregisterReceiver(mHomeKeyDownReceiver);
        }

        if (connectionListener != null && connection != null) {
            connection.removeConnectionListener(connectionListener);
        }
        super.onDestroy();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONNECTING:  //正在连接
                    tvTitle.setVisibility(View.GONE);
                    rlState.setVisibility(View.VISIBLE);
                    an.start();
                    break;
                case CONNECTION_SUCCESS:  //连接成功
                    tvTitle.setVisibility(View.VISIBLE);
                    rlState.setVisibility(View.GONE);
                    an.stop();
                    break;
            }
        }
    };

    /**
     * 监听的是 系统发出的取消Dialog的广播  反正点击home键会发出 当应用在前台时，锁屏键也会发出
     * 勉强可以用来处理home键点击事件
     */
    private void registerHomeKeyDownListener() {

        mHomeKeyDownReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MyLog.showLog("收到广播");
                if (pm.isScreenOn()) {
                    finish();
                }
            }
        };
        registerReceiver(mHomeKeyDownReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    /**
     * 短时间双击返回键退出
     */
    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {  //如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {  //两次按键小于2秒时，退出应用
                    IMService.getInstance().stopSelf();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

}
