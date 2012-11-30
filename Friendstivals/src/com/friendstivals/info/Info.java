package com.friendstivals.info;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.friendstivals.LocationMap;
import com.friendstivals.R;
import com.friendstivals.utils.Festival;
import com.friendstivals.utils.TopButtonActions;

public class Info extends Activity implements TopButtonActions{
	private String festivalId;
	private Festival fest;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		festivalId = getIntent().getExtras().getString("festival_id");
		fest = new Festival(this, festivalId);
		setContentView(R.layout.info);
	}

	public void leftButtonClick(View v) {
		finish();
	}

	public void rightButtonClick(View v) {
		
	}
	
	public void showDates(View v){
		Intent i = new Intent(this, InfoDates.class);
		Bundle b = new Bundle();
		b.putString("festival_id", festivalId);
		i.putExtras(b);
		startActivity(i);
	}
	
	public void showTickets(View v){
		Intent i = new Intent(this, InfoTickets.class);
		Bundle b = new Bundle();
		b.putString("festival_id", festivalId);
		i.putExtras(b);
		startActivity(i);
	}
	
	public void showLocation(View v){
		Intent i = new Intent(getApplicationContext(), LocationMap.class);
		Bundle b = new Bundle();
		b.putString("address", fest.getLocation() + ", " + fest.getGenLocation());
		b.putString("festival_name", fest.getName());
		i.putExtras(b);
		startActivity(i);
	}
}