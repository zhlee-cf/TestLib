package com.open.im.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 不再使用懒加载 每个界面的东西并不多 缓存可以提高打开速度
 * 
 * @author Administrator
 * 
 */
public class MyViewPager extends ViewPager {

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyViewPager(Context context) {
		super(context);
	}

	@Override
	/**
	 * 不处理事件
	 */
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	@Override
	/**
	 * 不中断事件
	 */
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}

	@Override
	/**
	 * false表示切换viewPager条目不需要时间
	 * 这个方法里的super调用下面那个方法，并把第二个参数置为false
	 */
	public void setCurrentItem(int item) {
		super.setCurrentItem(item, false);
	}

	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		super.setCurrentItem(item, smoothScroll);
	}

}
