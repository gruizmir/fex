package com.friendstivals.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.util.Log;

public class Festival {
	private String id;
	private String name;
	private String date;
	private String realDate;
	private String start;
	private String end;
	private String location;
	private String genLocation;
	private String country;
	private String splashMessage;
	private ArrayList<Entradas> entradas;
	protected static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	public Festival(Context cont, String id){
		this.id = id;
		entradas = new ArrayList<Entradas>();
		String[] data=null;
		try{
			data = cont.getResources().getStringArray(cont.getResources().getIdentifier(id, "array", "com.friendstivals"));
		}catch(Exception e){
			Log.e("problema", e.getMessage());
		}
		if(data != null){
			name = data[0];
			genLocation=data[1];
			country = data[2];
			date = data[3];
			realDate = data[4];
			location = data[5];
			if(data[6].equals("null")){
				start = "No disponible";
				end = "No disponible";
			}
			else{
				start = data[6];
				end = data[7];
			}
			splashMessage = data[8];
			for(int i=9; i<data.length; i++){
				String[] temp = data[i].split(":")[1].split("/");
				Entradas e = new Entradas();
				e.setTipoVenta(temp[0]);
				e.setType(temp[1]);
				e.setValue(temp[2]);
				entradas.add(e);
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
		String ent[]= new String[3];
		if(position<entradas.size()){
			ent[0] = entradas.get(position).getTipoVenta();
			ent[1] = entradas.get(position).getType();
			ent[2] = entradas.get(position).getValue();
			return ent;
		}
		else
			return null;
	}
	
	public int getCantEntradas(){
		return entradas.size();
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

	public String getSplashMessage() {
		return splashMessage;
	}

	public void setSplashMessage(String splashMessage) {
		this.splashMessage = splashMessage;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRealDate() {
		return realDate;
	}

	public void setRealDate(String realDate) {
		this.realDate = realDate;
	}

	public boolean isActive() {
		Date date = new Date();
		Date nDate;
		try {
			nDate = FORMATTER.parse(realDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		if(nDate.getTime() - date.getTime() <= 1209600000 &&  nDate.getTime() - date.getTime() >= 0)
			return true;
		else
			return false;
	}

}

class Entradas{
	private String tipoVenta;
	private String type;
	private String value;
	
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
	public String getTipoVenta() {
		return tipoVenta;
	}
	public void setTipoVenta(String tipoVenta) {
		this.tipoVenta = tipoVenta;
	}
}