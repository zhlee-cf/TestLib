package com.open.im.utils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.media.MediaRecorder;
import android.os.Environment;

/**
 * 录音用工具类
 * 
 * @author Administrator
 * 
 */
public class MyAudioRecordUtils {
	/**
	 * 录音文件保存路径
	 */
	public static String filePath = Environment.getExternalStorageDirectory() + "/exiu/cache/audio/";
	private static MediaRecorder recorder = new MediaRecorder();;

	/**
	 * 方法 开始录音
	 * 
	 * @return
	 */
	public static String startRecord() {
		String fileName = new Date().getTime() + ".amr";
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		recorder.reset();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 从麦克风采集声音
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		// 内容输出格式
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		// 音频编码方式

		recorder.setOutputFile(filePath + fileName);// 记住开SD权限
		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 预期准备
		recorder.start(); // 开始刻录
		MyLog.showLog("开始录音");
		return filePath + fileName;
	}

	/**
	 * 方法 停止录音
	 */
	public static void stopRecord() {
		if (recorder != null) {
			recorder.stop();
			MyLog.showLog("停止录音");
			recorder.reset();
		}
	}

	public static double getAmplitude() {
		if (recorder != null)
			return (recorder.getMaxAmplitude() / 2700.0);
		else
			return 0;
	}
}
