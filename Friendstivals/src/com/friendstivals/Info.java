package com.friendstivals;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.friendstivals.utils.FriendsViewActions;

public class Info extends Activity implements FriendsViewActions{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.info);
	}

	public void leftButtonClick(View v) {
		finish();
	}

	public void rightButtonClick(View v) {
		
	}

}
