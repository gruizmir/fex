package com.fex;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class Friend{
	private ImageView profile_pic;
	private TextView name;
	private CheckBox box;
	private Button btn;
	private String id;
	
	public TextView getName() {
		return name;
	}
	public void setName(TextView name) {
		this.name = name;
	}
	public ImageView getProfile_pic() {
		return profile_pic;
	}
	public void setProfile_pic(ImageView profile_pic) {
		this.profile_pic = profile_pic;
	}
	public CheckBox getBox() {
		return box;
	}
	public void setBox(CheckBox box) {
		this.box = box;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Button getBtn() {
		return btn;
	}
	public void setBtn(Button btn) {
		this.btn = btn;
	}	
}