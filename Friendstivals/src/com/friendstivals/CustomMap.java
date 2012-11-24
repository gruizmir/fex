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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.friendstivals.utils.BaseRequestListener;
import com.friendstivals.utils.Utility;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

@SuppressLint("HandlerLeak")
public class CustomMap extends MapActivity {
	private String festivalId;
	private ArrayList<String> ids;
	private static final int GET_POSITIONS=5;
	private static final int SEARCH=4;
	private List<Overlay> mapOverlays;
	private MyOverlay itemizedoverlay;
	protected Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == GET_POSITIONS){
				getPositions();
			}
			if(msg.what == SEARCH){
				search(msg.getData().getString("position"));
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
		showFriendsInMap();
	}

	public void openMap(View v){
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
//		Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
//		List<Address> addresses;
		String[] pos=direccion.split(",");
//		try {
//			addresses = geoCoder.getFromLocationName(direccion,5);
//			if(addresses.size() > 0){
			if(pos.length>0 && !pos[0].equals("") && !pos[0].equals(" ")){
				point = new GeoPoint((int)(Double.parseDouble(pos[0])*1000000),(int) (Double.parseDouble(pos[1])*1000000));
				Log.e("punto",point.toString());
				OverlayItem overlayitem = new OverlayItem(point, null, null);
				controller.animateTo(point);
				controller.setZoom(19);
				itemizedoverlay.addOverlay(overlayitem);
				mapOverlays.add(itemizedoverlay);
			}
//			}
//		} catch (IOException e) {
//		}
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
}
