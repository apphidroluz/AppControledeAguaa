package br.com.hidroluz.HidroluzWaterControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class Escolha_unidade extends Activity implements OnItemSelectedListener {

	private List<String> Unidades;
	private static final String PREF_NAME = "dadoslogin";
	String unidade;
	String nome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_escolha_unidade);
		restoreActionBar();

		Spinner sp = (Spinner) findViewById(R.id.spUnidades);
		
		SharedPreferences settings = this.getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);
		
		 Set<String> concatcSet = settings.getStringSet("UNI", null);
	     nome = settings.getString("NOME", null);

		 Unidades = new ArrayList<String>(concatcSet);	

		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item, Unidades);
		adapter.setDropDownViewResource(R.layout.spinner_item);
		sp.setAdapter(adapter);
		sp.setOnItemSelectedListener(this);

	}
	
	public void entrar(View v) {
		String[] n = unidade.split(";");
		
		for (int i = 0; i < n.length; i++) {
			System.out.println("OI " + n[i]);
		}

		String cod_cli = n[0];
		String nomfant_apel = n[1];
		String bloco = n[2];
		String unidade = n[3];
		
		SharedPreferences settings = getSharedPreferences(
				PREF_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings
				.edit();
		
	
		editor.putString("logado", "1");
		editor.putString("CodigoLogin", cod_cli);
		editor.putString("UnidadeLogin", unidade.trim());
		editor.putString("BlocoLogin", bloco.trim());
		editor.putString("NOME", nome);
		editor.putString("APELIDO", nomfant_apel);
	    editor.putString("TODAS", "NÃO");
	    editor.commit();


		Intent it = new Intent(this,
				DrawerActivity.class);
		Escolha_unidade.this.finish();
		it.putExtra("codigo", cod_cli);

		startActivity(it);
		
		
	}

	public void unidade(View v) {

			Intent it = new Intent(this,
					Cadastro_unidades.class);
			Escolha_unidade.this.finish();

			startActivity(it);
	}
	

	
	

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		getActionBar().setCustomView(R.layout.action_bar_layout);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
	    actionBar.setIcon(R.drawable.sf);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

	}

	

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {



	String a = "05050";

		unidade = parent.getItemAtPosition(position).toString();


		// Showing selected spinner item
		//Toast.makeText(parent.getContext(), "Selected: " + unidade,
		 //Toast.LENGTH_LONG).show();;;;;;
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stubb

	}
}
