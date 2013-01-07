package com.friendstivals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.friendstivals.utils.BaseRequestListener;
import com.friendstivals.utils.SessionStore;
import com.friendstivals.utils.Utility;

@SuppressLint("HandlerLeak")
public class Friendstivals extends Activity {
	private ImageButton fbButton; 
	private ImageButton mailButton;
	private HttpClient httpclient;
	private HttpPost httppost;
	private boolean firstLogin=false;
	//Direccion para realizar la conexion
	private static final String DIRECCION = "http://23.23.170.228/save.php?action=register";
	//D = true si se esta haciendo debug por logCat, DT = true si se esta haciendo debug por toast
	private static final boolean D = false;

	private Handler mHandler= new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//Significa que es la primera vez que se loguea
			if(msg.what==0){
				openFestivalList();
				openInviteView();
				finish();
			}
			else{
				openFestivalList();
				finish();
			}
		}
	};
	public String userUID = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utility.mFacebook = new Facebook(getString(R.string.fb_id));
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
		SessionStore.restore(Utility.mFacebook, this);
		if (Utility.mFacebook.isSessionValid()) {
			firstLogin=false;
			requestUserData();
		}
		else{
			firstLogin=true;
			setContentView(R.layout.main);
			fbButton = (ImageButton) this.findViewById(R.id.fb_button);
			fbButton.setClickable(true);
			mailButton = (ImageButton) this.findViewById(R.id.mail_button);
			mailButton.setClickable(false);
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
				if(firstLogin)
					mHandler.sendEmptyMessage(0);
				else
					mHandler.sendEmptyMessage(1);
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
					public void run() {
						createConexion();
					}
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

	/**
	 * Se inicia la conexion con el servidor para ingresar a un nuevo usuario.
	 * @author astom
	 *
	 */
	private class ConexionToServer extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... sUrl) {
			try {
				httpclient = new DefaultHttpClient();
				httppost = new HttpPost(sUrl[0]);
				if(httpclient != null && httppost != null && Utility.userUID != null && !Utility.userUID.equals("")){
					try {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair("fbid", Utility.userUID));
						nameValuePairs.add(new BasicNameValuePair("email", ""));
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = httpclient.execute(httppost);
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
						String sResponse;
						StringBuilder s = new StringBuilder();
						while ((sResponse = reader.readLine()) != null) 
							s = s.append(sResponse);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else{
				}
			} catch (Exception e) {
				if(D) e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
		}

		@Override
		protected void onPostExecute(String result) {
		}
	}

	public void createConexion(){
		ConexionToServer  conexionToServer  = new ConexionToServer();
		conexionToServer.execute(DIRECCION);
	}
}
