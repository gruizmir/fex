package com.friendstivals;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.friendstivals.utils.Festival;

/**
 * SplashScreen que muestra a los sponsors del festival
 * @author astom
 *
 */
public class SplashScreen extends Activity{

	ArrayList<Integer> imageIDs;
	private Handler mHandler;
	Intent nextWindow;
	
	boolean AUTO_CLOSE = true;
	int TIME_CLOSE = 3000;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.festival_splash_screen);

		mHandler = new Handler();

		Intent intent = getIntent();
		String id = intent.getStringExtra("festival_id");
		
		Festival festival = new Festival(this, "misteryland");
		TextView sponsorsMessage = (TextView)findViewById(R.id.sponsorMessage);
		TextView festivalMessage = (TextView)findViewById(R.id.festivalMessage);

		sponsorsMessage.setText("welcomes you to the\nbiggest festival in Chile:");
		festivalMessage.setText(festival.getName());

		imageIDs = new ArrayList<Integer>();
		imageIDs.add(R.drawable.ic_launcher);
		imageIDs.add(R.drawable.ic_launcher);

		GridView sponsors = (GridView)findViewById(R.id.sponsorsView);
		sponsors.setAdapter(new ImageAdapter(this));
		sponsors.setVerticalScrollBarEnabled(false);
		
		nextWindow = intent;
		nextWindow.setClass(this, CustomMap.class);
		mHandler.postDelayed(mUpdateTimeTask, TIME_CLOSE);
		
	}

	/**
	 * Runnable encargado de cerrar automaticamente la aplicacion
	 */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			if(AUTO_CLOSE){
				startActivity(nextWindow);
			}
		}
	};

	/**
	 * Se cierra la vista si el usuario preciona el boton festivals
	 * @param view
	 */
	public void onClickFestivals(View view){
		AUTO_CLOSE = false;
		this.finish();
	}

	/**
	 * Adapter para el gridView que muestra los sponsors
	 * @author astom
	 *
	 */
	public class ImageAdapter extends BaseAdapter 
	{
		private Context context;

		public ImageAdapter(Context c) 
		{
			context = c;
		}

		//---returns the number of images---
		public int getCount() {
			return imageIDs.size();
		}

		//---returns the ID of an item--- 
		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		//---returns an ImageView view---
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(context);
				imageView.setScaleType(ImageView.ScaleType.CENTER);
				imageView.setPadding(5, 5, 5, 5);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setImageResource(imageIDs.get(position));
			return imageView;
		}
	}    
}
