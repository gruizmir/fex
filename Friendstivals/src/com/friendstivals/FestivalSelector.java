package com.friendstivals;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.friendstivals.utils.Event;

public class FestivalSelector extends Activity {

	private ListView listView1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.festival_selector);

		ArrayList<Event> eventos = new ArrayList<Event>();
		
		Event evento = new Event(null, null, null, null, null);
		
		eventos.add(evento);
		
		FestivalAdapter adapter = new FestivalAdapter(this,
                R.layout.festival_list_item, eventos);
		
		listView1 = (ListView)findViewById(R.id.list_festival);

		View header = (View)getLayoutInflater().inflate(R.layout.festival_list_item, null);
		listView1.addHeaderView(header);

		listView1.setAdapter(adapter);
	}
}
