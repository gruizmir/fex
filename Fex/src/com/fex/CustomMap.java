package com.fex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.MBTilesFileArchive;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.fex.utils.BaseButtonActions;
import com.fex.utils.BaseRequestListener;
import com.fex.utils.TopButtonActions;
import com.fex.utils.Utility;

@SuppressLint("HandlerLeak")
public class CustomMap extends Activity implements TopButtonActions, BaseButtonActions{
	private String festivalId;
	private ArrayList<String> ids;
	private static final int GET_POSITIONS=5;
	private static final int SEARCH=4;
	private static final int MESSAGES=3;
	private static final int GET_NAME=6;
	private ArrayList<OverlayItem> itemsOverlay;
	private Handler handlerUpdateFriendsPosition;
	private ProgressDialog progressDialog;
	private RelativeLayout mainLayout;
	private SimpleRegisterReceiver simpleReceiver;
	protected int minId;
	private MapView mMapView;
	private ResourceProxy mResourceProxy;
	private ItemizedOverlay<OverlayItem> mMyLocationOverlay;
	//Seteo inicial del mapa.
	private int MIN_ZOOM = 16;
	private int MAX_ZOOM = 18;
	private double CENTER_LAT = -33.9644;
	private double CENTER_LON = -70.6273;
	private String MAP_NAME = "mysteryland.mbtiles";
	//Tiempo con el cual se obtiene la posicion de los amigos
	private static final int TIME_UPDATE_POSITION = 1000 * 60 * 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		festivalId = getIntent().getExtras().getString("festival_id");
		itemsOverlay = new ArrayList<OverlayItem>();
		setMapView();
		setContentView(R.layout.custom_map);
		configMapView();
	}

	/**
	 * handler encargado de recibir la informacion del servidor
	 */
	protected Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == GET_POSITIONS){
				getPositions();
			}
			if(msg.what == SEARCH){
				search(msg.getData().getString("position"), msg.getData().getString("name"));
			}
			if(msg.what == MESSAGES){
				showMessages(msg.getData().getString("messages"));
			}
			if(msg.what == GET_NAME){
				getName(msg.getData().getString("position"), msg.getData().getString("id"));
			}
		}
	};

	@Override
	protected void onResume(){
		super.onResume();
		//Declaracion del handler encargado de actualizar la posicion del usuario y amigos
		//cada cierto intervalo de tiempo.
		handlerUpdateFriendsPosition = new Handler();
		Runnable r = new Runnable(){
			public void run() {
				if(!itemsOverlay.isEmpty()){
					itemsOverlay.clear();
				}
				if(mMapView.getOverlays()!=null){
					mMapView.getOverlays().clear();
				}
				showFriendsInMap();
				handlerUpdateFriendsPosition.postDelayed(this, TIME_UPDATE_POSITION);
			}
		};
		handlerUpdateFriendsPosition.postDelayed(r, 1000);
	}

	@Override
	protected void onPause(){
		super.onPause();
		//Detencion del handler.
		handlerUpdateFriendsPosition.removeCallbacksAndMessages(null);
	}

	/**
	 * setMapView esta encargado de inicializar la vista con el mapa proviniente desde un archivo
	 * .mbtiles
	 */
	private void setMapView(){
		//Se define la procedencia del mapa, en este caso un archivo mbtile.
		XYTileSource MBTILESRENDER = new XYTileSource(
				"mbtiles", 
				ResourceProxy.string.offline_mode, 
				MIN_ZOOM, MAX_ZOOM, 
				256, ".png", ""
				);

		mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
		simpleReceiver = new SimpleRegisterReceiver(this);

		String path = Environment.getExternalStorageDirectory().getPath() + "/fex/maps/" + MAP_NAME;
		File mapa = new File(path);

		try {
			//En caso de que no se pueda leer el archivo desde la fuente inicial se copia a la 
			//memoria SD.
			if(!mapa.canRead()){
				path = Environment.getExternalStorageDirectory().getPath() + "/fex/maps/";
				File friendsFolder = new File(path);
				friendsFolder.mkdirs();
				Log.println(Log.DEBUG, "FileFolder", friendsFolder.getAbsolutePath());
				copyToSD(R.raw.mysteryland, friendsFolder.getAbsolutePath() + "/" + MAP_NAME);
				mapa = new File(Environment.getExternalStorageDirectory(), "/fex/maps/" + MAP_NAME);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		IArchiveFile[] files = {MBTilesFileArchive.getDatabaseFileArchive(mapa)}; 
		MapTileModuleProviderBase moduleProvider = new MapTileFileArchiveProvider(
				simpleReceiver, 
				MBTILESRENDER, 
				files
				);

		MapTileProviderArray mProvider = new MapTileProviderArray(
				MBTILESRENDER, 
				null, 
				new MapTileModuleProviderBase[]{ moduleProvider }
				);

		//Se inicializa la vista.
		this.mMapView =  new MapView(this, 256, mResourceProxy, mProvider);		
	}
	/**
	 * configMapView se encarga de configurar los aspectos basicos del MapView.
	 */
	private void configMapView(){
		mainLayout = (RelativeLayout) findViewById(R.id.maplayout);

		this.mMapView.setLayoutParams(new LinearLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT
				));

		mainLayout.addView(this.mMapView);

		this.mMapView.setBuiltInZoomControls(true);
		this.mMapView.setMultiTouchControls(true);
		this.mMapView.getController().setZoom(MIN_ZOOM);

		IGeoPoint point = new GeoPoint(CENTER_LAT, CENTER_LON);
		mMapView.getController().setCenter(point);
	}

	/**
	 * copyToSd funcion encarga de traspasar un archivo almacenado en la aplicacion a la memoria SD. 
	 * @param res : Recurso dentro de la aplicacion que quiere ser copiado
	 * @param path : Ruta donde sera copiado el archivo.	
	 * @throws Exception
	 */
	private void copyToSD(int res, String path) throws Exception{
		InputStream in = getResources().openRawResource(res);
		FileOutputStream out = new FileOutputStream(path);
		byte[] buff = new byte[1024];
		int read = 0;

		try {
			while ((read = in.read(buff)) > 0) {
				out.write(buff, 0, read);
			}
		} finally {
			in.close();
			out.close();
		}
	}

	public void openAcciones(View v){
		Intent i = new Intent(this, SendMessage.class);
		i.putExtras(getIntent());		
		startActivity(i);
	}

	public void openExtras(View v){
		Intent i = new Intent(getApplicationContext(), Extras.class);
		Bundle b = new Bundle();
		b.putString("festival_id", festivalId);
		i.putExtras(b);
		startActivity(i);
	}

	public void openSettings(View v){
		Intent i = new Intent(getApplicationContext(), Settings.class);
		Bundle b = new Bundle();
		b.putString("festival_id", festivalId);
		i.putExtras(b);
		startActivity(i);
	}

	public void openFestivalList(View v){
		this.finish();
	}

	public void sendMessageToFriends(){
		if (!Utility.mFacebook.isSessionValid()) {
			Util.showAlert(this, "Warning", "You must first log in.");
		} else {
			String query = "select name, current_location, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) and is_app_user='true' order by name";
			Bundle params = new Bundle();
			params.putString("method", "fql.query");
			params.putString("query", query);
			Utility.mAsyncRunner.request(null, params,
					new FriendsRequestListener());
		}
	}

	/*
	 * callback after friends are fetched via me/friends or fql query.
	 */
	public class FriendsRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			progressDialog.dismiss();
			Intent myIntent = new Intent(getApplicationContext(), FriendsList.class);
			myIntent.putExtra("API_RESPONSE", response);
			startActivity(myIntent);
		}

		public void onFacebookError(FacebookError error) {
			Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void showFriendsInMap(){
		try{
			if (!Utility.mFacebook.isSessionValid()) {
				Util.showAlert(this, "Warning", "You must first log in.");
			} else {
				SharedPreferences pref = this.getSharedPreferences("blocked", MODE_PRIVATE);
				String query;
				if(pref.contains("blocked_list_id"))
					query = "select uid from user where uid in (select uid2 from friend where uid1=me()) AND NOT (uid in (select uid, flid from friendlist_member where flid="+ pref.getString("blocked_list_id", null) + ")) AND is_app_user='true'";
				else
					query = "select uid from user where uid in (select uid2 from friend where uid1=me()) and is_app_user='true'";
				Bundle params = new Bundle();
				params.putString("method", "fql.query");
				params.putString("query", query);
				Utility.mAsyncRunner.request(null, params,
						new BaseRequestListener(){
					public void onComplete(final String response, final Object state) {
						ids = new ArrayList<String>();
						try {
							JSONArray jsonArray = new JSONArray(response);
							ids.add(Utility.userUID);
							for(int i=0; i<jsonArray.length(); i++){
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								ids.add(jsonObject.getString("uid"));   //Obtener la id de cada amigo
							}
							mHandler.sendEmptyMessage(GET_POSITIONS);
						} catch (JSONException e) {
							Log.e("json_fail", e.getMessage());
						}
					}
				});
			}
		}catch(NullPointerException e){
			Log.e("null", e.toString());
		}
	}

	private void getPositions(){
		Iterator<String> it = ids.iterator();
		while(it.hasNext()){
			String uid = it.next();
			ConexionToServer  conexionToServer  = new ConexionToServer(uid);
			conexionToServer.execute(getString(R.string.getLoc));
		}
	}

	/**
	 * search esta encargado de agregar los marcadores del usuario y de los amigos al mapa.
	 * @param direccion : posicion en que sera puesto el marcador.
	 */
	private void search(String direccion, String nombre){
		GeoPoint point;
		String[] pos=direccion.split(",");
		if(pos.length>0 && !pos[0].equals("") && !pos[0].equals(" ")){
			point = new GeoPoint((int)(Double.parseDouble(pos[0])*1000000),(int) (Double.parseDouble(pos[1])*1000000));
			OverlayItem nuevoItem = new OverlayItem("Here", "SampleDescription", point);
			nuevoItem.setMarker(this.getResources().getDrawable(R.drawable.boton_mapa));
			itemsOverlay.add(nuevoItem);

			mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(itemsOverlay,
					new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
				public boolean onItemSingleTapUp(final int index,
						final OverlayItem item) {
					// Agregar aqui que accion a realizar cuando se preciona uno de los marcadores
					// en el mapa.

					return true;
				}
				public boolean onItemLongPress(final int index,
						final OverlayItem item) {
					// Agregar aqui que accion a realizar cuando se mantiene precionado un marcador
					// en el mapa por un largo periodo de tiempo.

					return false;
				}
			}, mResourceProxy);

			if(mMapView.getOverlays() != null){
				mMapView.getOverlays().clear();
			}

			mMapView.getOverlays().add(mMyLocationOverlay);
			mMapView.refreshDrawableState();
			mMapView.invalidate();
		}
	}

	/**
	 * Se inicia la conexion con el servidor para ingresar a un nuevo usuario.
	 * @author astom
	 *
	 */
	private class ConexionToServer extends AsyncTask<String, Integer, String> {
		private String fbId;

		public ConexionToServer(String fbId){
			super();
			this.fbId = fbId;
		}

		@Override
		protected String doInBackground(String... sUrl) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(sUrl[0]);
				if(httpclient != null && httppost != null){
					try {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
						nameValuePairs.add(new BasicNameValuePair("fbid", this.fbId));
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = httpclient.execute(httppost);
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
						String sResponse;
						StringBuilder s = new StringBuilder();
						while ((sResponse = reader.readLine()) != null) 
							s = s.append(sResponse);
						if(s!=null && !s.toString().equals("ERROR") && s.toString().length()>0){
							Bundle b = new Bundle();
							b.putString("position",	s.toString());
							b.putString("id", this.fbId);
							Message m = new Message();
							m.setData(b);
							m.what = GET_NAME;
							mHandler.sendMessage(m);
						}
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
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

	public void leftButtonClick(View v) {
		Intent i = new Intent(getApplicationContext(), InviteView.class);
		startActivity(i);
	}

	public void rightButtonClick(View v) {		
		SharedPreferences pref = getSharedPreferences("messages", MODE_PRIVATE);
		minId = pref.getInt("last_got", 0);
		new Thread(new Runnable(){
			public void run(){
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(getString(R.string.get_messages));
				if(httpclient != null && httppost != null){
					try {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair("minid", Integer.toString(minId)));
						nameValuePairs.add(new BasicNameValuePair("receiver", Utility.userUID));
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = httpclient.execute(httppost);
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
						String sResponse;
						StringBuilder s = new StringBuilder();
						while ((sResponse = reader.readLine()) != null) 
							s = s.append(sResponse);
						Message m = new Message();
						Bundle data = new Bundle();
						if(s!=null)
							data.putString("messages", s.toString());
						else
							data.putString("messages", "");
						m.setData(data);
						m.what = MESSAGES;
						mHandler.sendMessage(m);

					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}				
			}
		}).start();


	}

	public void openMap(View v) {
		// TODO Auto-generated method stub
	}	

	private void showMessages(String resp){
		String[] filas = resp.split("</br>");
		Dialog mDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.setContentView(R.layout.bubble_dialog);
		LinearLayout customList = (LinearLayout)mDialog.findViewById(R.id.bubble_parent_layout);
		if(resp!=null && !resp.equals("ERROR") && resp.length()>0){
			for(int i=0; i<filas.length; i++){
				String[] row = filas[i].split("%%");
				TableLayout newItem = new TableLayout(this);
				LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.bubble_item, newItem);
				TextView txt = (TextView) newItem.findViewById(R.id.bubble_text);
				txt.setText(row[1]);
				customList.addView(newItem);
			}
		}
		else{
			TableLayout newItem = new TableLayout(this);
			LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.bubble_item, newItem);
			TextView txt = (TextView) newItem.findViewById(R.id.bubble_text);
			txt.setText("No tienes mensajes nuevos");
			customList.addView(newItem);
		}
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.show();
	}

	private void getName(final String position, String id){	
		try{
			if (!Utility.mFacebook.isSessionValid()) {
				Util.showAlert(this, "Warning", "You must first log in.");
			} else {
				String query = "select name from user where uid=" + id;
				Bundle params = new Bundle();
				params.putString("method", "fql.query");
				params.putString("query", query);
				Utility.mAsyncRunner.request(null, params,
						new BaseRequestListener(){
					public void onComplete(final String response, final Object state) {
						try {
							JSONArray jsonArray = new JSONArray(response);
							JSONObject jsonObject = jsonArray.getJSONObject(0);
							Message m = new Message();
							Bundle b = new Bundle();
							b.putString("name", jsonObject.getString("name"));
							b.putString("position", position);
							m.setData(b);
							m.what=SEARCH;
							mHandler.sendMessage(m);
						} catch (JSONException e) {
							Log.e("json_fail", e.getMessage());
						}
					}
				});
			}
		}catch(NullPointerException e){
			Log.e("getname", e.toString());
		}
	}
}
