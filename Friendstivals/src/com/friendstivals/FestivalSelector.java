package com.friendstivals;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;

public class FestivalSelector extends ListActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.festival_selector);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
