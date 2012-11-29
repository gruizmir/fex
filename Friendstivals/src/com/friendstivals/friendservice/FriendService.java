package com.friendstivals.friendservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.friendstivals.R;
import com.friendstivals.utils.Utility;

public class FriendService extends Service{

	/*
	 * Variables
	 */
	private final IBinder mBinder = new LocalBinder();
	private HttpClient httpclient;
	private HttpPost httppost;
	private Handler handlerUpdateFriendsPosition;
	
	/*
	 * Estaticas de control
	 */
	//Tiempo con el cual se obtiene la posicion de los amigos
	private static final int TIME_UPDATE_POSITION = 1000 * 60 * 1;
	//Direccion para realizar la conexion
	private static final String DIRECCION = "http://23.23.170.228/save.php?action=getloc";
	//D = true si se esta haciendo debug por logCat, DT = true si se esta haciendo debug por toast
	private static final boolean D = true;

	public class LocalBinder extends Binder {
		public FriendService getService() {
			return FriendService.this;
		}
	}

	@Override
	public void onCreate() {
		/*
		 * SECCION DE CONEXION AL SERVIDOR.
		 */
		handlerUpdateFriendsPosition = new Handler();
		Runnable r = new Runnable(){

			public void run() {
				ConexionToServer  conexionToServer  = new ConexionToServer();
				conexionToServer.execute(DIRECCION);
				handlerUpdateFriendsPosition.postDelayed(this, TIME_UPDATE_POSITION);
			}

		};
		handlerUpdateFriendsPosition.postDelayed(r, 1000);
		/*
		 * FIN DE SECCION DE CONEXION
		 */
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(D) Log.i(getString(R.string.friends_service), "Received start id " + startId + ": " + intent);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if(D) Log.println(Log.DEBUG, getString(R.string.friends_service), "Deteniendo FriendService");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private class ConexionToServer extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... sUrl) {
			try {
				if(D) Log.println(Log.DEBUG, "CONEXION_TO_SERVER_FS", "Iniciando conexion");
				httpclient = new DefaultHttpClient();
				httppost = new HttpPost(sUrl[0]);
				if(D) Log.println(Log.DEBUG, "CONEXION_TO_SERVER_FS", "Conexion lograda");
				if(httpclient != null && httppost != null && Utility.userUID != null && !Utility.userUID.equals("")){
					try {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair("fbid", Utility.userUID));
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						
						HttpResponse response = httpclient.execute(httppost);
						
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
						String sResponse;
						StringBuilder s = new StringBuilder();
						while ((sResponse = reader.readLine()) != null) 
							s = s.append(sResponse);
						
						if(D) Log.println(Log.DEBUG, "GET_FRIENDS", s.toString());
						
						
					} catch (ClientProtocolException e) {
						if(D) Log.println(Log.DEBUG, "GET_FRIENDS" , "Error de protocolo");
						e.printStackTrace();
					} catch (IOException e) {
						if(D) Log.println(Log.DEBUG, "GET_FRIENDS" , "I/O error");
						e.printStackTrace();
					}

				}
			} catch (Exception e) {
				if(D) Log.println(Log.DEBUG, "CONEXION_TO_SERVER_FS" , "No se puede conectar al servidor");
			}
			return null;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
		}

		@Override
		protected void onPostExecute(String result) {
		}
	}


}
