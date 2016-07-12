package com.open.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.open.im.R;
import com.open.im.utils.MyConstance;

public class RegisterSuccessActivity extends Activity {

    private Button btn_login;
    private RegisterSuccessActivity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_result);
        // 初始化
        init();
        // 注册监听
        register();
    }

    /**
     * 注册监听
     */
    private void register() {
        btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act,SplashActivity.class));
                finish();
            }
        });
    }

    /**
     * 初始化
     */
    private void init() {
        act = this;
        btn_login = (Button) findViewById(R.id.btn_login);
        TextView tv_username = (TextView) findViewById(R.id.tv_username);
        SharedPreferences sp = getSharedPreferences(MyConstance.SP_NAME, 0);
        String username = sp.getString("username", null);
        tv_username.setText(username);
    }
}
