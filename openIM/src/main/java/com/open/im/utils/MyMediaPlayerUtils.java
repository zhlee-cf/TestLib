package com.open.im.utils;

import java.io.IOException;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.widget.ImageView;

/**
 * 根据URL播放音频工具类
 * 
 * @author Administrator
 * 
 */
public class MyMediaPlayerUtils {

	private static MediaPlayer mediaPlayer;

	/**
	 * 方法 播放音频
	 * 
	 * @param context 
	 * @param url 声音的url
	 * @param sendAudio  声音播放时做动画的那个iv
	 */
	public static void play(Context context, String url, ImageView sendAudio) {

		final AnimationDrawable an = (AnimationDrawable) sendAudio.getDrawable();

		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
		}
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.reset();// 重置为初始状态
			MyLog.showLog("手动停止播放");
			// 停止动画
			an.stop();
			an.selectDrawable(2);
		} else {
			// mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			try {
				mediaPlayer.setDataSource(context, Uri.parse(url));
				// mediaPlayer.prepare();
				mediaPlayer.prepareAsync();
				/**
				 * 异步加载好了会回调到此方法
				 */
				mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer mp) {
						mediaPlayer.start();
						// 开启动画
						an.start();
						MyLog.showLog("开始播放语音");
					}
				});

				/**
				 * 正常播放完成会回调到此方法
				 */
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						MyLog.showLog("播放完成");
						mediaPlayer.reset();
						// 释放的话  首次播放完成后，第二次播放在判断是否正在播放中会报异常
//						mediaPlayer.release();
						// 播放完成停止动画 让动画保存初始化时的状态
						an.stop();
						an.selectDrawable(2);
					}
				});

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
