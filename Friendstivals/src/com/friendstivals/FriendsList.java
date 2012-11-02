package com.friendstivals;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.friendstivals.utils.TopButtonActions;

public class FriendsList extends ListActivity implements TopButtonActions {
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
		setListAdapter(new FriendListAdapter(this, jsonArray));

	}

	public void onListItemClick(ListView l, View v, int position, long id) {
//		try {
//			final long friendId;
//			friendId = jsonArray.getJSONObject(position).getLong("uid");
//			String name = jsonArray.getJSONObject(position).getString("name");
//
//		} catch (JSONException e) {
//		}
	}

	public void leftButtonClick(View v) {
		finish();
	}

	public void rightButtonClick(View v) {
		
	}
}
