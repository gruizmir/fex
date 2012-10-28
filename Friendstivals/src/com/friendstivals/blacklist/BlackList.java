package com.friendstivals.blacklist;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.android.Util;
import com.friendstivals.Friend;
import com.friendstivals.R;
import com.friendstivals.utils.BaseRequestListener;
import com.friendstivals.utils.FriendsViewActions;
import com.friendstivals.utils.Utility;

@SuppressLint("HandlerLeak")
public class BlackList extends ListActivity implements FriendsViewActions{
	protected static JSONArray jsonArray;
	private ArrayList<String> ids;
	private ProgressDialog progressDialog;
	private Handler mHandler= new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// Se cargo la data de AddBlackList
			if(msg.what == 1){
			}
			//No se pudo cargar la data
			if(msg.what == 0){
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend_list);
		Bundle extras = getIntent().getExtras();
		ids = new ArrayList<String>();
		String apiResponse = extras.getString("API_RESPONSE");
		try {
			jsonArray = new JSONArray(apiResponse);
		} catch (JSONException e) {
			Log.e("json_fail", e.getMessage());
		}
		setListAdapter(new BlacklistAdapter(this, jsonArray));
//		setListAdapter(new FriendListAdapter(this, jsonArray));

		TextView title = (TextView) findViewById(R.id.friend_title);
		title.setText(R.string.blocked_friends);

		Button blockedBlock= (Button) findViewById(R.id.friend_ok);
		blockedBlock.setText(R.string.add);

		Button blockedBack = (Button) findViewById(R.id.friend_back);
		blockedBack.setText(R.string.profile);

		SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);

		/*
		 * Falta verificar que la lista este en facebook aun.
		 */
		if(!pref.contains("blocked_list_id")){
			new Thread(new Runnable(){
				public void run() {
					Bundle params = new Bundle();
					params.putString("name", "FS_Lock");
					String response=null;
					try {
						response = Utility.mFacebook.request(Utility.userUID + "/friendlists", params, "POST");
						Log.e("response",response);
						try {
							JSONObject resp = new JSONObject(response);
							SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);
							Editor editPref = pref.edit();
							editPref.putString("blocked_list_id", resp.getString("id"));
							editPref.commit();
						} catch (JSONException e1) {
							Log.e("respuesta", e1.toString());
						}
						mHandler.sendEmptyMessage(2);
					} catch (FileNotFoundException e) {
					} catch (MalformedURLException e) {
					} catch (IOException e) {
					}
				}
			}).start();
		}
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Friend f = (Friend)v.getTag();
		if(!f.getBox().isChecked()){
			f.getBox().setChecked(true);
			ids.add(f.getId());
			f.getBtn().setVisibility(View.VISIBLE);
		}
		else{
			f.getBox().setChecked(false);
			ids.remove(ids.indexOf(f.getId()));
			f.getBtn().setVisibility(View.GONE);
		}
	}

	public void leftButtonClick(View v) {
		finish();
	}

	/**
	 * Abre una pantalla que muestra los amigos que no estan en la lista de bloqueados.
	 * Ejecuta la consulta FQL y desp
	 */
	public void rightButtonClick(View v) {
		progressDialog = ProgressDialog.show(this, "", getString(R.string.loading),true);
		if (!Utility.mFacebook.isSessionValid()) {
			Util.showAlert(this, "Warning", "You must first log in.");
		} else {
			String query;
			SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);
			if(!pref.contains("blocked_list_id"))
				query = "select name, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) and is_app_user='true' order by name";
			else{
				query = "select name, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) AND NOT (uid in (select uid, flid from friendlist_member where flid="+ pref.getString("blocked_list_id", null) + ")) AND is_app_user='true' order by name";
			}
			Bundle params = new Bundle();
			params.putString("method", "fql.query");
			params.putString("query", query);
			Utility.mAsyncRunner.request(null, params,
					new BaseRequestListener(){
				public void onComplete(final String response, final Object state) {
					Log.e("response", response);
					progressDialog.dismiss();
					Intent myIntent = new Intent(getApplicationContext(), AddBlackList.class);
					myIntent.putExtra("API_RESPONSE", response);
					startActivity(myIntent);
				}
			});
		}
	}
}