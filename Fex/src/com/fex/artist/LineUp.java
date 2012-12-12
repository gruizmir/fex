package com.fex.artist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.fex.R;
import com.fex.utils.TopButtonActions;
import com.fex.utils.Utility;

@SuppressLint("HandlerLeak")
public class LineUp extends ListActivity implements TopButtonActions{
	private ArrayList<Artist> artistList;
	private String festival;
	private ProgressDialog progressDialog;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			setData();
			if(progressDialog!=null && progressDialog.isShowing())
				progressDialog.dismiss();
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lineup);
		progressDialog = ProgressDialog.show(this, "", getString(R.string.loading),true);
		festival = getIntent().getExtras().getString("festival_id");
		artistList = new ArrayList<Artist>();
		new Thread(new Runnable(){
			public void run(){
				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(getString(R.string.get_artist));
					if(httpclient != null && httppost != null && Utility.userUID != null && !Utility.userUID.equals("")){
						try {
							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
							nameValuePairs.add(new BasicNameValuePair("festival", festival));
							httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							HttpResponse response = httpclient.execute(httppost);
							BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
							String sResponse;
							StringBuilder s = new StringBuilder();
							while ((sResponse = reader.readLine()) != null) 
								s = s.append(sResponse);
							if(!s.toString().equals("") && !s.toString().equals("NULL")){
								String[] lista = s.toString().split("</br>");
								for(int i=0; i<lista.length; i++){
									String art[] = lista[i].split("%%");
									try{
										Artist a = new Artist();
										a.setId(Integer.parseInt(art[0]));
										a.setName(art[1]);
										a.setFestivalId(festival);
										artistList.add(a);
									}catch(NumberFormatException e){
									}
								}
								mHandler.sendEmptyMessage(0);
							}
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else{
					}
				} catch (Exception e) {
				}
			}
		}).start();
	}
	
	public void onResume(){
		super.onResume();
		setData();
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(getApplicationContext(), ArtistVideos.class);
		Bundle b = new Bundle();
		Artist a = artistList.get(position);
		b.putString("festival_id", a.getFestivalId());
		b.putString("artist_name", a.getName());
		b.putInt("artist_id", a.getId());
		i.putExtras(b);
		startActivity(i);		
	}

	public void leftButtonClick(View v) {
		finish();
	}

	public void rightButtonClick(View v) {
	}	

	private void setData(){
		setListAdapter(new ArtistAdapter(getApplicationContext(), artistList));
	}
}
