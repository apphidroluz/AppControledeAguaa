package br.com.hidroluz.HidroluzWaterControl;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class Validacao {
	
	
	public boolean validateNotNull(View pView, String pMessage) {
		  if (pView instanceof EditText) {
		   EditText edText = (EditText) pView;
		   Editable text = edText.getText();
		   if (text != null) {
		    String strText = text.toString();
		    if (!TextUtils.isEmpty(strText)) {
		     return true;
		    }
		   }
		   edText.setError(pMessage);
		   edText.setFocusable(true);
		   edText.requestFocus();
		   return false;
		  }
		  return false;
		 }

}
