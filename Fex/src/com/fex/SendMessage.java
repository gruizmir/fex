package com.fex;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.fex.utils.BaseButtonActions;
import com.fex.utils.BaseRequestListener;
import com.fex.utils.TopButtonActions;
import com.fex.utils.Utility;

public class SendMessage extends Activity implements TopButtonActions, BaseButtonActions{
	private String festivalId;
	private ProgressDialog progressDialog;
	private String message=null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		festivalId = getIntent().getExtras().getString("festival_id");
		setContentView(R.layout.send_message);		
	}
	
	public void send(View v){
		progressDialog = ProgressDialog.show(this, "", getString(R.string.loading),true);
		EditText ed = (EditText) findViewById(R.id.message_edit);
		Spinner sp = (Spinner) findViewById(R.id.message_spinner);
		if(!ed.getText().toString().equals(""))
			message = ed.getText().toString();
		else
			message = sp.getSelectedItem().toString();
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
			Bundle b = new Bundle();
			b.putString("festival_id", festivalId);
			b.putString("message", message);
			b.putInt("ACTION", FriendsList.SEND_MESSAGE);
			myIntent.putExtras(b);
			startActivity(myIntent);
			progressDialog.dismiss();
		}

		public void onFacebookError(FacebookError error) {
			Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	public void openAcciones(View v) {
		// TODO Auto-generated method stub
	}

	public void openExtras(View v) {
		Intent i = new Intent(getApplicationContext(), Extras.class);
		Bundle b = new Bundle();
		b.putString("festival_id", festivalId);
		i.putExtras(b);
		startActivity(i);
	}

	public void openSettings(View v) {
		Intent i = new Intent(getApplicationContext(), Settings.class);
		Bundle b = new Bundle();
		b.putString("festival_id", festivalId);
		i.putExtras(b);
		startActivity(i);
	}

	public void openFestivalList(View v) {
		Intent i = new Intent(this, FestivalSelector.class);
		startActivity(i);
		this.finish();
	}

	public void openMap(View v) {
		// TODO Auto-generated method stub
	}

	public void leftButtonClick(View v) {
		Intent i = new Intent(this, InviteView.class);
		startActivity(i);
		
	}

	public void rightButtonClick(View v) {
		Dialog mDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.setContentView(R.layout.bubble_dialog);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.show();
	}
}
