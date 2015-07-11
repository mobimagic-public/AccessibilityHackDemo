package com.mobimagic.littlerabbit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	private TextView mTextView;
	private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mTextView = (TextView) findViewById(R.id.textview);
        mBtn = (Button) findViewById(R.id.btn);
        
        mBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(
						Settings.ACTION_ACCESSIBILITY_SETTINGS);
				startActivity(intent);
			}
		});
        

    }
    
    @Override
    protected void onResume(){ 	
    	super.onResume();
		if (Utils.isMyAccessibilitySettingEnable(getApplicationContext())) {
			mBtn.setVisibility(View.GONE);
			mTextView.setText("Thx, I'll try my best to protect you!!!");
		} else {
			mBtn.setVisibility(View.VISIBLE);
		}
		StringBuffer stringBuffer = getContentFromFile();
        if (stringBuffer != null){
        	mTextView.setText("Hi, I wanna share something with you :P \n\n"+stringBuffer.toString());
        }
		Intent serviceIntent = new Intent(getApplicationContext(),
				CheckingStatusService.class);
		startService(serviceIntent);
    }
    
	private StringBuffer getContentFromFile(){
    	try {  
    	    File file = new File(Environment.getExternalStorageDirectory(),  
    	            Utils.SD_FILE_NAME);  
    	    BufferedReader retStringBuffer = new BufferedReader(new FileReader(file));  
    	    String readline = "";  
    	    StringBuffer sb = new StringBuffer();  
    	    while ((readline = retStringBuffer.readLine()) != null) {    
    	        sb.append(readline+"\n");  
    	    }  
    	    retStringBuffer.close();  
    	    return sb; 
    	} catch (Exception e) {  
    	    e.printStackTrace();  
    	}
    	
    	return null;
    }
}
