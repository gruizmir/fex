package com.friendstivals;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.LinearLayout;

public class FestivalSelector extends ListActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.festival_selector);
        
        //LayoutInflater factory = LayoutInflater.from(this.getApplicationContext()); 
        //LinearLayout festivalView = (LinearLayout)factory.inflate( R.layout.festival_list_item, null);
        
        //this.setListAdapter(adapter);
    }
}
