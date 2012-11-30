package com.friendstivals.artist;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.friendstivals.R;
import com.friendstivals.utils.TopButtonActions;

public class LineUp extends ListActivity implements TopButtonActions{
	ArrayList<Artist> artistList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lineup);
		artistList = new ArrayList<Artist>();
		String[] lista = this.getResources().getStringArray(R.array.lineup_mysteryland);
		for(int i=0; i<lista.length; i++){
			Artist a = new Artist();
			a.setId(i);
			a.setName(lista[i]);
			artistList.add(a);
		}
		setListAdapter(new ArtistAdapter(getApplicationContext(), artistList));
	}
	
	public void leftButtonClick(View v) {
		finish();
	}

	public void rightButtonClick(View v) {
	}	

	
}
