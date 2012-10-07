package com.friendstivals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.R;
import com.facebook.android.Util;
import com.friendstivals.utils.BaseRequestListener;
import com.friendstivals.utils.SessionStore;
import com.friendstivals.utils.Utility;

public class Friendstivals extends Activity {

	private Facebook facebook = new Facebook("153953141413353");
	private AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
	private SharedPreferences mPrefs;
	private Button fbButton; 
	private Button mailButton;
	private Handler mHandler;
	public String userUID = null;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		fbButton = (Button) this.findViewById(R.id.fb_button);
		fbButton.setClickable(false);
		mailButton = (Button) this.findViewById(R.id.mail_button);
		mailButton.setClickable(false);
		mHandler = new Handler();

//		// Create the Facebook Object using the app id.
//		Utility.mFacebook = new Facebook(getString(R.string.fb_id));
//		// Instantiate the asynrunner object for asynchronous api calls.
//		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);


		// restore session if one exists
		SessionStore.restore(facebook, this);

		if (facebook.isSessionValid()) {
//			requestUserData();
			openFestivalList();
		}
		else{
			fbButton.setClickable(true);
		}


		//		/*
		//		 * Get existing access_token if any
		//		 */
//				mPrefs = getPreferences(MODE_PRIVATE);
//				String access_token = mPrefs.getString("access_token", null);
//				long expires = mPrefs.getLong("access_expires", 0);
		//		if(access_token != null) {
		//			facebook.setAccessToken(access_token);
		//		}
		//		if(expires != 0) {
		//			facebook.setAccessExpires(expires);
		//		}

		//		/*
		//		 * Only call authorize if the access_token has expired.
		//		 */
		//		if(!facebook.isSessionValid()) {
		//			fbButton.setClickable(true);
		//		}
		//		else{
		//			openFestivalList();
		//		}

	}

	/*
	 * Request user name, and picture to show on the main screen.
	 */
	public void requestUserData() {
		Bundle params = new Bundle();
		params.putString("fields", "name, picture");
		mAsyncRunner.request("me", params, new UserRequestListener());
	}

	/*
	 * The Callback for notifying the application when authorization succeeds or
	 * fails.
	 */

	/*
	 * Callback for fetching current user's name, picture, uid.
	 */
	public class UserRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(response);
				userUID = jsonObject.getString("id");

				mHandler.post(new Runnable() {
					public void run() {
						//                        mText.setText("Welcome " + name + "!");
						//                        mUserPic.setImageBitmap(Utility.getBitmap(picURL));
					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void openFestivalList() {
		//		Intent myIntent = new Intent(this, FestivalSelector.class);
		if (!facebook.isSessionValid()) {
			Util.showAlert(this, "Warning", "You must first log in.");
		} else {
			String query = "select name, current_location, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) and is_app_user='true' order by name";
			Bundle params = new Bundle();
			params.putString("method", "fql.query");
			params.putString("query", query);
			mAsyncRunner.request(null, params,
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.getItem(0).setTitle("Logout");
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.menu_logout:
			mAsyncRunner.logout(getBaseContext(), new RequestListener() {
				public void onComplete(String response, Object state) {
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.remove("access_token");
					editor.remove("access_expires");
					editor.commit();
					fbButton.setClickable(true);
				}

				public void onIOException(IOException e, Object state) {}

				public void onFileNotFoundException(FileNotFoundException e,
						Object state) {}

				public void onMalformedURLException(MalformedURLException e,
						Object state) {}

				public void onFacebookError(FacebookError e, Object state) {}
			});
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void fbLogin(View v){
		facebook.authorize(this, new String[] {"offline_access","publish_checkins"},new DialogListener() {
			public void onComplete(Bundle values) {
				SessionStore.save(facebook, Friendstivals.this);
				requestUserData();
				fbButton.setClickable(false);
				openFestivalList();
			}

			public void onFacebookError(FacebookError error) {
				Toast.makeText(Friendstivals.this, error.getMessage() , Toast.LENGTH_LONG).show();
			}

			public void onError(DialogError e) {
				Toast.makeText(Friendstivals.this, e.getMessage(), Toast.LENGTH_LONG).show();
			}

			public void onCancel() {
				Toast.makeText(Friendstivals.this, "Cancelado", Toast.LENGTH_LONG).show();
			}
		});
	}

	public void onResume() {    
		super.onResume();
		facebook.extendAccessTokenIfNeeded(this, null);
	}
}