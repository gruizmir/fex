package com.friendstivals;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.friendstivals.utils.Event;

public class FestivalSelector extends Activity implements OnItemClickListener{
	private ListView listView1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
		listView1.setOnItemClickListener(this);
	}
	

	/**
	 * Debe redirigir a una Activity con el mapa del festival.
	 * Los datos del festival se pasan como Bundle.
	 */
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		Intent mapIntent = new Intent(getApplicationContext(), CustomMap.class);
		Bundle mapBundle = new Bundle();
		mapBundle.putString("festival_name", "maquinaria");
		mapBundle.putString("festival_key", "key");
		mapIntent.putExtras(mapBundle);
		startActivity(mapIntent);
	}
}
