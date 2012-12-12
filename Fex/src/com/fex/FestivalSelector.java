package com.fex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;
import com.fex.friendservice.FriendService;
import com.fex.gpservice.GPService;
import com.fex.gpservice.GPService.LocalBinder;
import com.fex.utils.Festival;
import com.fex.utils.SessionStore;
import com.fex.utils.Utility;

@SuppressLint("HandlerLeak")
public class FestivalSelector extends Activity implements OnItemClickListener{
	private ListView listView1;
	protected ArrayList<Festival> eventos;
	/*Variables de control del servicio GPS */
	private GPService mBoundService;
	private Intent lock_intent = null;
	boolean mBound = false;
	/*Variables de control del servicio de amigos*/
	private FriendService mBoudServiceFriends;
	private Intent lock_intent_friends = null;
	boolean mBoundFriends = false;
	
	private int LOGOUT = 1;
	//D = true si se esta haciendo debug por logCat, DT = true si se esta haciendo debug por toast
	private static final boolean D = false;
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == LOGOUT){
				loggedOut();				
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.festival_selector);
		eventos= new ArrayList<Festival>();
		String[] fest_list = getResources().getStringArray(R.array.fests);
		for(int i=0; i<fest_list.length; i++){
			Festival f = new Festival(this, fest_list[i]);
			eventos.add(f);
		}
		FestivalAdapter adapter = new FestivalAdapter(this,R.layout.festival_list_item, eventos);
		listView1 = (ListView)findViewById(R.id.list_festival);
		listView1.setAdapter(adapter);
		listView1.setOnItemClickListener(this);

		/*Iniciado del servicio GPS */
		lock_intent = new Intent(this, GPService.class);
		startService(lock_intent);
		
		/*Iniciado del servicio GPS */
		lock_intent_friends = new Intent(this, FriendService.class);
		startService(lock_intent_friends);
	}


	/**
	 * Debe redirigir a una Activity con el mapa del festival.
	 * Los datos del festival se pasan como Bundle.
	 */
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		Intent splashIntent = new Intent(getApplicationContext(), SplashScreen.class);
		Bundle mapBundle = new Bundle();
		Festival f = eventos.get(position);
		if(f.isActive()){
			mapBundle.putString("festival_id", f.getId());
			splashIntent.putExtras(mapBundle);
			startActivity(splashIntent);
		}
	}

	public void onStart(){
		super.onStart();

		Intent intent = new Intent(this, GPService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		intent = new Intent(this, FriendService.class);
		bindService(intent,mConnectionFriends, Context.BIND_AUTO_CREATE);
	}

	public void onStop(){
		super.onStop();
		if(D) Log.println(Log.DEBUG, "FESTIVAL_SELECTOR", "Stoping...");
		if (mBound) {
			mBoundService.stopGPS();
			unbindService(mConnection);
			mBound = false;
		}
		if(mBoundFriends){
			unbindService(mConnectionFriends);
			mBoundFriends = false;
		}
		if(D) Log.println(Log.DEBUG, "FESTIVAL_SELECTOR", "Servicios desligado");
	}

	public void onDestroy(){
		super.onDestroy();
		if(D) Log.println(Log.DEBUG, "FESTIVAL_SELECTOR", "Destroying...");
		mBoundService.stopService(lock_intent);
		mBoudServiceFriends.stopService(lock_intent_friends);
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
	
	/**
	 * Iniciar conexion al servicio friends
	 */
	private ServiceConnection mConnectionFriends = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
			FriendService.LocalBinder binder = (FriendService.LocalBinder) service;
			mBoudServiceFriends = binder.getService();
			mBoundFriends = true;
		}

		public void onServiceDisconnected(ComponentName name) {
			mBoundFriends = false;
		}
		
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.getItem(0).setTitle("Logout");
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.menu_logout:
			Utility.mAsyncRunner.logout(getBaseContext(), new RequestListener() {
				public void onComplete(String response, Object state) {
					SessionStore.clear(getApplicationContext());
					mHandler.sendEmptyMessage(LOGOUT);
				}

				public void onIOException(IOException e, Object state) {}

				public void onFileNotFoundException(FileNotFoundException e,
						Object state) {}

				public void onMalformedURLException(MalformedURLException e,
						Object state) {}

				public void onFacebookError(FacebookError e, Object state) {}
			});
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void loggedOut(){
		SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.clear();
		ed.commit();
		
		pref = getSharedPreferences("settings", MODE_PRIVATE);
		ed = pref.edit();
		ed.clear();
		ed.commit();
		
		Toast.makeText(getApplicationContext(), "Se ha deslogueado correctamente", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this, Fex.class);
		startActivity(i);
		finish();
	}
}
