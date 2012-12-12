package com.fex.artist;

import android.graphics.Bitmap;
import android.text.Html;

public class Artist {
	private String name;
	private int id;
	private String video1;
	private String video2;
	private String video3;
	private String videoName1;
	private String videoName2;
	private String videoName3;
	private String festivalId;
	private Bitmap img;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name=Html.fromHtml(name).toString();
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
	public String getVideo1() {
		return video1;
	}
	public void setVideo1(String video1) {
		this.video1 = video1;
	}
	public String getVideo2() {
		return video2;
	}
	public void setVideo2(String video2) {
		this.video2 = video2;
	}
	public String getVideo3() {
		return video3;
	}
	public void setVideo3(String video3) {
		this.video3 = video3;
	}
	public String getFestivalId() {
		return festivalId;
	}
	public void setFestivalId(String festivalId) {
		this.festivalId = festivalId;
	}
	public String getVideoName1() {
		return videoName1;
	}
	public void setVideoName1(String videoName1) {
		this.videoName1 = Html.fromHtml(videoName1).toString();
	}
	public String getVideoName2() {
		return videoName2;
	}
	public void setVideoName2(String videoName2) {
		this.videoName2 = Html.fromHtml(videoName2).toString();
	}
	public String getVideoName3() {
		return videoName3;
	}
	public void setVideoName3(String videoName3) {
		this.videoName3 = Html.fromHtml(videoName3).toString();
	}
	
}
