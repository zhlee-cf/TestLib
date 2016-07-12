package com.open.im.pager;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.open.im.R;
import com.open.im.activity.MainActivity;
import com.open.im.activity.SubscribeActivity;
import com.open.im.activity.UserInfoActivity;
import com.open.im.app.MyApp;
import com.open.im.bean.VCardBean;
import com.open.im.db.OpenIMDao;
import com.open.im.utils.MyBitmapUtils;
import com.open.im.utils.MyConstance;
import com.open.im.utils.MyLog;
import com.open.im.utils.PinyinComparator;
import com.open.im.utils.ThreadUtil;
import com.open.im.view.CircularImage;
import com.open.im.view.MyDialog;
import com.open.im.view.SideBar;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class ContactPager extends BasePager implements View.OnClickListener {

    private MyFriendAdapter mFriendAdapter;
    private MainActivity act;
    private ListView lv_show_friends;
    private TextView mDialogText;
    private ArrayList<String> friendNicks = new ArrayList<String>();
    private String[] nicks;
    private MyDialog pd;

    private final static int LOAD_SUCCESS = 201;
    private List<String> avatars;
    private LinearLayout ll_stranger;

    private TreeMap<String, VCardBean> map = new TreeMap<String, VCardBean>();
    private MyBitmapUtils bitmapUtils;
    private CircularImage iv_avatar1;
    private CircularImage iv_avatar2;
    private CircularImage iv_avatar3;
    private OpenIMDao openIMDao;

    public ContactPager(Context ctx) {
        super(ctx);
        act = (MainActivity) ctx;
    }

    @Override
    public View initView() {

        WindowManager mWindowManager = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);

        View view = View.inflate(act, R.layout.pager_im_constact, null);
        lv_show_friends = (ListView) view.findViewById(R.id.lv_show_friends);
        ll_stranger = (LinearLayout) view.findViewById(R.id.ll_stranger);
        iv_avatar1 = (CircularImage) view.findViewById(R.id.iv_avatar1);
        iv_avatar2 = (CircularImage) view.findViewById(R.id.iv_avatar2);
        iv_avatar3 = (CircularImage) view.findViewById(R.id.iv_avatar3);

        SideBar indexBar = (SideBar) view.findViewById(R.id.sideBar);
        indexBar.setListView(lv_show_friends);
        mDialogText = (TextView) View.inflate(act, R.layout.list_position, null);
        mDialogText.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        mWindowManager.addView(mDialogText, lp);
        indexBar.setTextView(mDialogText);

        return view;
    }

    @Override
    /**
     * 初始化数据 设置adapter
     */
    public void initData() {
        openIMDao = OpenIMDao.getInstance(act);
        bitmapUtils = new MyBitmapUtils(act);
        pd = new MyDialog(act);
        ThreadUtil.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                //查询所有的好友
                queryFriends();
                queryAvatars();
            }
        });
        // 注册ListView条目点击事件
        register();
    }

    /**
     * 查询好友
     */
    public synchronized void queryFriends() {
        ThreadUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (pd != null && !pd.isShowing() && act != null && !act.isFinishing()) {
                    pd.show();
                }
            }
        });
        friendNicks.clear();
        map.clear();
        if (openIMDao == null) {
            openIMDao = OpenIMDao.getInstance(act);
        }
        List<VCardBean> allVCard = openIMDao.findAllVCard();
        for (VCardBean vCard : allVCard) {
            if (vCard.getNick() != null) {
                map.put(vCard.getNick(), vCard);
            } else {
                map.put(vCard.getJid(), vCard);
            }
            friendNicks.add(vCard.getNick());
        }
