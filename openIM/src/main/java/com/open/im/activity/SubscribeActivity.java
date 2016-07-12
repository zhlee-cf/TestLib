package com.open.im.activity;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.open.im.R;
import com.open.im.app.MyApp;
import com.open.im.bean.SubBean;
import com.open.im.db.OpenIMDao;
import com.open.im.utils.MyAnimationUtils;
import com.open.im.utils.MyBitmapUtils;
import com.open.im.utils.MyConstance;
import com.open.im.utils.ThreadUtil;
import com.open.im.view.CircularImage;

import java.util.List;

/**
 * 好友申请列表页面
 * Created by Administrator on 2016/3/24.
 */
public class SubscribeActivity extends BaseActivity implements View.OnClickListener {
    ImageView ivMinus;
    ListView lvSubscribe;

    private SubscribeActivity act;
    private List<SubBean> subBeans;
    private final int QUERY_SUCCESS = 100;
    private MyAdapter adapter;
    private MyBitmapUtils bitmapUtils;
    private OpenIMDao openIMDao;
    private ImageButton ibBack;
    private TextView tvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_msg);
        act = this;
        initView();

        initData();

        register();
    }

    private void register() {
        ibBack.setOnClickListener(this);
        ivMinus.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        lvSubscribe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubBean bean = (SubBean) adapter.getItem(position);
                String type = bean.getState();
                Intent intent = new Intent(act, UserInfoActivity.class);
                if ("0".equals(type)) {   //0 表示收到请求
                    intent.putExtra("type", 3);
                    intent.putExtra("friendJid", bean.getFromUser());
                } else if ("3".equals(type)) {  // 3表示发出的请求
                    intent.putExtra("type", 4);
                    intent.putExtra("friendJid", bean.getToUser());
                } else if ("1".equals(type)) {  // 1表示 同意添加对方为好友
                    intent.putExtra("type", 2);
                    intent.putExtra("friendJid", bean.getFromUser());
                } else if ("4".equals(type)) {   // 4表示 对方同意添加自己为好友
                    intent.putExtra("type", 2);
                    intent.putExtra("friendJid", bean.getToUser());
                } else if ("2".equals(type)) {
                    intent.putExtra("type", 1); //  2表示已拒绝对方请求
                    intent.putExtra("friendJid", bean.getFromUser());
                } else if ("5".equals(type)) {  // 5表示对方已拒绝
                    intent.putExtra("type", 1);
                    intent.putExtra("friendJid", bean.getToUser());
                }
                act.startActivity(intent);
                finish();
            }
        });
    }

    private void initData() {
        openIMDao = OpenIMDao.getInstance(act);
        bitmapUtils = new MyBitmapUtils(act);
        ThreadUtil.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                subBeans = openIMDao.findSubByOwner(MyApp.username, 15, 0);
                handler.sendEmptyMessage(QUERY_SUCCESS);
            }
        });
    }

    private void initView() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        lvSubscribe = (ListView) findViewById(R.id.lv_subscribe);
        ivMinus = (ImageView) findViewById(R.id.iv_minus);
        tvBack = (TextView) findViewById(R.id.tv_back);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(MyConstance.NOTIFY_ID_SUB);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.ib_back || i == R.id.tv_back) {//                Intent intent = new Intent(act, MainActivity.class);
//                intent.putExtra("selection",2);
//                startActivity(intent);
            finish();

        } else if (i == R.id.iv_minus) {// 旋转180度 不保存状态 补间动画
            MyAnimationUtils.rotate(ivMinus);
            subBeans.clear();
            openIMDao.deleteSubByOwner(MyApp.username);
            adapter.notifyDataSetChanged();

        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return subBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return subBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder vh;
            if (convertView == null) {
                convertView = View.inflate(act, R.layout.list_item_sub, null);
                vh = new ViewHolder();
                vh.ivAvatar = (CircularImage) convertView.findViewById(R.id.iv_avatar);
                vh.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                vh.tvState = (TextView) convertView.findViewById(R.id.tv_state);
                vh.ivAvatar.setTag(position);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            SubBean subBean = subBeans.get(position);
            vh.tvName.setText(subBean.getNick());
            if (subBean.getAvatar() != null) {
                bitmapUtils.display(vh.ivAvatar, subBean.getAvatar());
            } else {
                vh.ivAvatar.setImageResource(R.mipmap.ic_launcher);
            }
            String state = subBean.getState();
            if ("0".equals(state)) {  //0 表示收到请求
                vh.tvState.setText("[陌生人请求]");
                vh.tvState.setTextColor(Color.RED);
            } else if ("1".equals(state)) {  // 1 表示已同意对方申请
                vh.tvState.setText("[已同意对方申请]");
                vh.tvState.setTextColor(Color.GREEN);
            } else if ("2".equals(state)) {  // 2 表示拒绝对方申请
                vh.tvState.setText("[已拒绝对方申请]");
                vh.tvState.setTextColor(Color.RED);
            } else if ("3".equals(state)) {  // 3表示添加新朋友
                vh.tvState.setText("[添加新朋友]");
                vh.tvState.setTextColor(Color.GREEN);
            } else if ("4".equals(state)) {  // 4表示对方已同意申请
                vh.tvState.setText("[对方已同意申请]");
                vh.tvState.setTextColor(Color.GREEN);
            } else if ("5".equals(state)) {  // 5表示对方已拒绝申请
                vh.tvState.setText("[对方已拒绝申请]");
                vh.tvState.setTextColor(Color.RED);
            }
            return convertView;
        }
    }

    class ViewHolder {
        CircularImage ivAvatar;
        TextView tvName;
        TextView tvState;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case QUERY_SUCCESS:
                    if (adapter == null) {
                        adapter = new MyAdapter();
                    }
                    lvSubscribe.setAdapter(adapter);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(act, MainActivity.class);
//        intent.putExtra("selection",2);
//        startActivity(intent);
        super.onBackPressed();
    }
}
