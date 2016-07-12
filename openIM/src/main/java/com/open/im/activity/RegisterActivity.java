package com.open.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.open.im.R;
import com.open.im.app.MyApp;
import com.open.im.utils.MyBase64Utils;
import com.open.im.utils.MyConstance;
import com.open.im.utils.MyUtils;
import com.open.im.utils.ThreadUtil;
import com.open.im.utils.XMPPConnectionUtils;
import com.open.im.view.MyDialog;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;

public class RegisterActivity extends Activity implements View.OnClickListener {

    EditText etUsername;
    EditText etPwd;

    private RegisterActivity act;
    protected MyDialog pd;
    private static final int REGISTER_SUCCESS = 101;
    private static final int REGISTER_FAIL = 102;
    private SharedPreferences sp;
    private Button btnRegister;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_userinfo);
        initView();
        init();
        register();
    }

    private void register() {
        btnRegister.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void initView() {
        etUsername = (EditText) findViewById(R.id.et_username);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
    }

    /**
     * 方法 注册用户
     *
     * @param username
     * @param password
     */
    private void registerUser(final String username, final String password, final String nickname) {
        ThreadUtil.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                try {
                    XMPPConnectionUtils.initXMPPConnection(act);
                    XMPPTCPConnection connection = MyApp.connection;
                    if (!connection.isConnected()) {
                        connection.connect();
                    }
                    // 获得账户管理者
                    AccountManager accountManager = AccountManager.getInstance(connection);

                    if (accountManager.supportsAccountCreation()) {
                        accountManager.sensitiveOperationOverInsecureConnection(true);
                        accountManager.createAccount(username, password);
                        sp.edit().putString("username", username).apply();
                        sp.edit().putString("password", MyBase64Utils.encodeToString(password)).apply();
                        sp.edit().putString("nickname", nickname).apply();
                        handler.sendEmptyMessage(REGISTER_SUCCESS);

                        // 注册成功后跳转到注册成功界面
                        Intent intent = new Intent(act, RegisterSuccessActivity.class);
                        act.startActivity(intent);
                        finish();
                    }
                } catch (IOException | SmackException e) {
                    handler.sendEmptyMessage(REGISTER_FAIL);
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    if (e.getXMPPError().toString().contains(XMPPError.Condition.conflict.toString())) {
                        pdDismiss();
                        MyUtils.showToast(act, "用户已存在");
                    } else {
                        handler.sendEmptyMessage(REGISTER_FAIL);
                    }
                    // XMPPError: conflict - cancel
                    // conflict
                    e.printStackTrace();
                } catch (XMPPException e) {
                    handler.sendEmptyMessage(REGISTER_FAIL);
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

    /**
     * 初始化
     */
    private void init() {
        act = this;
        sp = getSharedPreferences(MyConstance.SP_NAME, 0);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            pdDismiss();
            switch (msg.what) {
                case REGISTER_SUCCESS:
                    MyUtils.showToast(act, "注册成功");
                    break;
                case REGISTER_FAIL:
                    MyUtils.showToast(act, "注册失败");
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_register) {
            final String username = etUsername.getText().toString().trim();
            final String password = etPwd.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                MyUtils.showToast(act, "用户名不能为空");
                return;
            } else if (TextUtils.isEmpty(password)) {
                MyUtils.showToast(act, "密码不能为空");
                return;
            }
//                  else if (etPwd.length() < 6) {
//                    MyUtils.showToast(act, "密码长度不能小于6");
//                    return;
//                }

            pd = new MyDialog(act);
            pd.show();

            // 注册用户 默认使用用户名作为昵称
            registerUser(username, password, username);

        } else if (i == R.id.btn_cancel) {
            finish();

        }
    }
}
