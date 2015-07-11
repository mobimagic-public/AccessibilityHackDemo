package com.mobimagic.littlerabbit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;

public class CheckingStatusService extends Service {
	
	private MessageHandler mHandler;
	private static Context mContext;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mHandler = new MessageHandler();
		mContext = getApplicationContext();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					boolean isEnable = SharePref.getBoolean(mContext, SharePref.SHAREPREF_IS_ENABLE, false);
					if (Utils.isMyAccessibilitySettingEnable(mContext) && !isEnable) {
                        mHandler.sendEmptyMessage(ACCESSIBILITY_ENABLED);
					}else if (!Utils.isMyAccessibilitySettingEnable(mContext) && isEnable) {
						mHandler.sendEmptyMessage(ACCESSIBILITY_DISABLE);
					}
				
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	private static final int ACCESSIBILITY_ENABLED = 1;
	private static final int ACCESSIBILITY_DISABLE = 2;
	private static View mSettingsFloatView;
	private static WindowManager.LayoutParams params;
	private static WindowManager wm;
	
	private final static class MessageHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case ACCESSIBILITY_ENABLED:
				SharePref.setBoolean(mContext, SharePref.SHAREPREF_IS_ENABLE, true);
				wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
				mSettingsFloatView = View.inflate(mContext, R.layout.mask, null);
		        params = new WindowManager.LayoutParams();
		        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
		                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
		        params.format = PixelFormat.RGBA_8888;
		        wm.addView(mSettingsFloatView, params);
		        SharePref.setBoolean(mContext, SharePref.SAHREPREF_IS_BACKGROUND, true);
		        
		        new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						wm.removeView(mSettingsFloatView);
						SharePref.setBoolean(mContext, SharePref.SAHREPREF_IS_BACKGROUND, false);
					}
				}).start();
		        
				Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
			    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
				break;
			case ACCESSIBILITY_DISABLE:
				SharePref.setBoolean(mContext, SharePref.SHAREPREF_IS_ENABLE, false);			
				break;
			default:
				break;
			}
		}	
	}
}
