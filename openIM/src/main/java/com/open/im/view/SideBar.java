package com.open.im.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.open.im.activity.MainActivity;

/**
 * 通讯录右侧的字母栏
 *
 * @author Administrator
 */
public class SideBar extends View {
    private char[] l;
    private SectionIndexer sectionIndexter = null;
    private ListView list;
    private TextView mDialogText;
    private MainActivity act;
    private int m_nItemHeight;

    public SideBar(Context context) {
        super(context);
        act = (MainActivity) context;
        init();
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        act = (MainActivity) context;
        init();
    }

    private void init() {
        l = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        final ViewTreeObserver vto = getViewTreeObserver();
        /**
         * 添加布局完成监听
         */
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                m_nItemHeight = getHeight() / 26;
            }
        });
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        act = (MainActivity) context;
        init();
    }

    public void setListView(ListView _list) {
        list = _list;
        sectionIndexter = (SectionIndexer) _list.getAdapter();
    }

    public void setTextView(TextView mDialogText) {
        this.mDialogText = mDialogText;
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int i = (int) event.getY();
        int idx = i / m_nItemHeight;
        if (idx >= l.length) {
            idx = l.length - 1;
        } else if (idx < 0) {
            idx = 0;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            mDialogText.setVisibility(View.VISIBLE);
            mDialogText.setText("" + l[idx]);
            if (sectionIndexter == null) {
                sectionIndexter = (SectionIndexer) list.getAdapter();
            }
            int position = sectionIndexter.getPositionForSection(l[idx]);
            if (position == -1) {
                return true;
            }
            list.setSelection(position);
        } else {
            mDialogText.setVisibility(View.INVISIBLE);
        }
        return true;
    }

    protected void onDraw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(0xff595c61);
        paint.setTextSize(m_nItemHeight - 5);
        paint.setTextAlign(Paint.Align.CENTER);
        float widthCenter = getMeasuredWidth() / 2;
        for (int i = 0; i < l.length; i++) {
            canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight + (i * m_nItemHeight), paint);
        }
        super.onDraw(canvas);
    }
}
