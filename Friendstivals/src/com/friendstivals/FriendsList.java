package com.friendstivals;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;

public class FriendsList extends ListActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
