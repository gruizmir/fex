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
		setListAdapter(new FriendListAdapter(this, jsonArray, true));

	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Friend f = (Friend)v.getTag();
		if(!f.getBox().isChecked()){
			f.getBox().setChecked(true);
		}
		else{
			f.getBox().setChecked(false);
		}
	}

	public void leftButtonClick(View v) {
		finish();
	}

	public void rightButtonClick(View v) {
		
	}
}
