package br.com.hidroluz.HidroluzWaterControl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class Login_ADM extends Fragment implements Runnable {
	private static final String PREF_NAME = "dadoslogin";
	private String usuario;
	private String passaword;
	private String[] valores;
	private String razsoc_nome;
	private String bloco;
	private String unidade;
	private String codigo;
	private String login;
	private ListView listView;
	private TemplateListaUnidades adapterListView;
	private ArrayList<ItemListUnidades> itens;
	private TextView razsoc;
	View rootView;
	static ProgressDialog pgd;
	private SweetAlertDialog pDialog;


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_login__adm, container,
				false);
		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		// restoreActionBar();

		SharedPreferences settings = this.getActivity().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);

		usuario = settings.getString("USUARIO", "");
		passaword = settings.getString("SENHA", "");

		usuario = usuario.toUpperCase();

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

		}

		try {

			importar();

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void run() {
		try {

			listar_unidades();

		} catch (Exception ex) {

			/*
			 * getFragmentManager().beginTransaction() .replace(R.id.container,
			 * new Tela_erro()) .addToBackStack(null).commit();
			 */

			Log.d("Erro = ", "Erro = " + ex.getMessage());
		}

	}

	public void importar() {

		pDialog = new SweetAlertDialog(Login_ADM.this.getActivity(),
				SweetAlertDialog.PROGRESS_TYPE);
		pDialog.setTitleText("Aguarde !!!");
		pDialog.setContentText("Carregando Unidades");
		pDialog.setCancelable(false);
		pDialog.show();

		new Thread(Login_ADM.this).start();

	}

	// public void restoreActionBar() {
	// ActionBar actionBar = getActionBar();
	// getActionBar().setCustomView(R.layout.action_bar_layout);
	// actionBar.setDisplayShowTitleEnabled(false);
	// actionBar.setDisplayShowCustomEnabled(true);
	// actionBar.setIcon(null);
	// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	//
	// }

	private void listar_unidades() throws InterruptedException {

		InputStream in = null;
		Conexao c = new Conexao();

		try {
			in = OpenHttpConnection("http://android.hidroluz.com.br/php/login_adm_novo_unidades.php?usuario="
					+ usuario + "&senha=" + passaword);

			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				doc = db.parse(in);
			} catch (ParserConfigurationException e) {
				getFragmentManager().beginTransaction()
						.replace(R.id.container, new Tela_erro())
						.addToBackStack(null).commit();
				e.printStackTrace();
			} catch (Exception e) {
				getFragmentManager().beginTransaction()
						.replace(R.id.container, new Tela_erro())
						.addToBackStack(null).commit();
			}
			doc.getDocumentElement().normalize();

			NodeList itemNodes = doc.getElementsByTagName("resposta");

			for (int i = 0; i < itemNodes.getLength(); i++) {
				Node itemNode = itemNodes.item(i);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
					NodeList numero_meses = doc.getElementsByTagName("valores");
					Element meses = (Element) numero_meses.item(0);
					itens = new ArrayList<ItemListUnidades>();

					for (int j = 0; j < numero_meses.getLength(); j++) {
						meses = (Element) numero_meses.item(j);

						razsoc_nome = getChildTagValue(meses, "nomfant_apel");
						bloco = getChildTagValue(meses, "bloco");
						unidade = getChildTagValue(meses, "unidade");
						codigo = getChildTagValue(meses, "codigo");

						ItemListUnidades item = new ItemListUnidades(
								getChildTagValue(meses, "razsoc_nome"),
								getChildTagValue(meses, "bloco"),
								getChildTagValue(meses, "unidade"),
								getChildTagValue(meses, "codigo"));

						itens.add(item);

					}

				}
			}

			h1.sendEmptyMessage(2);

		} catch (Exception e1) {
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new Tela_erro())
					.addToBackStack(null).commit();
		}

	}

	private void createListView() {
		// Criamos nossa lista que preenchera o ListView
		// Cria o adapter

		final SharedPreferences settingss = this.getActivity()
				.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		try {

			adapterListView = new TemplateListaUnidades(this.getActivity(),
					itens);

			listView = (ListView) getView().findViewById(R.id.listView2);
			// Define o Adapter
			listView.setAdapter(adapterListView);

			listView.setCacheColorHint(Color.TRANSPARENT);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					unidade = (String) ((TextView) view
							.findViewById(R.id.unid_unidade)).getText()
							.toString();

					bloco = (String) ((TextView) view
							.findViewById(R.id.unid_bloco)).getText()
							.toString();

					codigo = (String) ((TextView) view
							.findViewById(R.id.unid_codigo)).getText()
							.toString();

					new SweetAlertDialog(Login_ADM.this.getActivity(), SweetAlertDialog.SUCCESS_TYPE)
							.setTitleText("Visualizar")
							.setContentText("Você deseja Visualizar as leiuturas dessa unidade ?")
							.setConfirmText("Sim").setCancelText("Não")
							.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {

							SharedPreferences.Editor editor = settingss
									.edit();

							// editor.putString("logado", "1");
							editor.putString("ADM", "1");
							editor.putString("CodigoLogin",
									codigo);
							editor.putString("UnidadeLogin",
									unidade.trim());
							editor.putString("BlocoLogin",
									bloco.trim());
							editor.putString("NOME", "Usuário");
							editor.putString("APELIDO",
									razsoc_nome);
							editor.putString("TODAS", "NÃO");
							editor.commit();

							getActivity().finish();
							Intent intent = new Intent(
									getActivity(),
									DrawerActivity.class);
							startActivity(intent);

							intent.putExtra("codigo", codigo);

							startActivity(intent);

						}
					}).showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sDialog) {
									sDialog.cancel();
								}
							}).show();

				}
			});

			razsoc = (TextView) getView().findViewById(R.id.unid_RazSoc);

			razsoc.setText(razsoc_nome);

			calculeHeightListView();

		} catch (Exception e) {

			getFragmentManager().beginTransaction()
					.replace(R.id.container, new Tela_erro())
					.addToBackStack(null).commit();
		}

	}

	public static String getChildTagValue(Element elem, String tagName)
			throws Exception {
		NodeList children = elem.getElementsByTagName(tagName);
		String result = null;

		// por exemplo a tag <Endereco>
		if (children == null) {
			return result;
		}

		// a tag <cidade>
		Element child = (Element) children.item(0);

		if (child == null) {
			return result;
		}
		// recuperamos o texto contido na tagName
		result = child.getTextContent();

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private InputStream OpenHttpConnection(String urlString) throws IOException {

		InputStream in = null;
		int response = -1;

		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(15000);

		if (!(conn instanceof HttpURLConnection))
			throw new IOException("Não conectado");

		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			response = httpConn.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
			}
		} catch (Exception ex) {
			throw new IOException("Erro de conexão");
		}

		return in;
	}

	private void calculeHeightListView() {

		int totalHeight = 0;

		ListAdapter adapter = listView.getAdapter();
		int lenght = adapter.getCount();

		for (int i = 0; i < lenght; i++) {
			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (adapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	@SuppressLint("HandlerLeak")
	Handler h1 = new Handler() {
		public void handleMessage(Message msg) {
			mesagens(msg);
		}
	};

	@Override
	public void onResume() {
		super.onResume();

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_BACK) {
					// handle back button's click listener

					new SweetAlertDialog(Login_ADM.this.getActivity(), SweetAlertDialog.SUCCESS_TYPE)
							.setTitleText("Saida").setContentText("Você realmente deseja sair ?")
							.setConfirmText("Sim").setCancelText("Não")
							.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {

							SharedPreferences settings = getActivity()
									.getSharedPreferences(
											PREF_NAME, 0);
							settings.edit().remove("logado")
									.commit();

							getActivity().finish();
							Intent intent = new Intent(
									getActivity(),
									MainActivity.class);
							startActivity(intent);


						}
					}).showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sDialog) {
									sDialog.cancel();
								}
							}).show();
					return true;
				}
				return false;
			}
		});
	}

	private void mesagens(Message msg) {

		if (msg.what == 1) {
			pgd.setProgress(pgd.getProgress() + 1);
		} else if (msg.what == 2) {
			createListView();
			pDialog.dismiss();
		} else if (msg.what == 3) {

			pDialog.dismiss();
		}
	}

}
