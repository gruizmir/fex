package com.friendstivals.artist;

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
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.friendstivals.R;
import com.friendstivals.utils.TopButtonActions;
import com.friendstivals.utils.Utility;

@SuppressLint("HandlerLeak")
public class ArtistVideos extends Activity implements TopButtonActions{
	private Artist artista;
	private String[] videos;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			setData();
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.artist_videos);
		artista = new Artist();
		Bundle b = getIntent().getExtras();
		artista.setId(b.getInt("artist_id"));
		artista.setName(b.getString("artist_name"));
		TextView textName = (TextView) findViewById(R.id.videos_artist_name);
		textName.setText(artista.getName());
		new Thread(new Runnable(){
			public void run(){
				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(getString(R.string.get_videos));
					if(httpclient != null && httppost != null && Utility.userUID != null && !Utility.userUID.equals("")){
						try {
							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
							nameValuePairs.add(new BasicNameValuePair("id", Integer.toString(artista.getId())));
							httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							HttpResponse response = httpclient.execute(httppost);
							BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
							String sResponse;
							StringBuilder s = new StringBuilder();
							while ((sResponse = reader.readLine()) != null) 
								s = s.append(sResponse);
							if(!s.toString().equals("") && !s.toString().equals("NULL")){
								videos = s.toString().split(",");
								String[] data = videos[0].split("\\+\\+");
								artista.setVideo1(data[0]);
								artista.setVideoName1(data[1]);
								data = videos[1].split("\\+\\+");
								artista.setVideo2(data[0]);
								artista.setVideoName2(data[1]);
								data = videos[2].split("\\+\\+");
								artista.setVideo3(data[0]);
								artista.setVideoName3(data[1]);
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

	private void setData(){
		TextView video1 = (TextView) findViewById(R.id.videos_video1);
		video1.setText(artista.getVideoName1());
		video1.setTag(artista.getVideo1());
		video1.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((String)v.getTag())));
			}
		});
		
		TextView video2 = (TextView) findViewById(R.id.videos_video2);
		video2.setText(artista.getVideoName2());
		video2.setTag(artista.getVideo2());
		video2.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((String)v.getTag())));
			}
		});
		
		TextView video3 = (TextView) findViewById(R.id.videos_video3);
		video3.setText(artista.getVideoName3());
		video3.setTag(artista.getVideo3());
		video3.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((String)v.getTag())));
			}
		});
	}
	
	public void leftButtonClick(View v) {
		finish();
	}

	public void rightButtonClick(View v) {
	}	
}