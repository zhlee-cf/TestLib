package com.open.im.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open.im.R;
import com.open.im.utils.MyBitmapUtils;
import com.open.im.utils.MyCopyUtils;
import com.open.im.utils.MyFileUtils;
import com.open.im.utils.MyLog;
import com.open.im.utils.MyMD5Encoder;
import com.open.im.utils.MyPicUtils;
import com.open.im.utils.MyUtils;
import com.open.im.utils.ThreadUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 头像展示类
 * Created by Administrator on 2016/4/14.
 */
public class AvatarActivity extends BaseActivity implements View.OnClickListener {

    private AvatarActivity act;
    private int type;
    private PopupWindow popupWindow;
    private RelativeLayout rlSave;
    private RelativeLayout rlPic;
    private RelativeLayout rlCamera;
    private File tempFile;

    /**
     * 头像用
     */
    private static final int PHOTO_REQUEST_TAKEPHOTO = 10;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 11;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 0;// 结果
    private String avatarUrl;
    private Bitmap bitmap;
    private String avatarPath;
    private String dirPath = Environment.getExternalStorageDirectory() + "/exiu/cache/avatar/";
    private Intent intent;
    private boolean avatarChanged;
    private String cacheDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/exiu/cache/image/";
    private String avatarCachePath;
    private String DCIMPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    private String destPath;

    private ImageButton ibBack;
    private ImageView ivAvatar;
    private ImageView ivSave;
    private ImageView ivMore;
    private TextView tvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);

        act = this;
        avatarChanged = false;

        initView();

        initData();

        register();
    }

    private void initData() {
        intent = getIntent();
        String nickName = intent.getStringExtra("nickName");
        type = intent.getIntExtra("type", 0);
        avatarUrl = intent.getStringExtra("avatarUrl");
        MyBitmapUtils bitmapUtils = new MyBitmapUtils(act);
        if (type == 0) {
            ivSave.setVisibility(View.GONE);
            ivMore.setVisibility(View.VISIBLE);
            initPopupWindow();
            tvBack.setText("我的信息");
        } else {
            ivMore.setVisibility(View.GONE);
            ivSave.setVisibility(View.VISIBLE);
            tvBack.setText(nickName);
        }
        if (avatarUrl == null) {
            ivAvatar.setImageResource(R.mipmap.ic_launcher);
        } else {
            bitmapUtils.display(ivAvatar, avatarUrl);
        }
    }

    private void register() {
        ibBack.setOnClickListener(this);
        ivSave.setOnClickListener(this);
        ivMore.setOnClickListener(this);
        tvBack.setOnClickListener(this);
    }

    private void initView() {
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        ivAvatar.setTag(-5);
        ivSave = (ImageView) findViewById(R.id.iv_save);
        ivMore = (ImageView) findViewById(R.id.iv_more);
        tvBack = (TextView) findViewById(R.id.tv_back);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ib_back || i == R.id.tv_back) {
            if (type == 0 && avatarChanged) {
                intent.setData(Uri.parse(avatarPath));
                setResult(type, intent);
            }
            finish();

        } else if (i == R.id.iv_more) {
            MyUtils.showToast(act, "弹出pop");
            showPop(ivMore);

        } else if (i == R.id.iv_save) {
            MyUtils.showToast(act, "保存图片");
            saveAvatarToDCIM();

        } else if (i == R.id.rl_save) {
            saveAvatarToDCIM();
            MyUtils.showToast(act, "保存图片");
            if (popupWindow != null) {
                popupWindow.dismiss();
            }

        } else if (i == R.id.rl_pic) {
            MyUtils.showToast(act, "打开图库");
            Intent picIntent = new Intent(Intent.ACTION_PICK, null);
            picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(picIntent, PHOTO_REQUEST_GALLERY);
            if (popupWindow != null) {
                popupWindow.dismiss();
            }

        } else if (i == R.id.rl_camera) {
            MyUtils.showToast(act, "打开相机");
            // 调用系统的拍照功能
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 指定调用相机拍照后照片的储存路径
            tempFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "PicTest_" + System.currentTimeMillis() + ".jpg");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            startActivityForResult(cameraIntent, PHOTO_REQUEST_TAKEPHOTO);
            if (popupWindow != null) {
                popupWindow.dismiss();
            }

        }
    }

    /**
     * 保存头像到系统DCIM文件夹下
     */
    private void saveAvatarToDCIM() {
        if (avatarUrl != null) {
            String avatarName = MyMD5Encoder.encode(avatarUrl) + ".jpg";
            avatarCachePath = cacheDirPath + avatarName;
            destPath = DCIMPath + File.separator + avatarName;
            MyLog.showLog("1::" + avatarCachePath + "====2" + destPath);
            ThreadUtil.runOnBackThread(new Runnable() {
                @Override
                public void run() {
                    MyCopyUtils.copyImage(avatarCachePath, destPath);
                    MyFileUtils.scanFileToPhotoAlbum(act, destPath);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            startPhotoZoom(Uri.fromFile(tempFile), 350);
        } else if (requestCode == 11 && data != null) {
            startPhotoZoom(data.getData(), 350);
        } else if (requestCode == 0 && data != null) {
            MyLog.showLog("data::" + data.getDataString());
            savePic(data);
            ivAvatar.setImageBitmap(bitmap);
            avatarChanged = true;
        }
    }

    /**
     * 截图并保存
     *
     * @param data 包含头像的bitmap
     */
    private void savePic(Intent data) {
        Bundle bundle = data.getExtras();
        MyLog.showLog("bundle:" + bundle.size());
        bitmap = bundle.getParcelable("data");
        avatarPath = MyPicUtils.saveFile(bitmap, dirPath, getPhotoFileName(), 60);
    }

    /**
     * 使用系统当前日期加以调整作为照片的名称
     *
     * @return 头像图片名称
     */
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.CHINA);
        return dateFormat.format(date) + ".jpg";
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        View view = View.inflate(act, R.layout.pop_item_avatar, null);
        popupWindow = new PopupWindow();
        popupWindow.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);

        rlSave = (RelativeLayout) view.findViewById(R.id.rl_save);
        rlPic = (RelativeLayout) view.findViewById(R.id.rl_pic);
        rlCamera = (RelativeLayout) view.findViewById(R.id.rl_camera);
    }

    /**
     * PopupWindow显示
     *
     * @param v 在v这里展示pop
     */
    private void showPop(View v) {
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());// 设置此项可点击Popupwindow外区域消失，注释则不消失

        // 设置出现位置
        int[] location = new int[2];
        v.getLocationOnScreen(location);
//        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
//                location[0] + v.getWidth() / 2 - popupWindow.getWidth() / 2,
//                location[1] - popupWindow.getHeight());
        popupWindow.showAsDropDown(v);

        rlSave.setOnClickListener(this);
        rlPic.setOnClickListener(this);
        rlCamera.setOnClickListener(this);
    }

    /**
     * 方法 显示裁剪页面
     *
     * @param uri  本地图片的uri
     * @param size 截图尺寸
     */
    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @Override
    public void onBackPressed() {
        if (type == 0 && avatarChanged) {
            intent.setData(Uri.parse(avatarPath));
            setResult(type, intent);
        }
        super.onBackPressed();
    }
}
