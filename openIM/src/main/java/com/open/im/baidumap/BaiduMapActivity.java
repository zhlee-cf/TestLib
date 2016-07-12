package com.open.im.baidumap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.open.im.R;
import com.open.im.utils.MyFileUtils;
import com.open.im.utils.MyNetUtils;
import com.open.im.utils.MyUtils;
import com.open.im.utils.ThreadUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaiduMapActivity extends Activity implements View.OnClickListener {

    public final static String LATITUDE = "latitude";
    public final static String LONGITUDE = "longitude";
    public final static String ADDRESS = "address";
    public final static String NAME = "name";
    public final static String SNAPSHOTPATH = "snapshotpath";
    public final static int SNAPSHOT_SUCCESS = 101;
    private ImageButton original;

    private BaiduMapAdapter adatper;

    //    第一个是定位出的真实位置，第二个是用户选择的位置
    private LatLng originalLL, currentLL;// 初始化时的经纬度和地图滑动时屏幕中央的经纬度

    static MapView mMapView = null;
    private GeoCoder mSearch = null;
    private LocationClient mLocClient;// 定位相关
    public MyLocationListener myListener = new MyLocationListener();

    private Button send = null;
    private PoiSearch mPoiSearch;

    private List<PoiInfo> datas;
    private PoiInfo lastInfo = null;
    public static BaiduMapActivity act = null;
    private ProgressDialog progressDialog;
    private BaiduMap mBaiduMap;
    private MapStatusUpdate myselfU;

    private ListView listView;

    private boolean changeState = true;// 当滑动地图时再进行附近搜索

    private int preCheckedPosition = 0;// 点击的前一个位置

    private TextView refreshText;

    private String picUrl;

    private ProgressDialog pd;
    private boolean isFirstLoad;
    private ImageButton back;
    private BroadcastReceiver netReceiver;
    private String locationResult;

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ib_back) {
            finish();

        } else if (i == R.id.bmap_local_myself) {
            if (currentLL != originalLL) {
                changeState = true;
                mBaiduMap.animateMapStatus(myselfU);
            }

        } else if (i == R.id.btn_send) {
            pd = new ProgressDialog(act);
            pd.setMessage("正在发送位置...");
            pd.show();

            /**
             * 截图 并回调
             */
            mBaiduMap.snapshot(new SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap snapshot) {
                    final String picDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/exiu/cache/map/";
                    final String picName = new Date().getTime() + ".jpg";
                    final String filePath = picDirPath + picName;
                    File file = new File(filePath);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    FileOutputStream out;
                    try {
                        out = new FileOutputStream(file);
                        if (snapshot.compress(Bitmap.CompressFormat.JPEG, 80, out)) {
                            out.flush();
                            out.close();
                            /**
                             * 开子线程上传并复制文件到cache
                             */
                            ThreadUtil.runOnBackThread(new Runnable() {
                                @Override
                                public void run() {
                                    locationResult = MyFileUtils.uploadLocation(filePath, lastInfo.location.longitude, lastInfo.location.latitude, 0.01, lastInfo.address);
                                    if (locationResult != null) {
                                        handler.sendEmptyMessage(SNAPSHOT_SUCCESS);
//                                            ReceiveBean receiveBean = MyBase64Utils.decodeToBean(locationResult);
//                                            picUrl = receiveBean.getProperties().getThumbnail();
                                    }
                                    // 文件名是 URL用MD5加密
//										String saveName = MyMD5Encoder.encode(picUrl) + ".jpg";
                                    // 缓存保存路径
//										String cachePath = Environment.getExternalStorageDirectory() + "/exiu/cache/image/" + saveName;
                                    // 发送文件后，把压缩后的图片复制到缓存文件夹，以返回的文件名命名
//										MyCopyUtils.copyImage(picDirPath + picName, cachePath);
                                }
                            });
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class BaiduSDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            String st1 = "网络异常";
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {

                String st2 = "key validation error!请检查你的AndroidManifest.xml中的密钥是否正确";
                Toast.makeText(act, st2, Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(act, st1, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BaiduSDKReceiver mBaiduReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        act = this;
        setContentView(R.layout.activity_baidumap);
        if (MyNetUtils.isNetworkConnected(act)) {
            init();
        } else {
            MyUtils.showToast(act, "您没有连接网络，无法查看位置");
            finish();
        }
    }

    private void init() {
        original = (ImageButton) findViewById(R.id.bmap_local_myself);
        listView = (ListView) findViewById(R.id.bmap_listview);
        mMapView = (MapView) findViewById(R.id.bmap_View);
        mSearch = GeoCoder.newInstance();
        send = (Button) findViewById(R.id.btn_send);
        back = (ImageButton) findViewById(R.id.ib_back);
        back.setOnClickListener(this);
        refreshText = (TextView) findViewById(R.id.bmap_refresh);
        ImageView centerIcon = (ImageView) findViewById(R.id.bmap_center_icon);

        isFirstLoad = true;

        datas = new ArrayList<PoiInfo>();
        adatper = new BaiduMapAdapter(BaiduMapActivity.this, datas, R.layout.adapter_baidumap_item);
        listView.setAdapter(adatper);
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra(LATITUDE, 0);
        LocationMode mCurrentMode = LocationMode.NORMAL;
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        mPoiSearch = PoiSearch.newInstance();
        mMapView.setLongClickable(true);
        MyBaiduMapUtils.goneMapViewChild(mMapView, true, true);
//        // 隐藏百度logo ZoomControl
//        int count = mMapView.getChildCount();
//        for (int i = 0; i < count; i++) {
//            View child = mMapView.getChildAt(i);
//            if (child instanceof ImageView || child instanceof ZoomControls) {
//                child.setVisibility(View.INVISIBLE);
//            }
//        }
//        // 隐藏比例尺
////        mMapView.showScaleControl(false);
        if (latitude == 0) {
            mMapView = new MapView(this, new BaiduMapOptions());
            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));
            mBaiduMap.setMyLocationEnabled(true);
            send.setVisibility(View.VISIBLE);
            showMapWithLocationClient();
            setOnclick();
        } else {
            double longitude = intent.getDoubleExtra(LONGITUDE, 0);
            String address = intent.getStringExtra(ADDRESS);
            LatLng p = new LatLng(latitude, longitude);
            mMapView = new MapView(this, new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(p).build()));
            listView.setVisibility(View.GONE);
            refreshText.setVisibility(View.GONE);
            original.setVisibility(View.GONE);
            centerIcon.setVisibility(View.GONE);
            send.setVisibility(View.GONE);
            showMap(latitude, longitude, address.split("|")[1]);
        }

        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mBaiduReceiver = new BaiduSDKReceiver();
        registerReceiver(mBaiduReceiver, iFilter);

        /**
         * 注册网络连接监听
         */
        netReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                    boolean isConnected = MyNetUtils.isNetworkConnected(context);
                    if (isConnected) {
                        send.setEnabled(true);
                        send.setClickable(true);
                    } else {
                        send.setEnabled(false);
                        send.setClickable(false);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netReceiver, filter);

    }

    /**
     * 设置点击事件
     */
    private void setOnclick() {
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                changeState = true;
            }
        });
        original.setOnClickListener(this);
        listView.setOnItemClickListener(new MyItemClickListener());
        mPoiSearch.setOnGetPoiSearchResultListener(new MyGetPoiSearchResult());
        mSearch.setOnGetGeoCodeResultListener(new MyGetGeoCoderResultListener());
        mBaiduMap.setOnMapStatusChangeListener(new MyMapStatusChangeListener());
        send.setOnClickListener(this);
    }

    private boolean isSearchFinished;
    private boolean isGeoCoderFinished;

    private void refreshAdapter() {
        if (isSearchFinished && isGeoCoderFinished) {
            adatper.notifyDataSetChanged();
            refreshText.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            isSearchFinished = false;
            isGeoCoderFinished = false;
        }
    }

    /**
     * 根据关键字查找附近的位置信息
     */
    private class MyGetPoiSearchResult implements OnGetPoiSearchResultListener {

        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult.getAllPoi() != null) {
                datas.addAll(poiResult.getAllPoi());
            }
            preCheckedPosition = 0;
            isSearchFinished = true;
            refreshAdapter();
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }
    }

    /**
     * 根据经纬度进行反地理编码
     */
    private class MyGetGeoCoderResultListener implements OnGetGeoCoderResultListener {

        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (lastInfo == null || result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }

            lastInfo.address = result.getAddress();
            lastInfo.location = result.getLocation();
            lastInfo.name = "[位置]";
            datas.add(lastInfo);
            preCheckedPosition = 0;
            adatper.setSelection(0);
            isGeoCoderFinished = true;
            refreshAdapter();
        }
    }

    /**
     * 监听位置发生了变化
     */
    private class MyMapStatusChangeListener implements BaiduMap.OnMapStatusChangeListener {

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {
            if (changeState) {
                datas.clear();
                refreshText.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            if (changeState) {
                if (isFirstLoad) {
                    originalLL = mapStatus.target;
                    isFirstLoad = false;
                }
                currentLL = mapStatus.target;
                // 反Geo搜索
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(currentLL));
//				mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword("小区").location(currentLL).radius(1000));
                nearbySearch(0);
            }
        }
    }

    /**
     * 附近搜索
     *
     * @param page
     */
    private void nearbySearch(int page) {
        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .keyword("小区").location(currentLL).radius(500).pageNum(page).pageCapacity(10));
    }


    /**
     * 查看别人发过来，或者已经发送出去的位置信息
     *
     * @param latitude  维度
     * @param longitude 经度
     * @param address   详细地址信息
     */
    private void showMap(double latitude, double longitude, String address) {
        send.setVisibility(View.GONE);
        LatLng llA = new LatLng(latitude, longitude);
        OverlayOptions ooA = new MarkerOptions().position(llA).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_yourself_lication)).zIndex(4).draggable(true);
        mBaiduMap.addOverlay(ooA);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
        mBaiduMap.animateMapStatus(u);
    }

    /**
     * 显示当前的位置信息
     */
    private void showMapWithLocationClient() {
        String str1 = "正在刷新";
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(str1);
        progressDialog.setOnCancelListener(new OnCancelListener() {

            public void onCancel(DialogInterface arg0) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                finish();
            }
        });

        progressDialog.show();

        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("gcj02");
