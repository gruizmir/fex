package com.fex.templates;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.fex.R;

/**
 * @author Astom
 *
 *	customDialog simula un Dialog, extiende a un activity para asi poder enviar
 *	y recibir datos, y facilitar el uso de botones dentro del dialog.	
 */
public class customDialog extends Activity{
	//Intents del sender (quien llamo al customDialog) y de la respuesta del custom dialog.
	Intent senderIntent;
	Intent dialogIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	 {
        super.onCreate(savedInstanceState);
     // Las siguientes dos lineas nos permiten controlar los touch dentro del custom dialog (no borrar).
        getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL, LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
     // Set del XML a usuar.
        this.setContentView(R.layout.send_message);
     // Set del nombre del Custom Dialog, cambiar a conveniencia.
        this.setTitle("Custom Dialog");
     // Obtener el intent del sender.   
        senderIntent = getIntent();
        
        /*Agregar aqui cosas adicionales que se quieran hacer */
	 }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (MotionEvent.ACTION_OUTSIDE == event.getActionMasked()) {
			/*Descomentar las siguientes dos lineas si se quiere cerrar el dialog
			 * cuando se preciona en el background.
			 */
			//finish();
		    //return true;
		    }
		return super.onTouchEvent(event);
	  }
	
	/**
	 * Enviar los datos del customDialog de ser necesario (usar el dialogIntent).
	 */
	public void buttonOk(View v) {
		/*Agregar aqui cosas adicionales que se quieran hacer */
		
		this.setResult(Activity.RESULT_OK, this.dialogIntent);
		this.finish();
	}
	
	/**
	 * Terminar el dialog.
	 */
	public void buttonCancel(View v){
		/*Agregar aqui cosas adicionales que se quieran hacer */
		
    	this.setResult(Activity.RESULT_CANCELED, this.dialogIntent);
		this.finish();
	}
	
	@Override
    public void onBackPressed() {
		/*Agregar aqui cosas adicionales que se quieran hacer */
		
		super.onBackPressed();
    	this.setResult(Activity.RESULT_CANCELED, this.dialogIntent);	
        this.finish();
    }	

	/*Agregar aqui las declaraciones de nuevos botones*/
	
	/*Agregar aqui las declaraciones de nuevos metodos*/
	
	@Override
	public void onDestroy() {
		/*Agregar aqui cosas adicionales que se quieran hacer */
		
		super.onDestroy();
	}

}

/*
 *	RECORDATORIOS:
 *	(1) Agregar la siguiente linea en el android manifest:
 *		<activity android:name="RUTA_COMPLETA_DE_ESTA_ACTIVITY" android:screenOrientation="portrait" android:theme="@android:style/Theme.Dialog"></activity>
 *		Donde se debe reemplazar RUTA_COMPLETA_DE_ESTA_ACTIVITY, con algo como lo siguiente: com.sblitz.customdialog.customDialog
 *	(2) Los metodos buttonOk y buttonCancel son metodos llamados desde botones en el archivo XML.
 *	(3) En lo posible modificar solo las areas marcadas como modificables.
 *
 */
