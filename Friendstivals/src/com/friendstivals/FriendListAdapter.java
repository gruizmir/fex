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
			
			holder.profile_pic = (ImageView) hView.findViewById(R.id.friend_list_photo);
			holder.name = (TextView) hView.findViewById(R.id.friend_list_name);
			holder.box = (CheckBox) hView.findViewById(R.id.friend_check);
			hView.setTag(holder);
		}
		Friend holder = (Friend) hView.getTag();
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
		try {
			holder.id = jsonObject.getString("uid");
		} catch (JSONException e) {
			holder.id = "1";
		}
		return hView;
	}
}