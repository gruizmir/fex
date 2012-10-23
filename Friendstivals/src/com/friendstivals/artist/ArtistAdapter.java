package com.friendstivals.artist;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.friendstivals.R;

public class ArtistAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Artist holder;
	ArrayList<Artist> list;
	
	public ArtistAdapter(Context cont, ArrayList<Artist> list) {
		this.list = list;;		
		mInflater = LayoutInflater.from(cont);
	}

	public int getCount() {
		if(list==null)
			return 0;
		else
			return list.size();
	}

	public Object getItem(int position) {
		return holder;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		holder = list.get(position);
		View hView = convertView;
		if (convertView == null) {
			hView = mInflater.inflate(R.layout.artist_list_item, null);
			TextView txtView = (TextView)hView.findViewById(R.id.artist_name);
			txtView.setText(holder.getName());
		}
		hView.setTag(holder);
		return hView;
	}
}