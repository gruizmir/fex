package com.friendstivals.utils;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class Festival {
	private String id;
	private String name;
	private String date;
	private String start;
	private String end;
	private String location;
	private String genLocation;
	private ArrayList<Entradas> entradas;
	
	public Festival(Context cont, String id){
		this.id = id;
		String[] data=null;
		try{
			data = cont.getResources().getStringArray(cont.getResources().getIdentifier(id, "array", "com.friendstivals"));
		}catch(Exception e){
			Log.e("problema", e.getMessage());
		}
		if(data != null){
			name = data[0];
			genLocation=data[1];
			date = data[2];
			location = data[3];
			if(data[4].equals("null")){
				start = "No disponible";
				end = "No disponible";
			}
			else{
				start = data[4];
				end = data[5];
			}
				
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}	
	
	public String[] getEntradas(int position){
		String ent[]= new String[2];
		if(position<entradas.size()){
			ent[0] = entradas.get(position).getType();
			ent[1] = entradas.get(position).getValue();
			return ent;
		}
		else
			return null;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getGenLocation() {
		return genLocation;
	}

	public void setGenLocation(String genLocation) {
		this.genLocation = genLocation;
	}
}

class Entradas{
	private String type;
	private String value;
	private ArrayList<String> places;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public ArrayList<String> getPlaces() {
		return places;
	}
	public void setPlaces(ArrayList<String> places) {
		this.places = places;
	}
}