//        // 查询最新的三条好友请求信息
//        avatars = openIMDao.findSubByOwner4Avatar(MyApp.username, 3);
//        MyLog.showLog("avatars::" + avatars);
        handler.sendEmptyMessage(LOAD_SUCCESS);
    }

    private void queryAvatars(){
        // 查询最新的三条好友请求信息
        avatars = openIMDao.findSubByOwner4Avatar(MyApp.username, 3);
        MyLog.showLog("avatars::" + avatars);
        handler.sendEmptyMessage(LOAD_SUCCESS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_stranger:
                act.startActivity(new Intent(act, SubscribeActivity.class));
                break;
        }
    }

    /**
     * 好友列表的Adapter
     */
    private class MyFriendAdapter extends BaseAdapter implements SectionIndexer {

        public MyFriendAdapter() {
            if (nicks.length > 1) {
                Arrays.sort(nicks, new PinyinComparator());
            }
        }

        @Override
        public int getCount() {
            return nicks.length;
        }

        @Override
        public Object getItem(int position) {
            if (nicks.length != 0) {
                return nicks[position];
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String friendName = nicks[position];
            View view;
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                view = View.inflate(act, R.layout.list_item_contact, null);
                vh.tvNick = (TextView) view.findViewById(R.id.tv_nick);
                vh.tvCatalog = (TextView) view.findViewById(R.id.tv_log);
                vh.tvDesc = (TextView) view.findViewById(R.id.tv_desc);
                vh.ivAvatar = (CircularImage) view.findViewById(R.id.iv_avatar);
                vh.ivAvatar.setTag(position);
                view.setTag(vh);
            } else {
                view = convertView;
                vh = (ViewHolder) view.getTag();
            }
            String catalog = converterToFirstSpell(friendName).substring(0, 1).toUpperCase();
            if (position == 0) {
                vh.tvCatalog.setVisibility(View.VISIBLE);
                vh.tvCatalog.setText(catalog);
            } else {
                String lastCatalog = converterToFirstSpell(nicks[position - 1]).substring(0, 1);
                if (catalog.equalsIgnoreCase(lastCatalog)) {
                    vh.tvCatalog.setVisibility(View.GONE);
                } else {
                    vh.tvCatalog.setVisibility(View.VISIBLE);
                    vh.tvCatalog.setText(catalog);
                }
            }

            vh.tvNick.setText(nicks[position]);
            VCardBean vCardBean = map.get(nicks[position]);
            if (vCardBean != null) {
                if (vCardBean.getAvatar() != null) {
                    bitmapUtils.display(vh.ivAvatar, vCardBean.getAvatar());
                } else {
                    vh.ivAvatar.setImageResource(R.mipmap.ic_launcher);
                }
                if (vCardBean.getDesc() != null && !vCardBean.getDesc().equals("未填写")) {
                    vh.tvDesc.setText(vCardBean.getDesc());
                } else {
                    vh.tvDesc.setText("");
                }
            } else {
                vh.ivAvatar.setImageResource(R.mipmap.ic_launcher);
                vh.tvDesc.setText("");
            }
            return view;
        }

        @Override
        public int getPositionForSection(int section) {
            for (int i = 0; i < friendNicks.size(); i++) {
                String l = converterToFirstSpell(friendNicks.get(i)).substring(0, 1);
                char firstChar = l.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override
        public Object[] getSections() {
            return null;
        }

    }

    static class ViewHolder {
        TextView tvCatalog;// 目录
        CircularImage ivAvatar;// 头像
        TextView tvNick;// 昵称
        public TextView tvDesc;
    }

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToFirstSpell(String chines) {
        String pinyinName = "";
        if (chines != null) {
            char[] nameChar = chines.toCharArray();
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
            defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            for (int i = 0; i < nameChar.length; i++) {
                if (nameChar[i] > 128) {
                    try {
                        pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);
                    } catch (BadHanyuPinyinOutputFormatCombination e) {
                        e.printStackTrace();
                    }
                } else {
                    pinyinName += nameChar[i];
                }
            }
        }
        return pinyinName;
    }

    private void register() {
        act.getContentResolver().registerContentObserver(MyConstance.URI_VCARD, true, new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                queryFriends();
            }
        });

        act.getContentResolver().registerContentObserver(MyConstance.URI_SUB, true, new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                queryAvatars();
            }
        });


        /**
         * 好友列表条目设置点击监听
         */
        lv_show_friends.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friendJid = map.get(nicks[position]).getJid();
                // 跳转到会话界面
                Intent intent = new Intent(act, UserInfoActivity.class);
                intent.putExtra("friendJid", friendJid);
                intent.putExtra("type", 2);
                act.startActivity(intent);
            }
        });

        ll_stranger.setOnClickListener(this);
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    pdDismiss();
                    nicks = (String[]) friendNicks.toArray(new String[friendNicks.size()]);
                    mFriendAdapter = new MyFriendAdapter();
                    lv_show_friends.setAdapter(mFriendAdapter);
                    switch (avatars.size()) {
                        case 0:
                            iv_avatar1.setVisibility(View.GONE);
                            iv_avatar2.setVisibility(View.GONE);
                            iv_avatar3.setVisibility(View.GONE);
                            break;
                        case 1:
                            iv_avatar1.setVisibility(View.GONE);
                            iv_avatar2.setVisibility(View.GONE);
                            iv_avatar3.setVisibility(View.VISIBLE);
                            iv_avatar3.setTag(-1);
                            if (avatars.get(0) != null) {
                                bitmapUtils.display(iv_avatar3, avatars.get(0));
                            } else {
                                iv_avatar3.setImageResource(R.mipmap.ic_launcher);
                            }
                            break;
                        case 2:
                            iv_avatar1.setVisibility(View.GONE);
                            iv_avatar2.setVisibility(View.VISIBLE);
                            iv_avatar2.setTag(-2);
                            if (avatars.get(1) != null) {
                                bitmapUtils.display(iv_avatar2, avatars.get(1));
                            } else {
                                iv_avatar2.setImageResource(R.mipmap.ic_launcher);
                            }
                            iv_avatar3.setVisibility(View.VISIBLE);
                            iv_avatar3.setTag(-1);
                            if (avatars.get(0) != null) {
                                bitmapUtils.display(iv_avatar3, avatars.get(0));
                            } else {
                                iv_avatar3.setImageResource(R.mipmap.ic_launcher);
                            }
                            break;
                        case 3:
                            iv_avatar1.setVisibility(View.VISIBLE);
                            iv_avatar1.setTag(-1);
                            if (avatars.get(2) != null) {
                                bitmapUtils.display(iv_avatar1, avatars.get(2));
                            } else {
                                iv_avatar1.setImageResource(R.mipmap.ic_launcher);
                            }
                            iv_avatar2.setVisibility(View.VISIBLE);
                            iv_avatar2.setTag(-2);
                            if (avatars.get(1) != null) {
                                bitmapUtils.display(iv_avatar2, avatars.get(1));
                            } else {
                                iv_avatar2.setImageResource(R.mipmap.ic_launcher);
                            }
                            iv_avatar3.setVisibility(View.VISIBLE);
                            iv_avatar3.setTag(-3);
                            if (avatars.get(0) != null) {
                                bitmapUtils.display(iv_avatar3, avatars.get(0));
                            } else {
                                iv_avatar3.setImageResource(R.mipmap.ic_launcher);
                            }
                            break;
                    }

                    /**
                     * listView滑动时，显示当前可见的第一条的拼音首字母
                     */
                    lv_show_friends.setOnScrollListener(new OnScrollListener() {

                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            String firstItem = (String) mFriendAdapter.getItem(firstVisibleItem);
                            String converterToFirstSpell = converterToFirstSpell(firstItem);
                            if (!TextUtils.isEmpty(converterToFirstSpell)) {
                                String log = converterToFirstSpell.substring(0, 1).toUpperCase();
                                mDialogText.setText(log);
                            }
                        }
                    });

                    /**
                     * 判断何时显示提示首字母的view
                     */
                    lv_show_friends.setOnTouchListener(new OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                mDialogText.setVisibility(View.INVISIBLE);
                            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                mDialogText.setVisibility(View.VISIBLE);
                            }
                            return false;
                        }
                    });
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private void pdDismiss() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }
}
