package com.friendstivals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.friendstivals.artist.LineUp;
import com.friendstivals.info.Info;
import com.friendstivals.playlist.Playlist;
import com.friendstivals.utils.TopButtonActions;

public class Extras extends Activity implements TopButtonActions{
	private String festivalId;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		festivalId = getIntent().getExtras().getString("festival_id");
		setContentView(R.layout.extras);		
	}
	
	public void openLineup(View v){
		Intent i = new Intent(this, LineUp.class);
		startActivity(i);
	}
	
	public void openPlaylist(View v){
		Intent i = new Intent(this, Playlist.class);
		startActivity(i);
	}
	
	public void openInfo(View v){
		Intent i = new Intent(this, Info.class);
		Bundle b = new Bundle();
		b.putString("festival_id", festivalId);
		i.putExtras(b);
		startActivity(i);
	} 
	
	public void openAnteriores(View v){
		
	}

	public void leftButtonClick(View v) {
		Intent i = new Intent(this, InviteView.class);
		startActivity(i);
	}

	public void rightButtonClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	public void openMap(View v){
		this.finish();
	}
	
	public void openExtras(View v){
	}

	public void openSettings(View v){
		Intent i = new Intent(getApplicationContext(), Settings.class);
		Bundle b = new Bundle();
		b.putString("festival_id", festivalId);
		i.putExtras(b);
		startActivity(i);
		finish();
	}

	public void openFestivalList(View v){
		Intent i = new Intent(getApplicationContext(), FestivalSelector.class);
		startActivity(i);
		finish();
	}
}
