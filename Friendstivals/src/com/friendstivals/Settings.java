package com.friendstivals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.friendstivals.utils.BaseRequestListener;
import com.friendstivals.utils.Utility;

public class Settings extends Activity {
	private String festivalName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		Bundle extras = getIntent().getExtras();
		festivalName = extras.getString("festival_name");
		TextView title = (TextView) findViewById(R.id.settings_title);
		title.setText(festivalName);
		Bitmap pic = null;
		try {
			URL img_value = null;
			img_value = new URL("http://graph.facebook.com/"+Utility.userUID+"/picture?type=small");
			pic = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}catch (FacebookError e) {
		} 

		TextView profilePic = (TextView) findViewById(R.id.settings_name);
		Drawable d = new BitmapDrawable(getResources(), pic);
		profilePic.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		profilePic.setText(Utility.name);
	}
	
	public void openFriendsList(View v){
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
