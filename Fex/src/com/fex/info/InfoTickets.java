package com.fex.info;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.fex.R;
import com.fex.utils.Festival;
import com.fex.utils.TopButtonActions;

public class InfoTickets extends Activity implements TopButtonActions{
	private String festivalId;
	private Festival fest;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		festivalId = getIntent().getExtras().getString("festival_id");
		fest = new Festival(this, festivalId);
		setContentView(R.layout.info_tickets);
		TextView texto = (TextView) findViewById(R.id.info_tickets_text);
		String message = "";
		for(int i=0; i<fest.getCantEntradas();i++){
			String[] data = fest.getEntradas(i);
			message = message + data[0].toUpperCase() + " " + data[1].toUpperCase()
					+ "\n$" + data[2] + "+ Cargo por Servicio" + "\n\n";
		}
		texto.setText(message);
	}

	public void leftButtonClick(View v) {
		finish();
	}

	public void rightButtonClick(View v) {
		
	}
}