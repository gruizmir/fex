package com.fex.playlist;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

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
		TextView txt = (TextView) findViewById(R.id.groovelist);
		txt.setText("Ve la lista directamente en Grooveshark!");
		txt.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://grooveshark.com/#!/playlist/Mysteryland+Chile+2012/80189014"));
				startActivity(myIntent);
			}
		});
		setListAdapter(new ArtistAdapter(getApplicationContext(), songList));
	}

	public void leftButtonClick(View v) {
		finish();
	}

	public void rightButtonClick(View v) {
	}
}
