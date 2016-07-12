package com.open.im.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyPicUtils {
	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return 根据像素要求返回图片的压缩比例
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * 根据路径获得突破并压缩返回bitmap用于显示
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateInSampleSize(options, 1080, 1920);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		// 根据像素要求压缩后，然后判断压缩后的图片的大小，看是否需要第二次压缩，保证图片大小在1M以下
		return compressImage(bitmap);
	}

	/**
	 * 根据bitmap保存文件
	 * 
	 * @param scale
	 *            图片保存时的压缩比例  100%
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	public static String saveFile(Bitmap bm, String dirPath, String fileName, int scale) {
		File dirFile = new File(dirPath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File myCaptureFile = new File(dirPath + fileName);
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.JPEG, scale, bos);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dirPath + fileName;
	}

//	/**
//	 * function:图片转圆角
//	 *
//	 * @param bitmap
//	 *            需要转的bitmap
//	 * @param pixels
//	 *            转圆角的弧度
//	 * @return 转圆角的bitmap
//	 */
//	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
//		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
//		Canvas canvas = new Canvas(output);
//		final int color = 0xff424242;
//		final Paint paint = new Paint();
//		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//		final RectF rectF = new RectF(rect);
//		final float roundPx = pixels;
//		paint.setAntiAlias(true);
//		canvas.drawARGB(0, 0, 0, 0);
//		paint.setColor(color);
//		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//		canvas.drawBitmap(bitmap, rect, rect, paint);
//		return output;
//	}

	/**
	 * 图片的质量压缩方法
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 512) { // 循环判断如果压缩后图片是否大于512kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		if (baos != null) {
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (isBm != null) {
			try {
				isBm.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (image != null && !image.isRecycled()) {
			image.recycle();
			image = null;
		}
		return bitmap;
	}

//	/**
//	 * 图片按比例大小压缩方法(根据Bitmap图片压缩)
//	 *
//	 * @param image
//	 * @return
//	 */
//	public static Bitmap getImage(Context context, Bitmap image) {
//
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
//			baos.reset();// 重置baos即清空baos
//			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
//		}
//		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
//		BitmapFactory.Options newOpts = new BitmapFactory.Options();
//		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
//		newOpts.inJustDecodeBounds = true;
//		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//		newOpts.inJustDecodeBounds = false;
//		int w = newOpts.outWidth;
//		int h = newOpts.outHeight;
//		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//		int hh = 400;// 这里设置高度为800f
//		int ww = 240;// 这里设置宽度为480f
//		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//		int be = 1;// be=1表示不缩放
//
//		MyLog.showLog("w::" + w);
//		MyLog.showLog("ww::" + ww);
//		if (h > hh && w > ww) {
//			be = w / ww > h / hh ? w / ww : h / hh;
//		}
////		if (w >= h && w >= ww) {// 如果宽度大的话根据宽度固定大小缩放
////			be = (int) (newOpts.outWidth / ww) + 1;
////			MyLog.showLog("高大于屏幕宽度");
////		} else if (w < h && h >= hh) {// 如果高度高的话根据宽度固定大小缩放
////			// 如果图片高度大于800 则
////			be = (int) (newOpts.outHeight / hh) + 1;
////			MyLog.showLog("高大于屏幕高度");
////		}
//		if (be <= 1)
//			be = 1;
//
//		MyLog.showLog("缩放比例::" + be);
//
//		newOpts.inSampleSize = be;// 设置缩放比例
//		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//		isBm = new ByteArrayInputStream(baos.toByteArray());
//		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//		if (isBm != null) {
//			try {
//				isBm.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		if (image != null && !image.isRecycled()) {
//			image.recycle();
//			image = null;
//		}
//		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
//	}
}
