package com.open.im.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.open.im.R;
import com.open.im.utils.MyUtils;

public class UserSexUpdateActivity extends BaseActivity implements OnClickListener {

    TextView tvCancel;
    TextView tvSave;
    RadioGroup rgSex;
    TextView tvTitle;

    private UserSexUpdateActivity act;
    private Intent intent;
    private String sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usersex_update);

        initView();

        initData();

        register();
    }

    private void register() {
        tvCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        rgSex.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_male) {
                    sex = "男";

                } else if (checkedId == R.id.rb_female) {
                    sex = "女";

                } else if (checkedId == R.id.rb_secret) {
                    sex = "保密";

                }
            }
        });
    }

    private void initData() {
        intent = getIntent();
        sex = intent.getStringExtra("sex");
        MyUtils.showToast(act, sex);
        if ("男".equals(sex)) {
            rgSex.check(R.id.rb_male);
        } else if ("女".equals(sex)) {
            rgSex.check(R.id.rb_female);
        } else if ("保密".equals(sex)) {
            rgSex.check(R.id.rb_secret);
        } else {
            rgSex.clearCheck();
        }
    }

    private void initView() {

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvSave = (TextView) findViewById(R.id.tv_save);

        act = this;
        tvTitle.setText("性别");
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_cancel) {
            finish();

        } else if (i == R.id.tv_save) {
            if (sex != null) {
                intent.setData(Uri.parse(sex));
                setResult(3, intent);
                finish();
            }

        }
    }
}
