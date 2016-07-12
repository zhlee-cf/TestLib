package com.open.im.activity;

import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.open.im.R;
import com.open.im.utils.MyConstance;
import com.open.im.utils.MyUtils;
import com.open.im.utils.QRCodeUtil;
import com.open.im.utils.ThreadUtil;

import java.io.File;


/**
 * 关于客户端界面
 * Created by Administrator on 2016/4/13.
 */
public class ClientActivity extends BaseActivity implements View.OnClickListener {

    TextView tvVersion;
    ImageView ivQrcode;
    private ClientActivity act;
    private String filePath;
    private ImageButton ibBack;
    private TextView tvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        act = this;
        initView();
        register();
    }

    private void register() {
        ivQrcode.setOnClickListener(this);
        ibBack.setOnClickListener(this);
        tvBack.setOnClickListener(this);
    }

    private void initView() {
        tvVersion = (TextView) findViewById(R.id.tv_version);
        ivQrcode = (ImageView) findViewById(R.id.iv_qrcode);
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        tvBack = (TextView) findViewById(R.id.tv_back);
        /**
         * 获得包管理器，手机中所有应用，共用一个包管理器
         */
        PackageManager packageManager = act.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(act.getPackageName(), 0);
            String versionNameStr = packageInfo.versionName;
            tvVersion.setText("OpenIM " + versionNameStr);
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/exiu/OpenIM_" + versionNameStr + ".jpg";
            MyUtils.showToast(act, "当前版本号:" + versionNameStr);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                ivQrcode.setImageBitmap(BitmapFactory.decodeFile(filePath));
            } else {
                //如果二维码不存在 则创建 存在则直接显示
                createQRCode();
            }
        }
    }

    /**
     * 根据链接创建二维码并设置显示
     */
    public void createQRCode() {
        //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
        ThreadUtil.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                boolean success = QRCodeUtil.createQRImage(MyConstance.CLIENT_URL, 600, 600,
                        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),
                        filePath);

                if (success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivQrcode.setImageBitmap(BitmapFactory.decodeFile(filePath));
                        }
                    });
                }
            }
        });
    }

    /**
     * 方法 点击二维码 单独显示二维码
     */
    private void showImgDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(act, R.style.Lam_Dialog_FullScreen).create();
        Window win = dialog.getWindow();
        win.setGravity(Gravity.FILL);
        // 隐藏手机最上面的状态栏
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.show();

        View view = View.inflate(act, R.layout.dialog_qrcode, null);
        ImageView imgView = (ImageView) view.findViewById(R.id.iv_qrcode);

        imgView.setImageBitmap(BitmapFactory.decodeFile(filePath));

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        win.setContentView(view);
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
        if (i == R.id.iv_qrcode) {
            showImgDialog();

        } else if (i == R.id.ib_back || i == R.id.tv_back) {//                Intent intent = new Intent(act, MainActivity.class);
//                intent.putExtra("selection",3);
//                startActivity(intent);
            finish();

        }
    }
}
