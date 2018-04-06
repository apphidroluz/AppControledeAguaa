package br.com.hidroluz.HidroluzWaterControl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class ListadeLeituras extends Fragment implements Runnable {

	private static final String PREF_NAME = "dadoslogin";
	private String codigo;
	private String unidade;
	private String bloco;
	private String todas;
	private String data_da_leitura;
	private String[] meses;
	private TextView txtConsumo;
	private EditText edtValUnidade;
	private EditText edtValRateio;
	private EditText edtData;
	private String lcConsumoTotal = "";
	private String lcData = "";
	private String lcValorTotal = "";
	private String lcValorRateio = "";
	private String lcDias = "";
	private String lcData_anterior = "";
	private String lcMesAno = "";
	private String lcFluxo_continuo = "";
	private ListView listView;
	private TemplateListaLeituras adapterListView;
	private ArrayList<ItemListLeituras> itens;
	private String lcCodigo = "";
	private String[] valores;
	private String resposta = "";
	static ProgressDialog pgd;
    public SweetAlertDialog pDialog;



	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// ConfiguracaoOrietancao.setarRetrato(getActivity());
		rootView = inflater.inflate(R.layout.activity_listade_leituras,
				container, false);
		setRetainInstance(true);
		return rootView;
	}

	public void onStart() {
		super.onStart();

		SharedPreferences settings = this.getActivity().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);

		lcCodigo = settings.getString("CodigoLogin", "");

		todas = settings.getString("TODAS", "");

		if (todas.toString().equalsIgnoreCase("SIM")) {

			Spinner spinner = (Spinner) getView().findViewById(R.id.spLeituras);

			List<String> leituras = new ArrayList<String>();

			leituras.add("Mensal");
			leituras.add("Todas");

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

						getFragmentManager()
								.beginTransaction()
								.replace(R.id.container,
										new Todas_as_leituras())
								.addToBackStack(null).commit();
						break;

					}

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub

				}
			});

		} else {

			Spinner spinner = (Spinner) getView().findViewById(R.id.spLeituras);
			spinner.setVisibility(View.GONE);

			TextView txt = (TextView) getView().findViewById(R.id.lblLeitura);

			txt.setVisibility(View.GONE);

		}

		codigo = settings.getString("CodigoLogin", "").trim();
		unidade = settings.getString("UnidadeLogin", "").trim();
		bloco = settings.getString("BlocoLogin", "").trim();

		// Colocado para liberar politica de o funcionamento do WebServ
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

		}

		importar();

	}

	@Override
	public void run() {
		try {

			buscar_dados();

		} catch (Exception ex) {

			getFragmentManager().beginTransaction()
					.replace(R.id.container, new Tela_erro())
					.addToBackStack(null).commit();

			Log.d("Erro = ", "Erro = " + ex.getMessage());
		}

	}

	public void importar() {

    pDialog = new SweetAlertDialog(ListadeLeituras.this.getActivity(),
                SweetAlertDialog.PROGRESS_TYPE);


		pDialog.setTitleText("Aguarde !!!");
		pDialog.setContentText("Carregando Leituras");
		pDialog.setCancelable(false);
		pDialog.show();

		new Thread(ListadeLeituras.this).start();

	}

	private void createListView() {
		// Criamos nossa lista que preenchera o ListView
		// Cria o adapter
		try {

			adapterListView = new TemplateListaLeituras(getActivity(), itens);

			listView = (ListView) getView().findViewById(R.id.listView1);
			// Define o Adapter
			listView.setAdapter(adapterListView);

			listView.setCacheColorHint(Color.TRANSPARENT);

			setarmes(itens.get(0).getMesano());

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					String dias = (String) ((TextView) view
							.findViewById(R.id.Dias)).getText().toString();
					String media = (String) ((TextView) view
							.findViewById(R.id.Media)).getText().toString();
					String dt_ant = (String) ((TextView) view
							.findViewById(R.id.Dt_anterior)).getText()
							.toString();
					String mes = (String) ((TextView) view
							.findViewById(R.id.mesano2)).getText().toString();
					String dt = (String) ((TextView) view
							.findViewById(R.id.Dt_es)).getText().toString();

					String fl = (String) ((TextView) view
							.findViewById(R.id.fluxo_continuo)).getText()
							.toString();

					setarpref(dt, dias, media, dt_ant, mes, fl);

					getFragmentManager().beginTransaction()
							.replace(R.id.container, new DadosLeitura())
							.addToBackStack(null).commit();

				}

			});

			calculeHeightListView();

		} catch (Exception e) {

			getFragmentManager().beginTransaction()
					.replace(R.id.container, new Tela_erro())
					.addToBackStack(null).commit();
		}

	}

	public void setarmes(String MES) {
		SharedPreferences settings = this.getActivity().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();

		editor.putString("MesAno", MES);

		editor.commit();

	}

	public void setarpref(String DATA, String DIAS, String MEDIA,
			String ANTERIOR, String MES, String FLUXO) {
		SharedPreferences settings = this.getActivity().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();

		editor.putString("DIAS", DIAS);
		editor.putString("MEDIA", MEDIA);
		editor.putString("Data_da_leitura", DATA);
		editor.putString("ANTERIOR", ANTERIOR);
		editor.putString("MesAno", MES);
		editor.putString("Fluxo", FLUXO);

		editor.commit();

	}

	private void buscar_dados() throws InterruptedException {
		// TODO Auto-generated method stub

		InputStream in = null;

		try {
			String url = "http://android.hidroluz.com.br/php/lista_leitura.php?codigo="
					+ codigo + "&unidade=" + unidade + "&bloco=" + bloco;
			in = OpenHttpConnection(url);
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
					itens = new ArrayList<ItemListLeituras>();

					for (int j = 0; j < numero_meses.getLength(); j++) {
						meses = (Element) numero_meses.item(j);

						lcConsumoTotal = getChildTagValue(meses, "consumo");
						lcData = getChildTagValue(meses, "data");
						lcValorTotal = getChildTagValue(meses, "valortotal");
						lcValorRateio = getChildTagValue(meses, "valorrateio");
						lcDias = getChildTagValue(meses, "dias");
						lcData_anterior = getChildTagValue(meses, "anterior");
						lcMesAno = getChildTagValue(meses, "mesano");
						lcFluxo_continuo = getChildTagValue(meses,
								"fluxo_continuo");

						ItemListLeituras item = new ItemListLeituras(
								Integer.valueOf(getChildTagValue(meses,
										"consumo")), getChildTagValue(meses,
										"data"),
								Double.valueOf(getChildTagValue(meses,
										"valortotal")),
								Double.valueOf(getChildTagValue(meses,
										"valorrateio")), getChildTagValue(
										meses, "dias"), getChildTagValue(meses,
										"anterior"), getChildTagValue(meses,
										"mesano"), getChildTagValue(meses,
										"fluxo_continuo"));

						itens.add(item);

					}

				}
			}
		} catch (Exception e1) {
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new Tela_erro())
					.addToBackStack(null).commit();
		}

		h1.sendEmptyMessage(2);

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
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new Tela_erro())
					.addToBackStack(null).commit();
		}

		return in;
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

					new SweetAlertDialog(ListadeLeituras.this.getActivity(), SweetAlertDialog.SUCCESS_TYPE)
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
