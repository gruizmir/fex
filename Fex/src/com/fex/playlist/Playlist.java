package com.fex.playlist;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.fex.R;
import com.fex.artist.Artist;
import com.fex.artist.ArtistAdapter;
import com.fex.utils.TopButtonActions;

public class Playlist extends ListActivity implements TopButtonActions{
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
