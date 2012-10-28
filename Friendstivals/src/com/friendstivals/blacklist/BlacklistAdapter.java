package com.friendstivals.blacklist;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.friendstivals.Friend;
import com.friendstivals.R;
import com.friendstivals.utils.FriendsGetProfilePics;
import com.friendstivals.utils.Utility;

public class BlacklistAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private JSONArray jsonArray;
	private Friend holder;
	private String blockedId;
	private Handler mHandler= new Handler() {
		@Override
		public void handleMessage(Message msg) {
//			if(msg.what == 1)
//				showToast(true);
//			if(msg.what == 0)
//				showToast(false);			
		}
	};
	
	
	public BlacklistAdapter(Context cont, JSONArray jsonArray) {
		SharedPreferences pref = cont.getSharedPreferences("blocked", Activity.MODE_PRIVATE);
		blockedId = pref.getString("blocked_list_id", null);
		this.jsonArray = jsonArray;
		if (Utility.model == null) {
			Utility.model = new FriendsGetProfilePics();
		}
		Utility.model.setListener(this);
		mInflater = LayoutInflater.from(cont);
	}

	public int getCount() {
		if(jsonArray==null)
			return 0;
		else
			return jsonArray.length();
	}

	public Object getItem(int position) {
		return holder;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		holder = new Friend();
		JSONObject jsonObject = null;
		try {
			jsonObject = jsonArray.getJSONObject(position);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		View hView = convertView;
		if (convertView == null) {
			hView = mInflater.inflate(R.layout.blacklist_item, null);
			holder.setProfile_pic((ImageView) hView.findViewById(R.id.blacklist_photo));
			holder.setName((TextView) hView.findViewById(R.id.blacklist_name));
			holder.setBox((CheckBox) hView.findViewById(R.id.blacklist_check));
			holder.setBtn((Button)hView.findViewById(R.id.blacklist_delete));
			
		}
		try {
			holder.getProfile_pic().setImageBitmap(Utility.model.getBitmap(jsonObject.getString("pic_square")));
		} catch (JSONException e) {
		}
		try {
			holder.getName().setText(jsonObject.getString("name"));
		} catch (JSONException e) {
			holder.getName().setText("");
		}
		holder.getBox().setChecked(false);
		try {
			holder.setId(jsonObject.getString("uid"));
		} catch (JSONException e) {
			holder.setId("1");
		}
		holder.getBtn().setVisibility(View.GONE);
		
		/*
		 * Funcionara esto?
		 */
		holder.getBtn().setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				new Thread(new Runnable(){
					public void run(){
						String response=null;
						Bundle params = new Bundle();
						try {
							params.putString("members", holder.getId());
							response = Utility.mFacebook.request(blockedId + "/members", params, "DELETE");
							Log.e("response", response);
							mHandler.sendEmptyMessage(1);
						} catch (FileNotFoundException e) {
						} catch (MalformedURLException e) {
							mHandler.sendEmptyMessage(0);
						} catch (IOException e) {
						} catch (NullPointerException e){
						}
					}}).start();
			}
		});
		
		hView.setTag(holder);
		return hView;
	}

}
