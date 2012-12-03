package com.friendstivals;

import java.io.BufferedReader;
import java.io.IOException;
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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.friendstivals.utils.BaseButtonActions;
import com.friendstivals.utils.BaseRequestListener;
import com.friendstivals.utils.TopButtonActions;
import com.friendstivals.utils.Utility;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

@SuppressLint("HandlerLeak")
public class CustomMap extends MapActivity implements TopButtonActions, BaseButtonActions{
	private String festivalId;
	private ArrayList<String> ids;
	private static final int GET_POSITIONS=5;
	private static final int SEARCH=4;
	private static final int MESSAGES=3;
	private List<Overlay> mapOverlays;
	private MyOverlay itemizedoverlay;
	private Handler handlerUpdateFriendsPosition;
	private ProgressDialog progressDialog;
	protected int minId;

	//Tiempo con el cual se obtiene la posicion de los amigos
	private static final int TIME_UPDATE_POSITION = 1000 * 60 * 1;

	protected Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == GET_POSITIONS){
				getPositions();
			}
			if(msg.what == SEARCH){
				search(msg.getData().getString("position"));
			}
			if(msg.what == MESSAGES){
				showMessages(msg.getData().getString("messages"));
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_map);
		festivalId = getIntent().getExtras().getString("festival_id");
		MapView mapView = (MapView) findViewById(R.id.custom_map);
		mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.boton_mapa);
		itemizedoverlay = new MyOverlay(drawable, this);

		handlerUpdateFriendsPosition = new Handler();
		Runnable r = new Runnable(){

			public void run() {
				showFriendsInMap();
				handlerUpdateFriendsPosition.postDelayed(this, TIME_UPDATE_POSITION);
			}

		};
		handlerUpdateFriendsPosition.postDelayed(r, 1000);

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
		finish();
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
	}

	private void getPositions(){
		Iterator<String> it = ids.iterator();
		while(it.hasNext()){
			String uid = it.next();
			ConexionToServer  conexionToServer  = new ConexionToServer(uid);
			conexionToServer.execute(getString(R.string.getLoc));
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private void search(String direccion){
		GeoPoint point;
		MapView mapView = (MapView) findViewById(R.id.custom_map);
		MapController controller = mapView.getController();
		String[] pos=direccion.split(",");
		if(pos.length>0 && !pos[0].equals("") && !pos[0].equals(" ")){
			point = new GeoPoint((int)(Double.parseDouble(pos[0])*1000000),(int) (Double.parseDouble(pos[1])*1000000));
			//			Log.e("punto",point.toString());
			OverlayItem overlayitem = new OverlayItem(point, null, null);
			controller.animateTo(point);
			controller.setZoom(19);
			itemizedoverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedoverlay);
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
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair("fbid", this.fbId));
						nameValuePairs.add(new BasicNameValuePair("email", ""));
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = httpclient.execute(httppost);
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
						String sResponse;
						StringBuilder s = new StringBuilder();
						while ((sResponse = reader.readLine()) != null) 
							s = s.append(sResponse);
						if(s!=null && !s.toString().equals("ERROR") && s.toString().length()>0){
							Bundle b = new Bundle();
							b.putString("position",	s.toString().replace("|", ","));
							Message m = new Message();
							m.setData(b);
							m.what = SEARCH;
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

	@SuppressWarnings("rawtypes")
	class MyOverlay extends ItemizedOverlay {
		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		private Context mContext;

		public MyOverlay(Drawable defaultMarker, Context context) {
			super(boundCenterBottom(defaultMarker));
			mContext = context;
		}

		public MyOverlay(Drawable marker) {
			super(boundCenterBottom(marker));
		}

		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);
		}

		@Override
		public int size() {
			return mOverlays.size();
		}

		public void addOverlay(OverlayItem overlay) {
			mOverlays.add(overlay);
			populate();
		}

		@Override
		protected boolean onTap(int index) {
			OverlayItem item = mOverlays.get(index);
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(item.getTitle());
			dialog.setMessage(item.getSnippet());
			dialog.show();
			return true;
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
						//						nameValuePairs.add(new BasicNameValuePair("receiver", "123123123"));

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
}
