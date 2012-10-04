package com.friendstivals;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class Settings extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
