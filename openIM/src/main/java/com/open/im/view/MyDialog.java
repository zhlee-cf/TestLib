package com.open.im.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.widget.ImageView;

import com.open.im.R;

public class MyDialog extends Dialog {

	public MyDialog(Context context) {
		super(context, R.style.CustomProgressDialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);

		final ImageView imageView = (ImageView) findViewById(R.id.loadingImageView);
		Looper.myQueue().addIdleHandler(new IdleHandler() {
			@Override
			public boolean queueIdle() {
				AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
				animationDrawable.start();
				return false;
			}
		});

	}
}
