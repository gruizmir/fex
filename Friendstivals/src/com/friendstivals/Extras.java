package com.friendstivals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.friendstivals.artist.LineUp;
import com.friendstivals.playlist.Playlist;

public class Extras extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
		startActivity(i);
	} 
	
	public void openAnteriores(View v){
		
	}
}