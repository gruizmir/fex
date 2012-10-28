package com.friendstivals;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

/**
 * Vista asociada al envio de invitaciones a otras personas.
 * @author astom
 *
 */
public class InviteView extends Activity {
	
	LinearLayout customList;
	
	//Cambiar a visible si es que se quiere mostrar los botones.
	int VISIBILITY_ADD_BUTTONS = LinearLayout.INVISIBLE;
	//Cantidad inicial de textbox a mostrar
	int POPULATE_LIST = 5;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.festival_invite_list);
		customList = (LinearLayout)findViewById(R.id.customInviteList);
		
		populateList(POPULATE_LIST);
		customList = (LinearLayout)findViewById(R.id.inviteButtonsMoreFriends);
		customList.setVisibility(VISIBILITY_ADD_BUTTONS);
		
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
	 * Logica asociada al envio de las invitaciones a cada persona especificada
	 * en los textboxs.
	 * @param view
	 */
	public void onClickSend(View view){
		
	}
	
	/**
	 * Se cierra la ventana si es que el usuario no desea enviar invitaciones.
	 * @param view
	 */
	public void onClickLater(View view){
		this.finish();
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

}
