package br.com.hidroluz.HidroluzWaterControl;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class DrawerActivity extends Activity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private static final String PREF_NAME = "dadoslogin";
	private String codigo;
	private String unidade;
	private String bloco;
	private String nome;
	private String email;
	private String apelido;
	private TextView txtNome;
	private TextView txtApelido;
	private TextView txtBloco;
	private TextView txtUnidade;
	Fragment objFragment = null;

	public static ProgressDialog progressDialog;

	private Funcoes funcao = new Funcoes();

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer);

		SharedPreferences settings = this.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);

		codigo = settings.getString("CodigoLogin", "");
		unidade = settings.getString("UnidadeLogin", "");
		bloco = settings.getString("BlocoLogin", "");
		nome = settings.getString("NOME", "");
		apelido = settings.getString("APELIDO", "");

		txtNome = (TextView) findViewById(R.id.nomeCliente);
		txtApelido = (TextView) findViewById(R.id.apelido);
		txtBloco = (TextView) findViewById(R.id.bloco);
		txtUnidade = (TextView) findViewById(R.id.unidade);

		String nome1 = "Olá, " + nome + "!";
		String bl = "Bloco: " + bloco;
		String un = "Unidade: " + unidade;
		txtNome.setText(nome1);
		txtApelido.setText(apelido);
		txtBloco.setText(bl);
		txtUnidade.setText(un);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments

		String title = getString(R.string.app_name);

		switch (position) {
		case 0:

			objFragment = new ListadeLeituras();

			break;
		case 1:

			objFragment = new GraficoBarrasValores();

			break;
		case 2:

			objFragment = new GraficoConsumo();

			break;
		case 3:

			objFragment = new Editar_Cadastro();

			break;
		case 4:

			objFragment = new Noticias();

			break;

		case 5:

			objFragment = new Ajuda ();

			break;

		
		case 6:
			Intent it = new Intent(this, Escolha_unidade.class);
			DrawerActivity.this.finish();

			startActivity(it);

			break;

		case 7:
			onDetach();

			break;

		default:
			break;
		}

		if (objFragment != null) {

			FragmentManager fragmentManager = getFragmentManager();

			fragmentManager.beginTransaction()
					.replace(R.id.container, objFragment).addToBackStack(null)
					.commit();

			ActionBar actionBar = getActionBar();
			actionBar.setIcon(R.drawable.sf);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setTitle(mTitle);

		}

	}

	public void onSectionAttached(int number) {

	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		getActionBar().setCustomView(R.layout.action_bar_layout);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setIcon(null);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(mTitle);
	}

	public void onDetach() {

		new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.SUCCESS_TYPE).
				setTitleText("Saida").setContentText("Você realmente deseja sair ?")
				.setConfirmText("Sim").setCancelText("Não")
				.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {

				SharedPreferences settings = getSharedPreferences(
						PREF_NAME, 0);
				settings.edit().remove("logado").commit();
				settings.edit().remove("UNI").commit();

				String adm = settings.getString("ADM", "");
				DrawerActivity.this.finish();

				if (adm.equalsIgnoreCase("1")) {
					startActivity(new Intent(getBaseContext(),
							Drawer_adm.class));

				} else {
					startActivity(new Intent(getBaseContext(),
							MainActivity.class));
				}
			}
		}).showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sDialog) {
						sDialog.cancel();
					}
				}).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.drawer, menu);
			// getActionBar().se
			restoreActionBar();
			return false;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		return super.onOptionsItemSelected(item);
	}

	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((DrawerActivity) activity).onSectionAttached(getArguments()
					.getInt(ARG_SECTION_NUMBER));
		}
	}

}
