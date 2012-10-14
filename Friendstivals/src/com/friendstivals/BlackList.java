package com.friendstivals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
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

public class BlackList extends ListActivity implements FriendsViewActions{
	protected static JSONArray jsonArray;

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
		setListAdapter(new FriendListAdapter(this));
		
		TextView title = (TextView) findViewById(R.id.friend_title);
		title.setText(R.string.blocked_friends);
		
		Button blockedBlock= (Button) findViewById(R.id.friend_ok);
		blockedBlock.setText(R.string.add);
		
		Button blockedBack = (Button) findViewById(R.id.friend_back);
		blockedBack.setText(R.string.profile);
	}

	
	
	public void onListItemClick(ListView l, View v, int position, long id) {
		
	}
	
	/**
	 * Definition of the list adapter
	 */
	public class FriendListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		BlackList friendsList;

		public FriendListAdapter(BlackList friendsList) {
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
			String query = "select name, current_location, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) and is_app_user='true' order by name";
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
