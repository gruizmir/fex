package com.fex;

import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fex.utils.TopButtonActions;

/**
 * Vista asociada al envio de invitaciones a otras personas.
 * @author astom
 *
 */
public class InviteView extends Activity implements TopButtonActions{
	private final int REQUEST_MAIL = 5;
	private LinearLayout customList;
	protected Dialog mDialog;
	//Cambiar a visible si es que se quiere mostrar los botones.
	private boolean INVISIBLE_ADD_BUTTONS = true;
	//Cantidad inicial de textbox a mostrar
	private int POPULATE_LIST = 5;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.festival_invite_list);
		customList = (LinearLayout)findViewById(R.id.customInviteList);

		populateList(POPULATE_LIST);
		if(INVISIBLE_ADD_BUTTONS){
			customList = (LinearLayout)findViewById(R.id.inviteButtonsMoreFriends);
			customList.setVisibility(LinearLayout.INVISIBLE);
		}

	}

	/**
	 * Rellena la lista con una cierta cantidad de textboxs
	 * @param cantidad : Cantidad inicial de textboxs
	 */
	public void populateList(int cantidad){
		for(int i=0;i<cantidad;i++){
			addItemToList();
		}
	}

	/**
	 * Agrega un textbox o mas si es que el usario desea enviar mas invitaciones
	 * @param view
	 */
	public void onClickAdd(View view){
		addItemToList();
	}

	/**
	 * Se agrega una invitacion seleccionando a una persona en facebook
	 * @param view
	 */
	public void onClickFacebook(View view){

	}

	/**
	 * Logica asociada a agregar textbox a la lista de invitaciones
	 */
	public void addItemToList(){
		LinearLayout newItem;
		newItem = new LinearLayout(this);
		LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.festival_invite_item, newItem);
		customList.addView(newItem,customList.getChildCount()-1);
	}

	private boolean isValidEmail(String email) {
		Pattern pattern = Patterns.EMAIL_ADDRESS;
		return pattern.matcher(email).matches();
	}

	public void leftButtonClick(View v) {
		this.finish();		
	}

	public void rightButtonClick(View v) {

		LinearLayout customList = (LinearLayout)findViewById(R.id.customInviteList); 
		int count = customList.getChildCount();
		String dest = "";
		for(int i=0; i<count-1; i++){
			Log.w("a", ((LinearLayout)((LinearLayout)customList.getChildAt(i)).getChildAt(0)).getChildAt(0).toString());
			EditText et = (EditText)((LinearLayout)((LinearLayout)customList.getChildAt(i)).getChildAt(0)).getChildAt(0);
			if(!et.getText().equals("")){
				String aux = et.getText().toString();
				if(isValidEmail(aux)){
					dest += ","+aux;
				}
			}
		}
		if(!dest.equals("")){
			String[] destinatarios = dest.split(",");
			if(destinatarios.length>0){
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL, destinatarios);
				i.putExtra(Intent.EXTRA_SUBJECT, "Juntémonos en Fex");
				String msg = "<!DOCTYPE html><html><body>Te invito a probar Fex.</br> Descárgala desde <a href=\"http://23.23.170.228/15mo1q5ang/Fex.apk\">aquí</a></body></html>";
				i.putExtra(Intent.EXTRA_TEXT   , Html.fromHtml(msg));
				try {
					this.startActivityForResult(Intent.createChooser(i, "Send mail..."), REQUEST_MAIL);
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode==REQUEST_MAIL){
			mDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
			mDialog.setContentView(R.layout.invite_dialog);
			ImageButton ib = (ImageButton) mDialog.findViewById(R.id.invite_dialog_btn);
			ib.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					mDialog.dismiss();
				}
			});
			mDialog.show();
		}
	}
}
