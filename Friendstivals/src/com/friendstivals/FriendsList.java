package com.friendstivals;

import java.util.ArrayList;

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
	public final static int SEND_MESSAGE = 5;
	public final static int FRIENDS_IN_MAP = 4;
	private String message;
	private String festivalId;
	private int action;
	private ArrayList<String> ids;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend_list);
		Bundle extras = getIntent().getExtras();
		String apiResponse = extras.getString("API_RESPONSE");
		festivalId = extras.getString("festival_id");
		action = extras.getInt("ACTION");
		if(action == SEND_MESSAGE)
			message = extras.getString("message");
		ids = new ArrayList<String>();
		try {
			jsonArray = new JSONArray(apiResponse);
		} catch (JSONException e) {
			return;
		}
		setListAdapter(new FriendListAdapter(this, jsonArray, true));
		
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
		finish();
	}

	public void rightButtonClick(View v) {
		
	}
}