package com.friendstivals.playlist;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.friendstivals.R;

public class PlaylistAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Song holder;
	ArrayList<Song> list;

	public PlaylistAdapter(Context cont, ArrayList<Song> list) {
		this.list = list;		
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
			hView = mInflater.inflate(R.layout.playlist_item, null);
			TextView txtTitle = (TextView)hView.findViewById(R.id.song_title);
			txtTitle.setText(holder.getTitle());
			
			TextView txtArtist= (TextView)hView.findViewById(R.id.song_artist);
			txtArtist.setText(holder.getArtist());
			
			TextView txtAlbum = (TextView)hView.findViewById(R.id.song_album);
			txtAlbum.setText(holder.getAlbum());
		}
		hView.setTag(holder);
		return hView;
	}
}