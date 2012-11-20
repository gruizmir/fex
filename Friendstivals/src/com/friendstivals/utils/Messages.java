package com.friendstivals.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.friendstivals.R;

public class Messages extends Activity{
	Intent senderIntent;
	Intent dialogIntent;
	LinearLayout messagesList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	 {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL, LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        this.setContentView(R.layout.user_messages_dialog);
        this.setTitle("Custom Dialog");
        senderIntent = getIntent();
        
        messagesList = (LinearLayout) findViewById(R.id.messagesList);
        
        /**
         * TEST
         */
        LinearLayout message = new LinearLayout(this);
        LayoutInflater.from(this.getBaseContext()).inflate(R.layout.user_message, message);
        
        messagesList.addView(message);
	 }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (MotionEvent.ACTION_OUTSIDE == event.getActionMasked()) {
			finish();
		    return true;
		    }
		return super.onTouchEvent(event);
	  }
	
	public void buttonOk(View v) {
		this.setResult(Activity.RESULT_OK, this.dialogIntent);
		this.finish();
	}
	
	public void buttonCancel(View v){
    	this.setResult(Activity.RESULT_CANCELED, this.dialogIntent);
		this.finish();
	}
	
	@Override
    public void onBackPressed() {
		super.onBackPressed();
    	this.setResult(Activity.RESULT_CANCELED, this.dialogIntent);	
        this.finish();
    }	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}