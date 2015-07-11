package com.mobimagic.littlerabbit;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePref {
	
	public static String SHAREPREF_NAME = "myShareP";
	public static String SHAREPREF_IS_ENABLE = "is_enable";
	public static String SAHREPREF_IS_BACKGROUND = "is_background";
	
    public static boolean getBoolean(Context context, String key, boolean defValue){
    	boolean ret = false;
    	SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREPREF_NAME, 0);
    	ret = sharedPreferences.getBoolean(key, defValue);
    	return ret;
    }
    
    public static void setBoolean(Context context, String key, boolean value){
    	SharedPreferences settings = context.getSharedPreferences(SHAREPREF_NAME, 0);
		SharedPreferences.Editor localEditor = settings.edit();
		localEditor.putBoolean(key, value);
		localEditor.commit();	
    }
}
