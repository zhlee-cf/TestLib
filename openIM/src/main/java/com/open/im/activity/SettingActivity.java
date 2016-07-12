package com.open.im.activity;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open.im.R;
import com.open.im.app.MyApp;
import com.open.im.service.IMService;
import com.open.im.utils.MyConstance;

/**
 * 个人设置界面
 * Created by Administrator on 2016/4/8.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private SettingActivity act;
    private NotificationManager notificationManager;
    private SharedPreferences sp;
    private RelativeLayout rlChangePwd;
    private RelativeLayout rlClean;
    private Button btnLogout;
    private TextView tvBack;
    private ImageButton ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        act = this;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sp = getSharedPreferences(MyConstance.SP_NAME, 0);
        register();
    }

    private void register() {
        rlChangePwd.setOnClickListener(this);
        rlClean.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        ibBack.setOnClickListener(this);
        tvBack.setOnClickListener(this);
    }

    private void initView() {
        rlChangePwd = (RelativeLayout) findViewById(R.id.rl_change_pwd);
        rlClean = (RelativeLayout) findViewById(R.id.rl_clean);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        tvBack = (TextView) findViewById(R.id.tv_back);
        ibBack = (ImageButton) findViewById(R.id.ib_back);
    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(act, MainActivity.class);
//        intent.putExtra("selection",3);
//        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.rl_change_pwd) {
            act.startActivity(new Intent(act, UpdatePasswordActivity.class));

        } else if (i == R.id.rl_clean) {
            act.startActivity(new Intent(act, CleanCacheActivity.class));

        } else if (i == R.id.btn_logout) {// 注销登录时，退出应用，关闭服务
            IMService.getInstance().stopSelf();
            notificationManager.cancel(MyConstance.NOTIFY_ID_MSG);
            notificationManager.cancel(MyConstance.NOTIFY_ID_SUB);
            // 注销时清空密码
            sp.edit().putString("password", "").apply();
            // 删除RosterVersion
            sp.edit().putString(MyConstance.ROSTER_VER, "").apply();
            // 跳转到重新登录界面
            Intent loginIntent = new Intent(act, ReLoginActivity.class);
            act.startActivity(loginIntent);
            act.finish();
            MyApp.clearActivity();
            MyApp.connection = null;

        } else if (i == R.id.tv_back || i == R.id.ib_back) {//                Intent intent = new Intent(act, MainActivity.class);
//                intent.putExtra("selection",3);
//                startActivity(intent);
            finish();

        }
    }
}
