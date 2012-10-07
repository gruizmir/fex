package com.friendstivals.utils;

import java.util.Date;

import android.widget.ImageView;
import android.widget.TextView;

public class Event extends Object{
	private ImageView imagen;
	private TextView nombre;
	private Date fecha;
	private TextView identificador;
	private ImageView foto;
	
	public Event(ImageView photo, ImageView img, TextView name, TextView id, Date date){
		super();
		this.imagen = img;
		this.nombre = name;
		this.identificador = id;
		this.fecha = date;
		this.foto = photo;
	}
	
	public TextView getName(){
		return this.nombre;
	}
	
	public ImageView getImg(){
		return this.imagen;
	}
	
	public TextView getID(){
		return this.identificador;
	}
	
	public Date getDate(){
		return this.fecha;
	}
	
	public ImageView getPhoto(){
		return this.foto;
	}
}