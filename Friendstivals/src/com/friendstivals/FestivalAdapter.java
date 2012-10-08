package com.friendstivals;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.friendstivals.utils.Event;

public class FestivalAdapter extends ArrayAdapter<Event> {

	public Context context;
	private ArrayList<Event> eventos;
	int layoutResourceId;

	public FestivalAdapter(Context c, int layoutResourceId, ArrayList<Event> events) {
		super(c, layoutResourceId, events);
		this.layoutResourceId = layoutResourceId;
		this.context = c;
		this.eventos = events;
		this.notifyDataSetChanged();
	} 

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		FestivalHolder holder = null;
		if(convertView == null){
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.festival_list_item, null);
			holder = new FestivalHolder();
			holder.imgIcon = (ImageView)view.findViewById(R.id.fest_calendar_icon);
			holder.titleImg= (ImageView)view.findViewById(R.id.fest_festival_icon);
			view.setTag(holder);
		}
		else
		{
			holder = (FestivalHolder)view.getTag();
		}
		Event e = eventos.get(position);
		holder.titleImg= e.getPhoto(); 
		holder.imgIcon =  e.getImg();
		return view;
	}

	static class FestivalHolder
	{
		ImageView imgIcon;
		ImageView titleImg;
	}
}

