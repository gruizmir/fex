package com.friendstivals;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class MessageDialog extends Dialog {

	public MessageDialog(Context context) {
		super(context);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.message_dialog);
	}

	public MessageDialog(Context context, int theme) {
		super(context, theme);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.message_dialog);
	}
}
