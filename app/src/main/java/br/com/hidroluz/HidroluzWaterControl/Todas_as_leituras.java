package br.com.hidroluz.HidroluzWaterControl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class Todas_as_leituras extends Fragment implements Runnable {

	private static final String PREF_NAME = "dadoslogin";
	private String lcCodigo = "";
	private String codigo;
	private String unidade;
	private String bloco;
	private String lcConsumoTotal = "";
	private String lcData = "";
	private String lcDias = "";
	private String lcData_anterior = "";
	private Template_Todas_as_leituras adapterListView;
	private String lcMesAno = "";
	private ListView listView;
	private ArrayList<ItemListTodas> itens;
	static ProgressDialog pgd;

	private SweetAlertDialog pDialog;

	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// ConfiguracaoOrietancao.setarRetrato(getActivity());
		rootView = inflater.inflate(R.layout.activity_todas_as_leituras,
				container, false);
		setRetainInstance(true);
		return rootView;
	}

	public void onStart() {
		super.onStart();
		Spinner spinner = (Spinner) getView().findViewById(R.id.spLeituras2);

		List<String> leituras = new ArrayList<String>();

		leituras.add("Todas");
		leituras.add("Mensal");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				this.getActivity(), R.layout.custom_spinner, leituras);

		spinner.setAdapter(dataAdapter);

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				switch (position) {
				case 0:

					break;
				case 1:

					getFragmentManager().beginTransaction()
							.replace(R.id.container, new ListadeLeituras())
							.addToBackStack(null).commit();

					break;

				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		SharedPreferences settings = this.getActivity().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);

		codigo = settings.getString("CodigoLogin", "");
		unidade = settings.getString("UnidadeLogin", "");
		bloco = settings.getString("BlocoLogin", "");

		unidade = unidade.replaceAll(" ", "%20");

		// Colocado para liberar politica de o funcionamento do WebServ
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

		}

		importar();

	}

	public void importar() {

		pDialog = new SweetAlertDialog(Todas_as_leituras.this.getActivity(),
				SweetAlertDialog.PROGRESS_TYPE);


		pDialog.setTitleText("Aguarde !!!");
		pDialog.setContentText("Carregando Leituras");
		pDialog.setCancelable(false);
		pDialog.show();

		new Thread(Todas_as_leituras.this).start();

	}

	@Override
	public void run() {
		try {

			buscar_dados();

		} catch (Exception ex) {

			getFragmentManager().beginTransaction()
					.replace(R.id.container, new Tela_erro())
					.addToBackStack(null).commit();

			Log.d("Erro = ", "Erro1 = " + ex.getMessage());
		}

	}

	private void createListView() {

		try {

			adapterListView = new Template_Todas_as_leituras(getActivity(),
					itens);

			listView = (ListView) getView().findViewById(R.id.listView1);
			// Define o Adapter
			listView.setAdapter(adapterListView);
			listView.setCacheColorHint(Color.TRANSPARENT);

			calculeHeightListView();

		} catch (Exception e) {
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new Tela_erro())
					.addToBackStack(null).commit();

			Log.d("Erro = ", "Erro2 = " + e.getMessage());
		}

	}

	private void buscar_dados() throws InterruptedException {

		InputStream in = null;

		Conexao c = new Conexao();

		try {

			in = c.OpenHttpConnection("http://android.hidroluz.com.br/php/lista_leitura_geral.php?codigo="
					+ codigo + "&unidade=" + unidade + "&bloco=" + bloco);

			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;

			try {

				db = dbf.newDocumentBuilder();
				doc = db.parse(in);

			} catch (Exception e) {

				getFragmentManager().beginTransaction()
						.replace(R.id.container, new Tela_erro())
						.addToBackStack(null).commit();
				e.printStackTrace();
				// TODO: handle exception
			}

			doc.getDocumentElement().normalize();
			NodeList itemNodes = doc.getElementsByTagName("resposta");

			for (int i = 0; i < itemNodes.getLength(); i++) {
				Node itemNode = itemNodes.item(i);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
					NodeList numero_meses = doc.getElementsByTagName("valores");
					Element meses = (Element) numero_meses.item(0);
					itens = new ArrayList<ItemListTodas>();

					for (int j = 0; j < numero_meses.getLength(); j++) {
						meses = (Element) numero_meses.item(j);

						lcConsumoTotal = getChildTagValue(meses, "consumo");
						lcData = getChildTagValue(meses, "data");
						lcDias = getChildTagValue(meses, "dias");
						lcData_anterior = getChildTagValue(meses, "anterior");
						lcMesAno = getChildTagValue(meses, "mesano");

						ItemListTodas item = new ItemListTodas(
								Integer.valueOf(getChildTagValue(meses,
										"consumo")), getChildTagValue(meses,
										"data"),
								getChildTagValue(meses, "dias"),
								getChildTagValue(meses, "anterior"),
								getChildTagValue(meses, "mesano"));

						itens.add(item);
					}
				}
			}

		} catch (Exception e1) {
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new Tela_erro())
					.addToBackStack(null).commit();

			Log.d("Erro = ", "Erro3 = " + e1.getMessage());
		}

		h1.sendEmptyMessage(2);

	}

	@SuppressLint("HandlerLeak")
	Handler h1 = new Handler() {
		public void handleMessage(Message msg) {
			mesagens(msg);
		}
	};

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
					new SweetAlertDialog(Todas_as_leituras.this.getActivity(), SweetAlertDialog.SUCCESS_TYPE)
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

}
