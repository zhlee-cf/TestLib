package com.open.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.open.im.R;
import com.open.im.utils.MyBitmapUtils;
import com.open.im.view.ZoomImageView;

/**
 * 聊天详情展示大图片
 * Created by lzh12 on 2016/6/3.
 */
public class ShowImageActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        initView();
    }

    private void initView() {
        ZoomImageView imgView = (ZoomImageView)findViewById(R.id.iv_image);
        MyBitmapUtils mBitmapUtils = new MyBitmapUtils(this);
        Intent intent = getIntent();
        String picPath = intent.getStringExtra("picPath");
        if (TextUtils.isEmpty(picPath)){
            finish();
        } else {
            imgView.setTag(-1);
            mBitmapUtils.display(imgView,picPath);
        }
    }
}
