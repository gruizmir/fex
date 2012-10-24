package com.friendstivals.artist;

import android.graphics.Bitmap;

public class Artist {
	private String name;
	private int id;
	private Bitmap img;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Bitmap getImg() {
		return img;
	}
	public void setImg(Bitmap img) {
		this.img = img;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
