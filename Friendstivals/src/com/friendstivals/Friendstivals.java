package com.friendstivals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.friendstivals.utils.BaseRequestListener;
import com.friendstivals.utils.SessionStore;
import com.friendstivals.utils.Utility;

@SuppressLint("HandlerLeak")
public class Friendstivals extends Activity {
	private Button fbButton; 
	private Button mailButton;
	private Handler mHandler= new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//Significa que es la primera vez que se loguea
			if(fbButton.isClickable()){
				fbButton.setClickable(false);
				openInviteView();
			}
			else{
				fbButton.setClickable(false);
				openFestivalList();
			}
		}
	};
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

		Utility.mFacebook = new Facebook(getString(R.string.fb_id));
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
		SessionStore.restore(Utility.mFacebook, this);
		if (Utility.mFacebook.isSessionValid()) {
			requestUserData();
		}
		else{
			fbButton.setClickable(true);
		}
	}

	/*
	 * Request user name, and picture to show on the main screen.
	 */
	public void requestUserData() {
		new Thread(new Runnable(){
			public void run() {
				Bundle params = new Bundle();
				params.putString("fields", "id, name, picture");
				Utility.mAsyncRunner.request("me", params, new UserRequestListener());
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	/*
	 * Callback for fetching current user's name, picture, uid.
	 */
	public class UserRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(response);
				Utility.picURL = jsonObject.getString("picture");
                Utility.name = jsonObject.getString("name");
				Utility.userUID = jsonObject.getString("id");
				mHandler.post(new Runnable() {
					public void run() {	}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void openFestivalList() {
		Intent myIntent = new Intent(this, FestivalSelector.class);
		startActivity(myIntent);
	}

	private void openInviteView(){
		Intent i = new Intent(this, InviteView.class);
		this.startActivityForResult(i, 0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
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
			Utility.mAsyncRunner.logout(getBaseContext(), new RequestListener() {
				public void onComplete(String response, Object state) {
					SessionStore.clear(getApplicationContext());
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
		Utility.mFacebook.authorize(this, new String[] {"publish_checkins", "manage_friendlists", "read_friendlists"},new DialogListener() {
			public void onComplete(Bundle values) {
				SessionStore.save(Utility.mFacebook, Friendstivals.this);
				requestUserData();
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
		Utility.mFacebook.extendAccessTokenIfNeeded(this, null);
	}
}
