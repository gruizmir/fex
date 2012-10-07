package com.friendstivals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class Friendstivals extends Activity {

	Facebook facebook = new Facebook("153953141413353");
	AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
//		Intent myIntent = new Intent(this, FestivalSelector.class);
//		this.startActivity(myIntent);
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void fbLogin(View v){
		facebook.authorize(this, new String[] {"publish_checkins"},new DialogListener() {
            public void onComplete(Bundle values) {
            	Toast.makeText(Friendstivals.this, "Funciono", Toast.LENGTH_SHORT).show();
            }

            public void onFacebookError(FacebookError error) {
            	Toast.makeText(Friendstivals.this, "FB Error", Toast.LENGTH_SHORT).show();
            }

            public void onError(DialogError e) {
            	Toast.makeText(Friendstivals.this, "Error", Toast.LENGTH_SHORT).show();
            }

            public void onCancel() {
            	Toast.makeText(Friendstivals.this, "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });
	}
}