package com.open.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.open.im.R;
import com.open.im.app.MyApp;
import com.open.im.service.IMService;
import com.open.im.utils.MyBase64Utils;
import com.open.im.utils.MyConstance;
import com.open.im.utils.MyLog;
import com.open.im.utils.MyUtils;
import com.open.im.utils.ThreadUtil;
import com.open.im.utils.XMPPConnectionUtils;
import com.open.im.view.ClearEditText;
import com.open.im.view.MyDialog;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;

public class LoginActivity extends Activity implements OnClickListener {

    ClearEditText etUsername;
    ClearEditText etPwd;
    TextView tvRegister;

    private LoginActivity act;
    private MyDialog pd;
    private int beforeLength;

    /**
     * 登录状态
     */
    private static final int LOGIN_SUCCESS = 0;
    private static final int FAIL_PASSWORD_ERROR = 1;
    private static final int FAIL_UNKNOWN_USER = 2;
    private static final int INTERNET_ERROR = 3;
    private static final int LOGIN_FAIL = 4;

    private SharedPreferences sp;
    private Intent service;
    private XMPPTCPConnection connection;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        sp = getSharedPreferences(MyConstance.SP_NAME, 0);
        boolean isServiceRunning = MyUtils.isServiceRunning(act, IMService.class.getName());
        if (isServiceRunning) {
            startActivity(new Intent(act, MainActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_login);
            initView();

            btnLogin.setOnClickListener(this);
            tvRegister.setOnClickListener(this);

            etUsername.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    beforeLength = s.length();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    int afterLength = s.length();
                    if (afterLength < beforeLength) {
                        etPwd.setText("");
                    }
                }
            });
        }
    }

    /**
     * 界面初始化
     */
    private void initView() {

        etUsername = (ClearEditText) findViewById(R.id.et_username);
        etPwd = (ClearEditText) findViewById(R.id.et_pwd);
        btnLogin = (Button) findViewById(R.id.btn_login);
        tvRegister = (TextView) findViewById(R.id.tv_register);

        TextView tv_version = (TextView) findViewById(R.id.tv_version);

        TextPaint paint = tvRegister.getPaint();
        //加下划线
        paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
        //设置字体为粗体
        paint.setFakeBoldText(true);

        /**
         * 获得包管理器，手机中所有应用，共用一个包管理器
         */
        PackageManager packageManager = act.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(act.getPackageName(), 0);
            String versionNameStr = packageInfo.versionName;
            tv_version.setText("OpenIM " + versionNameStr);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //加下划线另一种方式
//		SpannableString content = new SpannableString("注册新用户");
//		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
//		tvRegister.setText(content);

//		String userName = sp.getString("username", "");
//		String password = sp.getString("password", "");

//		etUsername.setText(userName);
//		etPwd.setText(password);

    }

    /**
     * 方法 登录
     */
    private void login(final String username, final String password) {
        XMPPConnectionUtils.initXMPPConnection(act);
        connection = MyApp.connection;
        pd = new MyDialog(act);
        pd.show();
        ThreadUtil.runOnBackThread(new Runnable() {

            @Override
            public void run() {
                MyLog.showLog(Thread.currentThread().getName() + "正在执行。。。");
                try {
                    if (!connection.isConnected()) {
                        connection.connect();
                    }
                    connection.setPacketReplyTimeout(60 * 1000);
                    connection.login(username, password);

                    sp.edit().putString("username", username).apply();
                    sp.edit().putString("password", MyBase64Utils.encodeToString(password)).apply();

                    MyApp.username = username;

                    service = new Intent(act, IMService.class);
                    act.startService(service);

                    Intent intent = new Intent(act, MainActivity.class);
                    act.startActivity(intent);
                    finish();

                    handler.sendEmptyMessage(LOGIN_SUCCESS);

                } catch (SmackException e) {
                    handler.sendEmptyMessage(INTERNET_ERROR);
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    if (e.getMessage().contains("not-authorized")) {
                        // 未注册异常
                        handler.sendEmptyMessage(FAIL_UNKNOWN_USER);
                    } else if (e.getMessage().contains("bad-auth")) {
                        // 密码错误异常
                        handler.sendEmptyMessage(FAIL_PASSWORD_ERROR);
                    } else {
                        handler.sendEmptyMessage(LOGIN_FAIL);
                    }
                    e.printStackTrace();
                }
            }
        });
    }

    private void pdDismiss() {
        if (pd != null && pd.isShowing() && act != null) {
            pd.dismiss();
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            pdDismiss();
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    MyUtils.showToast(act, "登录成功");
                    break;
                case INTERNET_ERROR:
                    MyUtils.showToast(act, "网络错误，请检查您的网络");
                    break;
                case FAIL_UNKNOWN_USER:
                    MyUtils.showToast(act, "用户未注册");
                    break;
                case FAIL_PASSWORD_ERROR:
                    MyUtils.showToast(act, "密码错误");
                    sp.edit().putString("password", "").apply();
                    break;
                case LOGIN_FAIL:
                    MyUtils.showToast(act, "登录失败");
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_login) {
            final String username = etUsername.getText().toString().trim();
            final String password = etPwd.getText().toString().trim();
            if (TextUtils.isEmpty(username)) {
                MyUtils.showToast(act, "用户名不能为空");
//				etUsername.setError("用户名不能为空");
                return;
            } else if (TextUtils.isEmpty(password)) {
                MyUtils.showToast(act, "密码不能为空");
//				etPwd.setError("密码不能为空");
                return;
            }

            login(username, password);


            /**
             * 注册
             */
        } else if (i == R.id.tv_register) {
            Intent intent = new Intent(act, RegisterActivity.class);
            act.startActivity(intent);
            finish();

        }
    }
}
