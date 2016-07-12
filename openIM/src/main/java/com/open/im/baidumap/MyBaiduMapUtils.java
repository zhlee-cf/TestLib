package com.open.im.baidumap;

import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.MapView;

public class MyBaiduMapUtils {

	/**
	 * @Description: 隱藏百度logo亦或百度自帶的縮放鍵
	 * @param mMapView
	 * @param goneLogo
	 * @param goneZoomControls
	 * @return void
	 */
	public static void goneMapViewChild(MapView mMapView, boolean goneLogo,
										boolean goneZoomControls) {
		int count = mMapView.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = mMapView.getChildAt(i);
			if (child instanceof ImageView && goneLogo) { // 隐藏百度logo
				child.setVisibility(View.GONE);
			}
			if (child instanceof ZoomControls && goneZoomControls) { // 隐藏百度的縮放按鍵
				child.setVisibility(View.GONE);
			}
		}
	}
}