//        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setScanSpan(10000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        if (mLocClient != null) {
            mLocClient.stop();
        }
        super.onPause();
        lastInfo = null;
    }

    @Override
    protected void onDestroy() {
        if (mLocClient != null)
            mLocClient.stop();
        if (mMapView != null)
            mMapView.onDestroy();
        if (mBaiduReceiver != null)
            unregisterReceiver(mBaiduReceiver);
        if (netReceiver != null) {
            unregisterReceiver(netReceiver);
        }
        super.onDestroy();
    }

    /**
     * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }

            send.setEnabled(true);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            if (lastInfo != null) {
                return;
            }
            lastInfo = new PoiInfo();
            mBaiduMap.clear();
            LatLng llA = new LatLng(location.getLatitude(), location.getLongitude());

            lastInfo.location = llA;
            lastInfo.address = location.getAddrStr();
            lastInfo.name = "[位置]";

            LatLng ll = new LatLng(location.getLatitude() - 0.0002, location.getLongitude());
            CoordinateConverter converter = new CoordinateConverter();// 坐标转换工具类
            converter.coord(ll);// 设置源坐标数据
            converter.from(CoordinateConverter.CoordType.COMMON);// 设置源坐标类型
            LatLng convertLatLng = converter.convert();
            OverlayOptions myselfOOA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_yourself_lication)).zIndex(4).draggable(true);
            mBaiduMap.addOverlay(myselfOOA);
            myselfU = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
            mBaiduMap.animateMapStatus(myselfU);

        }

    }

    /**
     * 点击相应的位置，移动到该位置
     */
    private class MyItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (preCheckedPosition != position) {
                adatper.setSelection(position);
                View view1 = listView.getChildAt(preCheckedPosition - listView.getFirstVisiblePosition());
                ImageView checked;
                if (view1 != null) {
                    checked = (ImageView) view1.findViewById(R.id.adapter_baidumap_location_checked);
                    checked.setVisibility(View.GONE);
                }
                preCheckedPosition = position;
                changeState = false;
                PoiInfo info = datas.get(position);
                LatLng llA = info.location;
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
                mBaiduMap.animateMapStatus(u);
                lastInfo = info;
                checked = (ImageView) view.findViewById(R.id.adapter_baidumap_location_checked);
                checked.setVisibility(View.VISIBLE);
            }

        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == SNAPSHOT_SUCCESS) { // 截图成功并上传成功 并复制成功时，回到聊天详情页面

                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }

                Intent intent = BaiduMapActivity.this.getIntent();
//                intent.putExtra(LATITUDE, lastInfo.location.latitude);
//                intent.putExtra(LONGITUDE, lastInfo.location.longitude);
//                intent.putExtra(ADDRESS, lastInfo.address);
//                intent.putExtra(NAME, lastInfo.name);
//                intent.putExtra(SNAPSHOTPATH, picUrl);
                intent.putExtra("locationResult", locationResult);
                BaiduMapActivity.this.setResult(RESULT_OK, intent);
                finish();
            }
        }

        ;
    };
}
