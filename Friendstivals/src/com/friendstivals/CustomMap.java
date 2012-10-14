package com.friendstivals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.facebook.android.FacebookError;
import com.facebook.android.R;
import com.facebook.android.Util;
import com.friendstivals.utils.BaseRequestListener;
import com.friendstivals.utils.Utility;

public class CustomMap extends Activity {
	private String festivalName;
//	private String festivalKey;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_map);
		Bundle extras = getIntent().getExtras();
		festivalName = extras.getString("festival_name");
//		festivalKey = extras.getString("festival_key");
	}

	public void openMap(View v){

	}

	public void openSettings(View v){
		Intent i = new Intent(getApplicationContext(), Settings.class);
		Bundle b = new Bundle();
		b.putString("festival_name", festivalName);
		i.putExtras(b);
		startActivity(i);
	}

	public void openFestivalList(View v){
		this.finish();
	}
	
	public void sendMessageToFriends(){
		if (!Utility.mFacebook.isSessionValid()) {
			Util.showAlert(this, "Warning", "You must first log in.");
		} else {
			String query = "select name, current_location, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) and is_app_user='true' order by name";
			Bundle params = new Bundle();
			params.putString("method", "fql.query");
			params.putString("query", query);
			Utility.mAsyncRunner.request(null, params,
					new FriendsRequestListener());

		}
	}
	
	/*
	 * callback after friends are fetched via me/friends or fql query.
	 */
	public class FriendsRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			Intent myIntent = new Intent(getApplicationContext(), FriendsList.class);
			myIntent.putExtra("API_RESPONSE", response);
			startActivity(myIntent);
		}

		public void onFacebookError(FacebookError error) {
			Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}
}
