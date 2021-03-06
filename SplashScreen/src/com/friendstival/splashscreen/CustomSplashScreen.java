package com.friendstival.splashscreen;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

public class CustomSplashScreen extends Activity {

	int SPLASH_DISPLAY_LENGTH = 2500;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Handler handler = new Handler();
		handler.postDelayed(getRunnableStartApp(), SPLASH_DISPLAY_LENGTH);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private Runnable getRunnableStartApp(){
		Runnable runnable = new Runnable(){
			public void run(){
				finish();
			}
		};
		return runnable;
	}
}
