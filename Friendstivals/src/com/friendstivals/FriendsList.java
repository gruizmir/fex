package com.friendstivals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.friendstivals.utils.FriendsGetProfilePics;
import com.friendstivals.utils.Utility;

public class FriendsList extends Activity implements OnItemClickListener {
	protected ListView friendsList;
	protected static JSONArray jsonArray;

	/*
	 * Layout the friends' list
	 */
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
			return;
		}
		friendsList = (ListView) findViewById(R.id.amigos);
		friendsList.setOnItemClickListener(this);
		friendsList.setAdapter(new FriendListAdapter(this));

	}

	/*
	 * Clicking on a friend should popup a dialog for user to post on friend's
	 * wall.
	 */
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
//		try {
//			final long friendId;
//			friendId = jsonArray.getJSONObject(position).getLong("uid");
//			String name = jsonArray.getJSONObject(position).getString("name");
//
//		} catch (JSONException e) {
//		}
	}

	/**
	 * Definition of the list adapter
	 */
	public class FriendListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		FriendsList friendsList;

		public FriendListAdapter(FriendsList friendsList) {
			this.friendsList = friendsList;
			if (Utility.model == null) {
				Utility.model = new FriendsGetProfilePics();
			}
			Utility.model.setListener(this);
			mInflater = LayoutInflater.from(friendsList.getBaseContext());
		}

		public int getCount() {
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			View hView = convertView;
			if (convertView == null) {
				hView = mInflater.inflate(R.layout.friend_list_item, null);
				ViewHolder holder = new ViewHolder();
				holder.profile_pic = (ImageView) hView.findViewById(R.id.friend_list_photo);
				holder.name = (TextView) hView.findViewById(R.id.friend_list_name);
				//                holder.info = (TextView) hView.findViewById(R.id.info);
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
}
