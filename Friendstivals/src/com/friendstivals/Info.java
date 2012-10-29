package com.friendstivals;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.friendstivals.utils.Festival;
import com.friendstivals.utils.FriendsViewActions;

public class Info extends Activity implements FriendsViewActions{
	private String festivalId;
	private Festival fest;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		festivalId = getIntent().getExtras().getString("festival_id");
		fest = new Festival(this, festivalId);
		setContentView(R.layout.info);
	}

	public void leftButtonClick(View v) {
		finish();
	}

	public void rightButtonClick(View v) {
		
	}
	
	public void showDates(View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Fechas y horarios");
		builder.setMessage(" Fecha: "+ fest.getDate() +  
				"\n Apertura de puertas: " + fest.getStart() + 
				"\n Finalización conciertos:" + fest.getEnd());
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

			public void onClick(DialogInterface dialog, int which) {
			}

		});
		builder.show();
	}
	
	public void showTickets(View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Entradas");
		String message = "";
		for(int i=0; i<fest.getCantEntradas();i++){
			String[] data = fest.getEntradas(i);
			message = message + "Venta: " + data[0] + "\nTipo: " + data[1]
					+ "\nPrecio: $" + data[2] + "\n\n";
		}
		builder.setMessage(message);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

			public void onClick(DialogInterface dialog, int which) {
			}

		});
		builder.show();
	}
	
	public void showLocation(View v){
		Intent i = new Intent(getApplicationContext(), LocationMap.class);
		Bundle b = new Bundle();
		b.putString("address", fest.getLocation() + ", " + fest.getGenLocation());
		b.putString("festival_name", fest.getName());
		i.putExtras(b);
		startActivity(i);
	}
}