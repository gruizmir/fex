package com.friendstivals.blacklist;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.friendstivals.Friend;
import com.friendstivals.FriendListAdapter;
import com.friendstivals.R;
import com.friendstivals.utils.TopButtonActions;
import com.friendstivals.utils.Utility;

@SuppressLint("HandlerLeak")
public class AddBlackList extends ListActivity implements TopButtonActions {
	protected static JSONArray jsonArray;
	private ArrayList<String> ids;
	private Handler mHandler= new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1)
				showToast(true);
			if(msg.what == 0)
				showToast(false);			
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend_list);
		((ImageView) findViewById(R.id.friend_title)).setImageResource(R.drawable.titulo_bloquear);

		ids = new ArrayList<String>();
		Bundle extras = getIntent().getExtras();
		String apiResponse = extras.getString("API_RESPONSE");
		try {
			jsonArray = new JSONArray(apiResponse);
			Log.e("response", apiResponse);
		} catch (JSONException e) {
			Log.e("JSON_fail", e.getMessage());
		}
		setListAdapter(new FriendListAdapter(this, jsonArray, false));
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

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id){
		super.onListItemClick(l, v, position, id);
		Friend f = (Friend)v.getTag();
		if(!f.getBox().isChecked()){
			f.getBox().setChecked(true);
			ids.add(f.getId());
		}
		else{
			f.getBox().setChecked(false);
			ids.remove(ids.indexOf(f.getId()));
		}
	}

	public void leftButtonClick(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}

	public void rightButtonClick(View v) {
		new Thread(new Runnable(){
			public void run(){
				SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);
				String id = pref.getString("blocked_list_id", null);
				String response=null;
				try {
					Bundle params = new Bundle();
					Iterator<String> it = ids.iterator();
					while(it.hasNext()){
						params.putString("members", it.next());
						response = Utility.mFacebook.request(id + "/members", params, "POST");
						Log.e("response", response);
					}
					mHandler.sendEmptyMessage(1);
				} catch (FileNotFoundException e) {
				} catch (MalformedURLException e) {
					mHandler.sendEmptyMessage(0);
				} catch (IOException e) {
				} catch (NullPointerException e){
				}
			}}).start();
	}
	
	private void showToast(boolean result){
		if(result)
			Toast.makeText(getApplicationContext(), "Tus amigos han sido bloqueados.", Toast.LENGTH_LONG).show();
		else
			Toast.makeText(getApplicationContext(), "No se pudo realizar el bloqueo.", Toast.LENGTH_LONG).show();
		setResult(RESULT_OK);
		finish();
	}
}