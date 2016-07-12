package com.open.im.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 自定义的BitmapUtils，下载图片工具类
 * 
 * @author wangdh
 * 
 */
public class MyBitmapUtils {
	private Context context;// MainActivity
	/**
	 * 1. 总内存大小 2. 存储的bitmap的大小
	 */
	private LruCache<String, Bitmap> mLruCache;

	private File cacheFileDir = null;// 本地的缓存目录
	private Executor executor;

	public MyBitmapUtils(Context context) {
		this.context = context;
		// lrucache的内存大小，指定虚拟机内存的8/1
		int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);// 虚拟机最大内存16M
		mLruCache = new LruCache<String, Bitmap>(maxSize) {
			/**
			 * 表示Lrucache存储的每一个图片的大小
			 */
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// value.getByteCount();//字节个数，图片的总大小
				return value.getRowBytes() * value.getHeight();// 每行的字节数*高度（行数）
			}
		};
		// 获取cache目录
		cacheFileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/exiu/cache/image");
		// 线程池:创建一个固定大小的线程池，线程个数：3个
		executor = Executors.newFixedThreadPool(3);
	}

	/**
	 * 展示网络图片方法
	 * 
	 * @param imageView
	 *            ：显示图片的控件
	 * @param imageUrl
	 *            ：网络url
	 */
	public void display(ImageView imageView, String imageUrl) {
		/**
		 * 三级缓存： 1. 内存缓存 hashMap<key,value>,key:图片的url，value图片本身：bitmap v4
		 * :LruCache 类似hashMap lru算法： less（最少） recent（最近） use 2. 本地缓存
		 * sd卡，/mnt/sdcard/exiu/cache/image/xx.png （如果没有sd卡，可以缓存到data/data
		 * cache） xx:图片的名称。 图片的url+md5 为了避免图片url有特殊字符 3. 网络缓存
		 * 缓存：手机，没有联网情况下能获取的数据。
		 * 
		 * 使用步骤：（谁最快，先从谁中获取） 1.从内存缓存中获取图片，如果获取到，直接展示。如果获取不到，从本地获取
		 * 2.从本地缓存中获取，如果获取到，先加载到内存中，然后展示。如果获取不到，再从网络获取 3.
		 * 从网络获取，如果获取到，先加载到内存中，然后保存到本地，最后展示
		 * 
		 */
		// 1.从内存中获取图片
		Bitmap cacheBitmap = mLruCache.get(imageUrl);

		if (cacheBitmap != null) {
			System.out.println("从内存中获取");
			imageView.setImageBitmap(cacheBitmap);
			return;
		}
		// 2. 本地
		String cacheFileName = MyMD5Encoder.encode(imageUrl) + ".jpg";// url+md5
		File cacheFile = new File(cacheFileDir, cacheFileName);
		// 校验本地缓存文件
		if (cacheFile.exists() && cacheFile.length() > 0) {
			System.out.println("从本地中获取");
			Bitmap decodeFileImage = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
			// Bitmap scaledImage = MyPicUtils.getImage(context,
			// decodeFileImage);
			// Bitmap roundCorner = MyPicUtils.toRoundCorner(decodeFileImage,
			// 15);
			// 保存内存中
			if (decodeFileImage != null) {
				mLruCache.put(imageUrl, decodeFileImage);
			}
			// 展示
			imageView.setImageBitmap(decodeFileImage);
			// imageView.setImageBitmap(mLruCache.get(imageUrl));
			return;
		}

		// 3. 网络 子线程
		// new DownLoadThread().start(); //android中对于线程管理，一般都采用线程池
		// 请求的位置
		int currentPosition = (Integer) imageView.getTag();
		executor.execute(new DownLoadRunnable(imageView, imageUrl, currentPosition));
		System.out.println("从网络中获取");
	}

	/**
	 * 下载子线程
	 * 
	 * @author wangdh Runnable，子线程调用，run方法就在子线程中执行 主线程调用，run方法就在主线程执行
	 * 
	 */
	class DownLoadRunnable implements Runnable {
		private String imageUrl;// 图片url
		private ImageView imageView;// 展示图片的控件
		private int currentPosition;

		public DownLoadRunnable(ImageView imageView, String imageUrl, int currentPosition) {
			this.imageView = imageView;
			this.imageUrl = imageUrl;
			this.currentPosition = currentPosition;

		}
		@Override
		public void run() {
			/**
			 * 1. HttpUrlConnection 2. HttpClient 3. AsyncHttpClient 4.
			 * BitmapUtils
			 */
			try {
				URL url = new URL(imageUrl);// 参数是图片url
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				// 设置超时时间
				httpURLConnection.setConnectTimeout(3000);
				// 获取响应码
				int responseCode = httpURLConnection.getResponseCode();
				if (responseCode == 200) {
					// 流一旦使用后 就没了 里面就没东西了 如果转成了bitmap再拿流写文件 是没有内容的
					InputStream inputStream = httpURLConnection.getInputStream();
					// byte[] buf = new byte[1024];
					// int read = inputStream.read(buf);
					// MyLog.showLog("read::" + read);
					// inputStream 转换bitmap
					Bitmap decodeStreamBitmap = BitmapFactory.decodeStream(inputStream);
					String cacheFileName = MyMD5Encoder.encode(imageUrl) + ".jpg";// url+md5
					// 如果本地目录不存在，创建
					if (!cacheFileDir.exists()) {
						cacheFileDir.mkdirs();// 创建所有目录，包括子文件夹
					}
					File cacheFile = new File(cacheFileDir, cacheFileName);
					FileOutputStream stream = new FileOutputStream(cacheFile);
					String cacheDirPath = Environment.getExternalStorageDirectory() + "/exiu/cache/image/";
					MyPicUtils.saveFile(decodeStreamBitmap, cacheDirPath, cacheFileName, 80);
					String cacheFilePath = cacheDirPath + cacheFileName;
					// 文件下载后 添加扫描 让文件从图库中能被看到
					MyFileUtils.scanFileToPhotoAlbum(context, cacheFilePath);
					stream.close();
					Bitmap bitmap = BitmapFactory.decodeFile(cacheFilePath);
					// Bitmap scaledImage = MyPicUtils.getImage(context,
					// bitmap);
					// Bitmap roundCorner = MyPicUtils.toRoundCorner(bitmap,
					// 15);
					mLruCache.put(imageUrl, bitmap);
					
					// 获取Imageviewtag
					int tag = (Integer) imageView.getTag();
					
					if (currentPosition == tag) {
						ThreadUtil.runOnUIThread(new Runnable() {
							@Override
							public void run() {
								// 显示
								imageView.setImageBitmap(mLruCache.get(imageUrl));
								MyLog.showLog("主线程更新UI");
								// 设置内存中的图片
								// imageView.setImageBitmap(mLruCache.get(imageUrl));
							}
						});
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
