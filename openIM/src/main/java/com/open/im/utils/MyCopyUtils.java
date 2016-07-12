package com.open.im.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 复制文件工具类
 * 
 * @author Administrator
 * 
 */
public class MyCopyUtils {
	/**
	 * 方法 复制图片
	 * 
	 * @param src
	 * @param dest
	 */
	public static void copyImage(String src, String dest) {
		try {

			File file = new File(dest);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			FileInputStream fis = new FileInputStream(src);
			FileOutputStream fos = new FileOutputStream(dest);

			// 一次读写一个字节数组
			byte[] bys = new byte[8192];
			int len = 0;
			while ((len = fis.read(bys)) != -1) {
				fos.write(bys, 0, len);
			}
			fis.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
