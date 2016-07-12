package com.open.im.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.open.im.R;
import com.open.im.db.OpenIMDao;
import com.open.im.utils.MyConstance;
import com.open.im.utils.MyFileUtils;
import com.open.im.utils.MyUtils;

import java.io.File;

/**
 * Created by Administrator on 2016/4/12.
 */
public class CleanCacheActivity extends BaseActivity implements View.OnClickListener {

    private CleanCacheActivity act;
    private ImageButton ibBack;
    private Button btnClean;
    private TextView tvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        act = this;
        initView();
        register();
    }

    private void register() {
        ibBack.setOnClickListener(this);
        btnClean.setOnClickListener(this);
        tvBack.setOnClickListener(this);
    }

    private void initView() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        btnClean = (Button) findViewById(R.id.btn_clean);
        tvBack = (TextView) findViewById(R.id.tv_back);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.ib_back || i == R.id.tv_back) {
            finish();

        } else if (i == R.id.btn_clean) {
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/exiu/";
            File file = new File(filePath);
            //删除缓存文件
            MyFileUtils.deleteFile(file);
            OpenIMDao openIMDao = OpenIMDao.getInstance(act);
            //删除缓存信息
            openIMDao.deleteAllMessage();
            //删除好友请求
            openIMDao.deleteAllSub();
            //清空VCard缓存
            openIMDao.deleteAllVCard();
            // 清空sp文件
            SharedPreferences sp = act.getSharedPreferences(MyConstance.SP_NAME, 0);
            sp.edit().clear().apply();
            MyUtils.showToast(act, "清空缓存成功");

        }
    }
}
