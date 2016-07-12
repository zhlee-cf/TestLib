package com.open.im.pager;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.open.im.R;
import com.open.im.activity.ChatActivity;
import com.open.im.activity.MainActivity;
import com.open.im.adapter.NewsLVAdapter;
import com.open.im.app.MyApp;
import com.open.im.bean.MessageBean;
import com.open.im.db.OpenIMDao;
import com.open.im.utils.MyConstance;
import com.open.im.utils.ThreadUtil;
import com.open.im.view.MyDialog;

import java.util.ArrayList;
import java.util.List;

public class NewsPager extends BasePager {

    private MainActivity act;
    private ListView mListView;
    private List<MessageBean> list = new ArrayList<MessageBean>();
    private static final int QUERY_SUCCESS = 100;
    private MyDialog pd;
    private NewsLVAdapter mAdapter;
    private final OpenIMDao openIMDao;

    public NewsPager(Context ctx) {
        super(ctx);
        act = (MainActivity) ctx;
        openIMDao = OpenIMDao.getInstance(ctx);
    }

    @Override
    public View initView() {
        View view = View.inflate(act, R.layout.pager_im_news, null);
        mListView = (ListView) view.findViewById(R.id.listview);
        return view;
    }

    @Override
    public void initData() {
        pd = new MyDialog(act);
        pd.show();
        ThreadUtil.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                list.clear();
                List<MessageBean> data = openIMDao.queryConversation(MyApp.username);
                for (MessageBean messageBean : data) {
                    list.add(messageBean);
                }
                // 发送查询完成消息
                handler.sendEmptyMessage(QUERY_SUCCESS);
            }
        });

        /**
         * 不知道为嘛 cursorAdapter在activity外使用就不能自动更新了 所以在这儿写了个内容观察者，观察数据库的URL
         * 如果数据库发生变化 就改变cursor 然后让adapter刷新cursor
         */
        ctx.getContentResolver().registerContentObserver(MyConstance.URI_MSG, true, new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                ThreadUtil.runOnBackThread(new Runnable() {
                    @Override
                    public void run() {
                        List<MessageBean> data = openIMDao.queryConversation(MyApp.username);
                        list.clear();
                        for (MessageBean messageBean : data) {
                            list.add(messageBean);
                        }
                        // 发送查询完成消息
                        handler.sendEmptyMessage(QUERY_SUCCESS);
                    }
                });
            }
        });

    }
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                // 查询成功
                case QUERY_SUCCESS:
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    if (mAdapter == null) {
                        mAdapter = new NewsLVAdapter(act, list, 0);
                    } else {
                        // 这个要求adapter对应的list是同一个对象才能生效，不同对象不能生效
                        mAdapter.notifyDataSetChanged();
//                        MyLog.showLog("数据库改变");
                    }
                    mListView.setAdapter(mAdapter);

                    /**
                     * 条目点击事件，跳转到聊天详情界面
                     */
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ListView listView = (ListView) parent;
                            MessageBean bean = (MessageBean) listView.getItemAtPosition(position);

                            String msgFrom = bean.getFromUser();
                            String friendNick = bean.getNick();
                            String msgTo = bean.getToUser();
                            String avatarUrl = bean.getAvatar();
                            String friendName;
                            if (msgFrom.equals(MyApp.username)) {
                                friendName = msgTo;
                            } else {
                                friendName = msgFrom;
                            }

                            Intent intent = new Intent(act, ChatActivity.class);
                            intent.putExtra("friendName", friendName);
                            intent.putExtra("friendNick", friendNick);
                            intent.putExtra("avatarUrl",avatarUrl);
                            act.startActivity(intent);
                        }
                    });
                    break;
            }
        }

        ;
    };
}
