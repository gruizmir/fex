package com.fex;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.facebook.android.FacebookError;
import com.fex.artist.LineUp;
import com.fex.info.Info;
import com.fex.playlist.Playlist;
import com.fex.utils.BaseButtonActions;
import com.fex.utils.BaseRequestListener;
import com.fex.utils.TopButtonActions;

public class Extras extends Activity implements TopButtonActions, BaseButtonActions{
	private String festivalId;
	private ProgressDialog progressDialog;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		festivalId = getIntent().getExtras().getString("festival_id");
		setContentView(R.layout.extras);		
	}
	
	public void openLineup(View v){
		Intent i = new Intent(this, LineUp.class);
		Bundle b = new Bundle();
		b.putString("festival_id", festivalId);
		i.putExtras(b);
		startActivity(i);
	}
	
	public void openPlaylist(View v){
		Intent i = new Intent(this, Playlist.class);
		startActivity(i);
	}
	
	public void openInfo(View v){
		Intent i = new Intent(this, Info.class);
		Bundle b = new Bundle();
		b.putString("festival_id", festivalId);
		i.putExtras(b);
		startActivity(i);
	} 
	
	public void openAnteriores(View v){
		
	}

	public void leftButtonClick(View v) {
		Intent i = new Intent(this, InviteView.class);
		startActivity(i);
	}

	public void rightButtonClick(View v) {
		Intent i = new Intent(this, SendMessage.class);
		i.putExtras(getIntent());
		startActivity(i);
	}

	/*
	 * callback after friends are fetched via me/friends or fql query.
	 */
	public class FriendsRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			progressDialog.dismiss();
			Intent myIntent = new Intent(getApplicationContext(), FriendsList.class);
			myIntent.putExtra("API_RESPONSE", response);
			startActivity(myIntent);
		}

		public void onFacebookError(FacebookError error) {
			Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}
	
	public void openMap(View v){
		this.finish();
	}
	
	public void openExtras(View v){
	}

	public void openSettings(View v){
		Intent i = new Intent(getApplicationContext(), Settings.class);
		Bundle b = new Bundle();
		b.putString("festival_id", festivalId);
		i.putExtras(b);
		startActivity(i);
		finish();
	}

	public void openFestivalList(View v){
		Intent i = new Intent(getApplicationContext(), FestivalSelector.class);
		startActivity(i);
		finish();
	}

	public void openAcciones(View v) {
		// TODO Auto-generated method stub
	}
}
