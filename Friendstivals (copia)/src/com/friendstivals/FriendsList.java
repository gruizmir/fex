package com.friendstivals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.friendstivals.utils.TopButtonActions;
import com.friendstivals.utils.Utility;

@SuppressLint("HandlerLeak")
public class FriendsList extends ListActivity implements TopButtonActions {
	protected static JSONArray jsonArray;
	public final static int SEND_MESSAGE = 5;
	public final static int FRIENDS_IN_MAP = 4;
	public final static int SENT_MESSAGE = 3;
	private Dialog sentDialog;
	private String message;
	private int action;
	private ArrayList<String> ids;
	private ProgressDialog mProgressDialog;
	protected static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
	public Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == SENT_MESSAGE){
				showSentDialog();
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend_list);
		Bundle extras = getIntent().getExtras();
		String apiResponse = extras.getString("API_RESPONSE");
		action = extras.getInt("ACTION");
		if(action == SEND_MESSAGE)
			message = extras.getString("message");
		ids = new ArrayList<String>();
		try {
			jsonArray = new JSONArray(apiResponse);
		} catch (JSONException e) {
			return;
		}
		setListAdapter(new FriendListAdapter(this, jsonArray, false));		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id){
		super.onListItemClick(l, v, position, id);
		Friend f = (Friend)v.getTag();
		if(!f.getBox().isChecked()){
			f.getBox().setChecked(true);
			ids.add(f.getId());
		}
		else{
			f.getBox().setChecked(false);
			if(!ids.isEmpty())
				ids.remove(ids.indexOf(f.getId()));
		}
	}

	public void leftButtonClick(View v) {
		finish();
	}

	public void rightButtonClick(View v) {
		if(action==SEND_MESSAGE){
			mProgressDialog = ProgressDialog.show(this, "", "Enviando...");
			send(ids);
			mProgressDialog.dismiss();
			Log.e("enviados", "true");
		}
	}

	private void send(final ArrayList<String> uid){
		new Thread(new Runnable(){
			public void run(){
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(getString(R.string.send_message));
				if(httpclient != null && httppost != null){
					try {
						Iterator<String> it = ids.iterator();
						Date date = new Date();
						while(it.hasNext()){
							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
							nameValuePairs.add(new BasicNameValuePair("sender", Utility.userUID));
							nameValuePairs.add(new BasicNameValuePair("receiver", it.next()));
							nameValuePairs.add(new BasicNameValuePair("message", message));
							nameValuePairs.add(new BasicNameValuePair("date", FORMATTER.format(date)));
							httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							HttpResponse response = httpclient.execute(httppost);
							BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
							String sResponse;
							StringBuilder s = new StringBuilder();
							while ((sResponse = reader.readLine()) != null) 
								s = s.append(sResponse);
						}
						mHandler.sendEmptyMessage(SENT_MESSAGE);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}				
			}
		}).start();
	}
	
	private void showSentDialog(){
		sentDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		sentDialog.setContentView(R.layout.invite_dialog);
		ImageButton ib = (ImageButton) sentDialog.findViewById(R.id.invite_dialog_btn);
		ib.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				sentDialog.dismiss();
			}
		});
		TextView txt = (TextView) sentDialog.findViewById(R.id.invite_dialog_text);
		txt.setText("Se ha enviado un mensaje a tus amigos");
		sentDialog.show();
	}
}