package com.friendstivals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.friendstivals.utils.FriendsGetProfilePics;
import com.friendstivals.utils.Utility;

public class FriendListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private JSONArray jsonArray;
	private Friend holder;
	
	public FriendListAdapter(Context cont, JSONArray jsonArray) {
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
			hView = mInflater.inflate(R.layout.friend_list_item, null);
			holder.setProfile_pic((ImageView) hView.findViewById(R.id.friend_list_photo));
			holder.setName((TextView) hView.findViewById(R.id.friend_list_name));
			holder.setBox((CheckBox) hView.findViewById(R.id.friend_check));
			
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
		hView.setTag(holder);
		return hView;
	}
}