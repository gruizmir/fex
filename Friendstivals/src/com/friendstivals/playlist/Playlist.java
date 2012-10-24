package com.friendstivals.playlist;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.friendstivals.R;
import com.friendstivals.artist.Artist;
import com.friendstivals.artist.ArtistAdapter;
import com.friendstivals.utils.FriendsViewActions;

public class Playlist extends ListActivity implements FriendsViewActions{
	ArrayList<Artist> songList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.playlist);
		songList = null;
		setListAdapter(new ArtistAdapter(getApplicationContext(), songList));
	}

	public void leftButtonClick(View v) {
		finish();
	}

	public void rightButtonClick(View v) {
	}
}
