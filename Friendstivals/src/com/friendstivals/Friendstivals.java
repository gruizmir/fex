package com.friendstivals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class Friendstivals extends Activity {

	private Facebook facebook = new Facebook("153953141413353");
	private AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
	private SharedPreferences mPrefs;
	private Button fbButton; 
	private Button mailButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		fbButton = (Button) this.findViewById(R.id.fb_button);
		fbButton.setClickable(false);
		mailButton = (Button) this.findViewById(R.id.mail_button);
		mailButton.setClickable(false);
		/*
		 * Get existing access_token if any
		 */
		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		if(access_token != null) {
			facebook.setAccessToken(access_token);
		}
		if(expires != 0) {
			facebook.setAccessExpires(expires);
		}

		/*
		 * Only call authorize if the access_token has expired.
		 */
		if(!facebook.isSessionValid()) {
			fbButton.setClickable(true);
		}
		else{
			openFestivalList();
		}

	}

	private void openFestivalList() {
		Intent myIntent = new Intent(this, FestivalSelector.class);
		this.startActivity(myIntent);		
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
		facebook.authorize(this, new String[] {"publish_checkins"},new DialogListener() {
			public void onComplete(Bundle values) {
				SharedPreferences.Editor editor = mPrefs.edit();
				editor.putString("access_token", facebook.getAccessToken());
				editor.putLong("access_expires", facebook.getAccessExpires());
				editor.commit();
				fbButton.setClickable(false);
				openFestivalList();
			}

			public void onFacebookError(FacebookError error) {
				Toast.makeText(Friendstivals.this, error.getMessage() , Toast.LENGTH_SHORT).show();
			}

			public void onError(DialogError e) {
				Toast.makeText(Friendstivals.this, e.getMessage(), Toast.LENGTH_SHORT).show();
			}

			public void onCancel() {
				Toast.makeText(Friendstivals.this, "Cancelado", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void onResume() {    
        super.onResume();
        facebook.extendAccessTokenIfNeeded(this, null);
    }
}