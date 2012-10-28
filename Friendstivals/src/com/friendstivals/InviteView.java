package com.friendstivals;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.LinearLayout;

public class InviteView extends Activity {
	
	LinearLayout customList;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.festival_invite_list);
		customList = (LinearLayout)findViewById(R.id.customInviteList);
		populateList(5);
		
		
	}
	
	public void populateList(int cantidad){
		LinearLayout newItem;
		for(int i=0;i<cantidad;i++){
			newItem = new LinearLayout(this);
			LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.festival_invite_item, newItem);
			customList.addView(newItem);
		}
	}

}
