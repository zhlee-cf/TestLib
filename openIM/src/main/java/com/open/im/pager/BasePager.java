package com.open.im.pager;

import android.content.Context;
import android.view.View;

/**
 * 自定义pager的父类 
 * @author Administrator
 *
 */
public abstract class BasePager {
	
	public Context ctx;
	
	public BasePager(Context ctx){
		this.ctx = ctx;
	}
	
	/**
	 * 初始化控件
	 * @return
	 */
	public abstract View initView();
	
	/**
	 * 初始化数据
	 * @return
	 */
	public abstract void initData();
}
