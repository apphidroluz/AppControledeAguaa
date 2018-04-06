package br.com.hidroluz.HidroluzWaterControl;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class Sair extends Fragment {

	public void onStart() {
		super.onStart();

		teste(getActivity());

		/*
		 * Intent intent = new Intent(getActivity(), MainActivity.class);
		 * startActivity(intent);
		 */
	}

	protected void Init() throws Throwable {

	}

	public void confirmExit(Context activity) {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
		alertbox.setMessage(activity
				.getString(R.string.message_closeApplication));

		alertbox.setPositiveButton(activity.getString(R.string.label_Yes),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						  Intent intent = new Intent(getActivity(), MainActivity.class);
						  startActivity(intent);
					}
				});

		alertbox.setNegativeButton(activity.getString(R.string.label_No),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
			
				});

		alertbox.show();
	}
	
	

	public void teste(Context activity) {
	    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
	    alertDialogBuilder.setTitle("Atenção");
	 
	    alertDialogBuilder.setMessage("Deseja realmente cancelar o pedido?").setCancelable(false).setPositiveButton("Sim",
	        new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	  Intent intent = new Intent(getActivity(), MainActivity.class);
					  startActivity(intent);
	            }
	        })
	        .setNegativeButton("Não",
	        new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	            }
	        });
	 
	    AlertDialog alertDialog = alertDialogBuilder.create();
	    alertDialog.show();
	}
	
	
	

}
