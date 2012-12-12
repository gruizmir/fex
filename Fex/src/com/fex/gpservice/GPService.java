package com.fex.gpservice;

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
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.fex.R;
import com.fex.utils.Utility;

public class GPService extends Service implements LocationListener{

	/*
	 * Variables
	 */
	private final IBinder mBinder = new LocalBinder();
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Handler handlerUpdatePosition;
	private HttpClient httpclient;
	private HttpPost httppost;
	//Mejor posicion guardada en GPService
	private Location bestLocation;

	/*
	 * Estaticas de control
	 */
	//Tiempo de sobre escritura de bestLocation si es que no se a sobre escrito en TWO_MINUTES.
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	//Tiempo con el cual se actulizara la posicion en el servidor.
	private static final int TIME_UPDATE_POSITION = 1000 * 60 * 1;
	//Direccion para realizar la conexion
	private static final String DIRECCION = "http://23.23.170.228/save.php?action=updateloc";
	//D = true si se esta haciendo debug por logCat, DT = true si se esta haciendo debug por toast
	private static final boolean D = false;
	private static final boolean DT = false;

	public class LocalBinder extends Binder {
		public GPService getService() {
			return GPService.this;
		}
	}

	@Override
	public void onCreate() {
		if(D) Log.println(Log.DEBUG, getString(R.string.gps_service), "onCreate GPService");
		bestLocation = null;
		/*
		 * SECCION DE CONEXION AL SERVIDOR.
		 */
		handlerUpdatePosition = new Handler();
		Runnable r = new Runnable(){

			public void run() {
				ConexionToServer  conexionToServer  = new ConexionToServer();
				conexionToServer.execute(DIRECCION);
				handlerUpdatePosition.postDelayed(this, TIME_UPDATE_POSITION);
			}

		};
		handlerUpdatePosition.postDelayed(r, 1000);
		/*
		 * FIN DE SECCION DE CONEXION
		 */

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(D) Log.i(getString(R.string.gps_service), "Received start id " + startId + ": " + intent);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if(D) Log.println(Log.DEBUG, getString(R.string.gps_service), "Deteniendo GPService");

		if(locationManager != null && locationListener != null){
			locationManager.removeUpdates(locationListener);
		}
		if(handlerUpdatePosition != null){
			handlerUpdatePosition.removeCallbacksAndMessages(null);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
	 * getLocation retorna la mejor posicion disponible
	 * @return : Se retorna la posicion si es posible, null en caso contrario.
	 */
	public Location getLocation(){
		return bestLocation;
	}

	/**
	 * startGPS funcion encargada de iniciar la obtencion de la posicion del usuario.
	 */
	public void startGPS(){
		if(D) Log.println(Log.DEBUG, getString(R.string.gps_service), "Start GPS");

		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationListener = this;

		if(isBetterLocation(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER),
				locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)))
		{
			bestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		else
		{
			bestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}

//		if(bestLocation!=null && D){
//			Log.println(Log.DEBUG, "GPS_LISTENER", "Latitud" + bestLocation.getLatitude());
//			Log.println(Log.DEBUG, "GPS_LISTENER", "Longitud" + bestLocation.getLongitude());
//		}

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

	/**
	 * stopGPS funcion encargada de detener la obtencion de posicion.
	 */
	public void stopGPS(){
		locationManager.removeUpdates(locationListener);
	}

	/*
	 * INICIO LOCATIONLISTENER
	 */
	public void onLocationChanged(Location location) {

		if(isBetterLocation(location, bestLocation)){
			bestLocation = location;
			if(D) Log.println(Log.DEBUG, "GPS_NETWORK_LISTENER", "Provider" + bestLocation.getProvider());
			if(D) Log.println(Log.DEBUG, "GPS_NETWORK_LISTENER", "Latitud" + bestLocation.getLatitude());
			if(D) Log.println(Log.DEBUG, "GPS_NETWORK_LISTENER", "Longitud" + bestLocation.getLongitude());

			if(DT) Toast.makeText(getBaseContext(), 
					"GPS_NETWORK_LISTENER | PROVIDER | " + 
							bestLocation.getProvider() +
							"\nGPS_NETWORK_LISTENER | LONGITUD | " +
							bestLocation.getLongitude() +
							"\nGPS_NETWORK_LISTENER | LATITUD | " +
							bestLocation.getLatitude(),Toast.LENGTH_LONG
					).show();
		}

	}

	public void onProviderDisabled(String provider) {
		if(D) Log.println(Log.DEBUG, "GPS_NETWORK_LISTENER", "Provider " + provider + " DISABLED");

	}

	public void onProviderEnabled(String provider) {
		if(D) Log.println(Log.DEBUG, "GPS_NETWORK_LISTENER", "Provider " + provider + " ENABLED");
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {

	}
	/*
	 * FIN LOCATIONLISTENER
	 */

	/**
	 * Funcion que determina si una nueva posicion es mejor que la anterior segun algunos
	 * criterios.
	 * @param location : Nueva poscion
	 * @param currentBestLocation : Mejor posicion actual
	 * @return : true si es mejor la nueva posicion, false si la poscion actual es mejor.
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			return true;
		}

		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		if (isSignificantlyNewer) {
			return true;
		} else if (isSignificantlyOlder) {
			return false;
		}

		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	/**
	 * Se inicia la conexion con el servidor.
	 * @author astom
	 *
	 */
	private class ConexionToServer extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... sUrl) {
			try {
				if(D) Log.println(Log.DEBUG, "CONEXION_TO_SERVER", "Iniciando conexion");
				httpclient = new DefaultHttpClient();
				httppost = new HttpPost(sUrl[0]);
				if(D) Log.println(Log.DEBUG, "CONEXION_TO_SERVER", "Conexion lograda");
				if(httpclient != null && httppost != null && bestLocation != null){
					try {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
						nameValuePairs.add(new BasicNameValuePair("fbid", Utility.userUID));
						nameValuePairs.add(new BasicNameValuePair("latitude", bestLocation.getLatitude()+""));
						nameValuePairs.add(new BasicNameValuePair("longitude", bestLocation.getLongitude()+""));
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

						HttpResponse response = httpclient.execute(httppost);
						
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
						String sResponse;
						StringBuilder s = new StringBuilder();
						while ((sResponse = reader.readLine()) != null) 
							s = s.append(sResponse);
						
						if(D) Log.println(Log.DEBUG, "UPDATE_POSITION", s.toString());	
						if(D) Log.println(Log.DEBUG, "UPDATE_POSITION", "Posicion actualizada para el usuario " + Utility.userUID);
					} catch (ClientProtocolException e) {
						if(D) Log.println(Log.DEBUG, "UPDATE_POSITION" , "Error de protocolo");
						e.printStackTrace();
					} catch (IOException e) {
						if(D) Log.println(Log.DEBUG, "UPDATE_POSITION" , "I/O error");
						e.printStackTrace();
					} catch(NullPointerException e){
						if(D) Log.println(Log.DEBUG, "UPDATE_POSITION" , "NullPointer");
						e.printStackTrace();
					}
				}
				else{
					if(D) Log.println(Log.DEBUG, "UPDATE_POSITION", "Error al intentar hacer update");
				}
			} catch (Exception e) {
				if(D) Log.println(Log.DEBUG, "CONEXION_TO_SERVER" , "No se puede conectar al servidor");
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
