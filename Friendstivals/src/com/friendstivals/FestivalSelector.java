package com.friendstivals;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.friendstivals.gpservice.GPService;
import com.friendstivals.gpservice.GPService.LocalBinder;
import com.friendstivals.utils.Festival;

public class FestivalSelector extends Activity implements OnItemClickListener{
	private ListView listView1;
	protected ArrayList<Festival> eventos;
	/*Variables de control del servicio GPS */
	private GPService mBoundService;
	private Intent lock_intent = null;
	boolean mBound = false;

	//D = true si se esta haciendo debug por logCat, DT = true si se esta haciendo debug por toast
	private static final boolean D = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.festival_selector);
		//		ArrayList<F> eventos = new ArrayList<Event>();
		//		Event evento = new Event(null, null, null, null, null);
		//		eventos.add(evento);

		//		FestivalAdapter adapter = new FestivalAdapter(this,
		//                R.layout.festival_list_item, eventos);
		eventos= new ArrayList<Festival>();
		String[] fest_list = getResources().getStringArray(R.array.fests);
		for(int i=0; i<fest_list.length; i++){
			Festival f = new Festival(this, fest_list[i]);
			eventos.add(f);
		}
		FestivalAdapter adapter = new FestivalAdapter(this,R.layout.festival_list_item, eventos);
		listView1 = (ListView)findViewById(R.id.list_festival);
		//		View header = (View)getLayoutInflater().inflate(R.layout.festival_list_item, null);
		//		listView1.addHeaderView(header);
		listView1.setAdapter(adapter);
		listView1.setOnItemClickListener(this);

		/*Iniciado del servicio GPS */
		lock_intent = new Intent(this, GPService.class);
		startService(lock_intent);
	}


	/**
	 * Debe redirigir a una Activity con el mapa del festival.
	 * Los datos del festival se pasan como Bundle.
	 */
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		Intent splashIntent = new Intent(getApplicationContext(), SplashScreen.class);
		Bundle mapBundle = new Bundle();
		Festival f = eventos.get(position);
		mapBundle.putString("festival_id", f.getId());
		splashIntent.putExtras(mapBundle);
		startActivity(splashIntent);
	}

	public void onStart(){
		super.onStart();

		Intent intent = new Intent(this, GPService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

	}

	public void onStop(){
		super.onStop();
		if(D) Log.println(Log.DEBUG, "FESTIVAL_SELECTOR", "Stoping...");
		if (mBound) {
			mBoundService.stopGPS();
			unbindService(mConnection);
			mBound = false;
		}
		if(D) Log.println(Log.DEBUG, "FESTIVAL_SELECTOR", "Servicio desligado");
	}

	public void onDestroy(){
		super.onDestroy();
		if(D) Log.println(Log.DEBUG, "FESTIVAL_SELECTOR", "Destroying...");
		mBoundService.stopService(lock_intent);
		if(D) Log.println(Log.DEBUG, "FESTIVAL_SELECTOR", "Servicio GPS detenido");

	}

	/**
	 * Iniciar conexion al servicio.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className,IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mBoundService = binder.getService();
			mBoundService.startGPS();
			mBound = true;
		}

		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}

	};
}
