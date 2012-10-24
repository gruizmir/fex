package com.friendstivals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.android.Util;
import com.friendstivals.utils.BaseRequestListener;
import com.friendstivals.utils.FriendsGetProfilePics;
import com.friendstivals.utils.FriendsViewActions;
import com.friendstivals.utils.Utility;

@SuppressLint("HandlerLeak")
public class BlackList extends ListActivity implements FriendsViewActions{
	protected static JSONArray jsonArray;
	private Handler mHandler= new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){

			}
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
		String apiResponse = extras.getString("API_RESPONSE");
		try {
			jsonArray = new JSONArray(apiResponse);
		} catch (JSONException e) {
			Log.e("json_fail", e.getMessage());
		}
		setListAdapter(new BlackListAdapter(this));

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

	}

	/**
	 * Definition of the list adapter
	 */
	public class BlackListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public BlackListAdapter(BlackList friendsList) {
			if (Utility.model == null) {
				Utility.model = new FriendsGetProfilePics();
			}
			Utility.model.setListener(this);
			mInflater = LayoutInflater.from(friendsList.getBaseContext());
		}

		public int getCount() {
			if(jsonArray==null)
				return 0;
			else
				return jsonArray.length();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			JSONObject jsonObject = null;
			try {
				jsonObject = jsonArray.getJSONObject(position);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			View hView = convertView;
			if (convertView == null) {
				hView = mInflater.inflate(R.layout.friend_list_item, null);
				ViewHolder holder = new ViewHolder();
				holder.profile_pic = (ImageView) hView.findViewById(R.id.friend_list_photo);
				holder.name = (TextView) hView.findViewById(R.id.friend_list_name);
				hView.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) hView.getTag();
			try {
				holder.profile_pic.setImageBitmap(Utility.model.getImage(
						jsonObject.getString("uid"), jsonObject.getString("pic_square")));
			} catch (JSONException e) {
			}
			try {
				holder.name.setText(jsonObject.getString("name"));
			} catch (JSONException e) {
				holder.name.setText("");
			}
			return hView;
		}
	}

	class ViewHolder {
		ImageView profile_pic;
		TextView name;
	}

	public void leftButtonClick(View v) {
		finish();
	}

	public void rightButtonClick(View v) {
		if (!Utility.mFacebook.isSessionValid()) {
			Util.showAlert(this, "Warning", "You must first log in.");
		} else {
			String query;
			SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);
			if(!pref.contains("blocked_list_id"))
				query = "select name, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) and is_app_user='true' order by name";
			else{
				query = "select name, uid, pic_square from user where uid not in (select flid, uid from friendlist_member where flid="+ pref.getString("blocked_list_id", null) + ") and is_app_user='true' order by name";
			}
			Bundle params = new Bundle();
			params.putString("method", "fql.query");
			params.putString("query", query);
			Utility.mAsyncRunner.request(null, params,
					new BaseRequestListener(){
				public void onComplete(final String response, final Object state) {
					Intent myIntent = new Intent(getApplicationContext(), AddBlackList.class);
					myIntent.putExtra("API_RESPONSE", response);
					startActivity(myIntent);
				}
			});
		}
	}
}