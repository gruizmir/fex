package com.fex.info;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.fex.R;
import com.fex.utils.Festival;
import com.fex.utils.TopButtonActions;

public class InfoDates extends Activity implements TopButtonActions{
	private String festivalId;
	private Festival fest;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		festivalId = getIntent().getExtras().getString("festival_id");
		fest = new Festival(this, festivalId);
		setContentView(R.layout.info_dates);
		TextView texto = (TextView) findViewById(R.id.info_date_text);
		texto.setText(fest.getDate() +  
				"\n\n CHECK IN\n " + fest.getStart() + 
				"\n\n SOLO CHECK OUT\n" + fest.getEnd());
	}

	public void leftButtonClick(View v) {
		finish();
	}

	public void rightButtonClick(View v) {
		
	}
}