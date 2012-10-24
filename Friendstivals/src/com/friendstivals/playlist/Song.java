package com.friendstivals.playlist;

public class Song {
	private String title;
	private String id;
	private String artist;
	private String album;
	private String link;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String name) {
		this.title= name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
}
