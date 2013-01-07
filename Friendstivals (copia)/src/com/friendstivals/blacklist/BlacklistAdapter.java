package com.friendstivals.blacklist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
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

@SuppressLint("HandlerLeak")
public class BlacklistAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private JSONArray jsonArray;
	private Friend holder;
	private String userId;
	
	public BlacklistAdapter(Context cont, JSONArray jsonArray) {
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
			userId = jsonObject.getString("uid");
			holder.setId(userId);
		} catch (JSONException e) {
			holder.setId("1");
		}
		hView.setTag(holder);
		return hView;
	}
}
