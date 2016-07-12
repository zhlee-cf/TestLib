package com.open.im.utils;

import android.content.ClipboardManager;
import android.content.Context;

/**
 * 文本操作工具类
 */
public class MyTextUtils {
	/**
	 * 复制文本
	 * 
	 * @param context
	 * @param message
	 *            // 被复制的文本
	 */
	public static void copyText(Context context, String message) {
		// 获取剪贴板管理服务
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		// 将文本数据复制到剪贴板
		cmb.setText(message.trim());
	}

	/**
	 * 粘贴文本
	 * 
	 * @param context
	 * @return
	 */
	public static String pasteText(Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		return cmb.getText().toString().trim();
	}

}