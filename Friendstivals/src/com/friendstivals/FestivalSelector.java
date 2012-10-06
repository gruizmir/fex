package com.friendstivals;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

public class FestivalSelector extends ListActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.festival_selector);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View festivalView;
        
        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        festivalView = inflater.inflate(R.layout.festival_list_item, null);
        LinearLayout festivalItem = (LinearLayout)festivalView.findViewById(R.id.team_name);

        this.addContentView(festivalItem, null);
    }
}
