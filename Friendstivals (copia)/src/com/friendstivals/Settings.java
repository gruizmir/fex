package com.friendstivals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.friendstivals.blacklist.BlackList;
import com.friendstivals.utils.BaseButtonActions;
import com.friendstivals.utils.BaseRequestListener;
import com.friendstivals.utils.TopButtonActions;
import com.friendstivals.utils.Utility;

@SuppressLint("HandlerLeak")
public class Settings extends Activity implements TopButtonActions, BaseButtonActions {
	private CheckBox check;
	protected int cantBlock=0;
	private String festivalId;
	protected Bitmap pic=null;
	private ProgressDialog progressDialog;
	private CheckBox avail;
	private int NO_LIST_ID = 2;
	private int SEND_DATA = 3;
	private int COUNT = 4;
	private int NO_ACT=5;
	private int ACT=6;
	private String id=null;
	private Button countBtn; 
	private Handler mHandler= new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(progressDialog!=null && progressDialog.isShowing())
				progressDialog.dismiss();
			if(msg.what == ACT){
			}

			if(msg.what == 1){
				setImage();
			}
			if(msg.what == 0){
				setCantBlock();
			}
			if(msg.what == NO_LIST_ID)
				getBlockedListId();
			if(msg.what == SEND_DATA){
				id = msg.getData().getString("blocked_list_id");
				new Thread(new Runnable(){
					public void run() {
						ConexionToServer  conexionToServer  = new ConexionToServer();
						conexionToServer.execute(getString(R.string.setBlockedListId));
						mHandler.sendEmptyMessage(0);
					}
				}).start();
			}
			if(msg.what ==COUNT){
				try {
					JSONArray jsonArray = new JSONArray(msg.getData().getString("count"));
					cantBlock = jsonArray.length();
				} catch (JSONException e) {
					Log.e("json_fail", e.getMessage());
				}
				SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);
				countBtn.setText(Integer.toString(cantBlock) + " amigos");
				Editor ed = pref.edit();
				ed.putInt("blocked_count", cantBlock);
				ed.commit();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		progressDialog = ProgressDialog.show(this, "", getString(R.string.loading),true);
		festivalId = getIntent().getExtras().getString("festival_id");
		countBtn = (Button) findViewById(R.id.blocked_button);
		SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);
		/*
		 * Falta verificar que la lista este en facebook aun.
		 */
		if(!pref.contains("blocked_list_id")){
			getBlockedListIdFromServer();
		}
		else{
			setCantBlock();
		}
		try {
			new Thread(new Runnable(){
				public void run() {
					URL img_value = null;
					try {
						img_value = new URL("http://graph.facebook.com/"+Utility.userUID+"/picture?type=small");
						pic = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
						mHandler.sendEmptyMessage(1);
					} catch (MalformedURLException e) {
						mHandler.sendEmptyMessage(0);
					} catch (IOException e){
						mHandler.sendEmptyMessage(0);
					}
				}}).start();
		}catch (FacebookError e) {
		} 

		//Switch para ver si la persona esta disponible para que sus amigos la vean.
		avail = (CheckBox)findViewById(R.id.avalaible_check);
		final SharedPreferences sets = getSharedPreferences("settings", MODE_PRIVATE);
		if(sets.contains("available"))
			avail.setChecked(sets.getBoolean("available", true));
		avail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				sendAvailable(isChecked);
				Editor sed = sets.edit();
				sed.putBoolean("available", isChecked);
				sed.commit();
			}
		});
		//Ver si el GPS esta encendido para poner el switch en on u off
		check = (CheckBox)findViewById(R.id.gps_switch);
		check.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Intent actividad = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(actividad);
			}

		});
	}

	@Override
	protected void onResume(){
		super.onResume();
		LocationManager servicio = (LocationManager) getSystemService(LOCATION_SERVICE);
		Boolean activado = servicio
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!activado) {
			check.setChecked(false);
		} else {
			check.setChecked(true);
		}
	}

	private void setImage(){
		TextView profilePic = (TextView) findViewById(R.id.settings_name);
		Drawable d = new BitmapDrawable(getResources(), pic);
		profilePic.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
		profilePic.setText(Utility.name);
	}
	/**
	 * Debe mostrar la lista de usuarios en la lista negra. Mientras, solo tiene los datos de todos los usuarios.
	 * @author gabriel
	 *
	 */
	public void openBlackList(View v){
		progressDialog = ProgressDialog.show(this, "", getString(R.string.loading),true);
		if (!Utility.mFacebook.isSessionValid()) {
			Util.showAlert(this, "Warning", "You must first log in.");
		} else {
			String query;
			SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);
			if(!pref.contains("blocked_list_id"))
				query = "select name, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) and is_app_user='true' order by name";
			else{
				query = "select name, uid, pic_square from user where uid in (select flid, uid from friendlist_member where flid="+ pref.getString("blocked_list_id", null) + ") and is_app_user='true' order by name";
			}
			Bundle params = new Bundle();
			params.putString("method", "fql.query");
			params.putString("query", query);
			Utility.mAsyncRunner.request(null, params,
					new BaseRequestListener(){
				public void onComplete(final String response, final Object state) {
					progressDialog.dismiss();
					Intent myIntent = new Intent(getApplicationContext(), BlackList.class);
					myIntent.putExtra("API_RESPONSE", response);
					startActivityForResult(myIntent,1);
				}
			});
		}
	}

	public void openInviteView(View v){
		Intent i = new Intent(this, InviteView.class);
		this.startActivityForResult(i, 0);
	}

	public void openFriendsList(View v){
		progressDialog = ProgressDialog.show(this, "", getString(R.string.loading),true);
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
			Bundle b = new Bundle();
			b.putString("API_RESPONSE", response);
			b.putInt("ACTION", FriendsList.FRIENDS_IN_MAP);
			myIntent.putExtras(b);
			startActivityForResult(myIntent,1);
		}

		public void onFacebookError(FacebookError error) {
			Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		SharedPreferences pref = this.getSharedPreferences("settings", MODE_PRIVATE);
		if(!pref.contains("available")){
			Editor ed = pref.edit();
			if(avail.isChecked())
				ed.putBoolean("available", true);
			else{
				ed.putBoolean("available", false);
			}
			ed.commit();
		}else{
			Editor ed = pref.edit();
			if(avail.isChecked())
				ed.putBoolean("available", true);
			else{
				ed.putBoolean("available", false);
			}
			ed.commit();
		}
	}

	private void getBlockedListIdFromServer(){
		new Thread(new Runnable(){
			public void run() {
				ConexionToServer  conexionToServer  = new ConexionToServer();
				conexionToServer.execute(getString(R.string.getBlockedListId));
			}
		}).start();
	}

	private void getBlockedListId(){
		new Thread(new Runnable(){
			public void run() {
				Bundle params = new Bundle();
				params.putString("name", "FS_Lock");
				String response=null;
				try {
					response = Utility.mFacebook.request(Utility.userUID + "/friendlists", params, "POST");
					try {
						JSONObject resp = new JSONObject(response);
						SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);
						Editor editPref = pref.edit();
						editPref.putString("blocked_list_id", resp.getString("id"));
						editPref.commit();
						Message m = new Message();
						Bundle b = new Bundle();
						b.putString("blocked_list_id", resp.getString("id"));
						m.setData(b);
						m.what = SEND_DATA;
						mHandler.sendMessage(m);
					} catch (JSONException e1) {
					}
				} catch (FileNotFoundException e) {
				} catch (MalformedURLException e) {
				} catch (IOException e) {
				}
			}
		}).start();
	}

	/**
	 * Se inicia la conexion con el servidor para ingresar a un nuevo usuario.
	 * @author astom
	 *
	 */
	private class ConexionToServer extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... sUrl) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(sUrl[0]);
				if(httpclient != null && httppost != null && Utility.userUID != null && !Utility.userUID.equals("")){
					try {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("fbid", Utility.userUID));
						if(id!=null){
							Log.e("blocked", id);
							nameValuePairs.add(new BasicNameValuePair("blockedlistid", id));
						}
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = httpclient.execute(httppost);
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
						String sResponse;
						StringBuilder s = new StringBuilder();
						while ((sResponse = reader.readLine()) != null) 
							s = s.append(sResponse);
						if(s.toString().equals("OK")){
							mHandler.sendEmptyMessage(0);
							return "OK";
						}
						if(!s.toString().equals("") && !s.toString().equals("NULL")){
							SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);
							Editor editPref = pref.edit();
							editPref.putString("blocked_list_id", s.toString());
							editPref.commit();
							mHandler.sendEmptyMessage(0);
						}
						else
							mHandler.sendEmptyMessage(NO_LIST_ID);

					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else{
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
		Intent i = new Intent(this, SendMessage.class);
		i.putExtras(getIntent());
		startActivity(i);
	}

	public void openMap(View v){
		this.finish();
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
	}

	public void openFestivalList(View v){
		Intent i = new Intent(getApplicationContext(), FestivalSelector.class);
		startActivity(i);
		finish();
	}

	public void openAcciones(View v) {
		// TODO Auto-generated method stub

	}

	private void getCantBloqueados(){
		if (!Utility.mFacebook.isSessionValid()) {
			Util.showAlert(this, "Warning", "You must first log in.");
		} else {
			SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);
			String query = "select uid from user where uid in (select flid, uid from friendlist_member where flid="+ pref.getString("blocked_list_id", null) + ") and is_app_user='true'";
			Bundle params = new Bundle();
			params.putString("method", "fql.query");
			params.putString("query", query);
			Utility.mAsyncRunner.request(null, params, new BaseRequestListener() {
				public void onComplete(final String response, final Object state) {
					Bundle b = new Bundle();
					b.putString("count", response);
					Message m = new Message();
					m.what = COUNT;
					m.setData(b);
					mHandler.sendMessage(m);
				}
			});
		}
	}

	private void setCantBlock(){
		getCantBloqueados();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data){
		setCantBlock();
	}

	private void sendAvailable(final boolean isChecked){
		new Thread(new Runnable(){
			public void run(){
				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(getString(R.string.setAvail));
					if(httpclient != null && httppost != null && Utility.userUID != null && !Utility.userUID.equals("")){
						try {
							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
							nameValuePairs.add(new BasicNameValuePair("fbid", Utility.userUID));
							nameValuePairs.add(new BasicNameValuePair("available", Boolean.toString(isChecked)));
							httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							HttpResponse response = httpclient.execute(httppost);
							BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
							String sResponse;
							StringBuilder s = new StringBuilder();
							while ((sResponse = reader.readLine()) != null) 
								s = s.append(sResponse);
							if(!s.toString().equals("") && !s.toString().equals("NULL"))
								mHandler.sendEmptyMessage(ACT);
							else
								mHandler.sendEmptyMessage(NO_ACT);
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else{
					}
				} catch (Exception e) {
				}
			}
		}).start();
	}
}