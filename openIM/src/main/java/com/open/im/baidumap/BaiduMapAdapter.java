package com.open.im.baidumap;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.open.im.R;

import java.util.List;

public class BaiduMapAdapter extends CommonAdapter<PoiInfo> {

    private int selectPosition;

    public BaiduMapAdapter(Context context, List<PoiInfo> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, PoiInfo poiInfo, int position) {
        TextView name = holder.getView(R.id.adapter_baidumap_location_name);
        TextView address = holder.getView(R.id.adapter_baidumap_location_address);
        ImageView checked = holder.getView(R.id.adapter_baidumap_location_checked);
        if (position == selectPosition) {
            checked.setVisibility(View.VISIBLE);
        } else {
            checked.setVisibility(View.GONE);
        }
        name.setText(poiInfo.name);
        address.setText(poiInfo.address);
    }

    public void setSelection(int selectPosition) {
        this.selectPosition = selectPosition;
    }
}
