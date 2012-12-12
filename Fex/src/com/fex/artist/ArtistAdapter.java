package com.fex.artist;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fex.R;

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
		View view = convertView;
		Artist holder = null;
		TextView name;
		if(convertView == null){
			view = mInflater.inflate(R.layout.lineup_item, null);
			holder = new Artist();
			holder = list.get(position);
			name = (TextView)view.findViewById(R.id.lineup_name);
			view.setTag(holder);
		}
		else
		{
			holder = (Artist)view.getTag();
			name = (TextView)view.findViewById(R.id.lineup_name);
		}
		Artist e = list.get(position);
		name.setText(e.getName()); 
		return view;
	}
}