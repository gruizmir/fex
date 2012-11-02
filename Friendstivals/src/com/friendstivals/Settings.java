package com.friendstivals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.friendstivals.blacklist.BlackList;
import com.friendstivals.utils.BaseRequestListener;
import com.friendstivals.utils.Festival;
import com.friendstivals.utils.Utility;

public class Settings extends Activity {
	private String festivalId;
	protected Bitmap pic=null;
	private ProgressDialog progressDialog;
	private Handler mHandler= new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setImage();
			progressDialog.dismiss();
		}
	};

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		progressDialog = ProgressDialog.show(this, "", getString(R.string.loading),true);
		Bundle extras = getIntent().getExtras();
		festivalId = extras.getString("festival_id");
		Festival f = new Festival(this, festivalId);  

		SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);
		/*
		 * Falta verificar que la lista este en facebook aun.
		 */
		if(!pref.contains("blocked_list_id")){
			new Thread(new Runnable(){
				public void run() {
					Bundle params = new Bundle();
					params.putString("name", "FS_Lock");
					String response=null;
					try {
						response = Utility.mFacebook.request(Utility.userUID + "/friendlists", params, "POST");
						Log.e("response",response);
						try {
							JSONObject resp = new JSONObject(response);
							SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);
							Editor editPref = pref.edit();
							editPref.putString("blocked_list_id", resp.getString("id"));
							editPref.commit();
						} catch (JSONException e1) {
							Log.e("respuesta", e1.toString());
						}
					} catch (FileNotFoundException e) {
					} catch (MalformedURLException e) {
					} catch (IOException e) {
					}
				}
			}).start();
		}

		TextView title = (TextView) findViewById(R.id.settings_title);
		title.setText(f.getName());
		try {
			new Thread(new Runnable(){
				public void run() {
					URL img_value = null;
					try {
						img_value = new URL("http://graph.facebook.com/"+Utility.userUID+"/picture?type=small");
						pic = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
						mHandler.sendEmptyMessage(1);
					} catch (MalformedURLException e) {
					} catch (IOException e){
					}
				}}).start();
		}catch (FacebookError e) {
		} 
	}

	private void setImage(){
		TextView profilePic = (TextView) findViewById(R.id.settings_name);
		Drawable d = new BitmapDrawable(getResources(), pic);
		profilePic.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		profilePic.setText(Utility.name);
	}
	/**
	 * Debe mostrar la lista de usuarios en la lista negra. Mientras, solo tiene los datos de todos los usuarios.
	 * @author gabriel
	 *
	 */
	public void openBlackList(View v){
		progressDialog = ProgressDialog.show(this, "", getString(R.string.loading),true);
		if (!Utility.mFacebook.isSessionValid()) {
			Util.showAlert(this, "Warning", "You must first log in.");
		} else {
			String query;
			SharedPreferences pref = getSharedPreferences("blocked", MODE_PRIVATE);
			if(!pref.contains("blocked_list_id"))
				query = "select name, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) and is_app_user='true' order by name";
			else{
				query = "select name, uid, pic_square from user where uid in (select flid, uid from friendlist_member where flid="+ pref.getString("blocked_list_id", null) + ") and is_app_user='true' order by name";
			}
			Bundle params = new Bundle();
			params.putString("method", "fql.query");
			params.putString("query", query);
			Utility.mAsyncRunner.request(null, params,
					new BaseRequestListener(){
				public void onComplete(final String response, final Object state) {
					progressDialog.dismiss();
					Intent myIntent = new Intent(getApplicationContext(), BlackList.class);
					myIntent.putExtra("API_RESPONSE", response);
					startActivity(myIntent);
				}
			});
		}
	}

	public void openInviteView(View v){
		Intent i = new Intent(this, InviteView.class);
		this.startActivityForResult(i, 0);
	}

	public void openFriendsList(View v){
		progressDialog = ProgressDialog.show(this, "", getString(R.string.loading),true);
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
}
