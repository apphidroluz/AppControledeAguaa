package br.com.hidroluz.HidroluzWaterControl;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;


public class DadosLeitura extends Fragment {

	private static final String PREF_NAME = "dadoslogin";
	private String codigo;
	private String unidade;
	private String bloco;
	private String data_da_leitura;
	private String dias;
	private String media_diario;
	private String data_anterior;
	private String fluxo_continuo;
	private TextView txtConsumo;
	private TextView edtValUnidade;
	private TextView edtValRateio;
	private TextView edtValTotal;
	private TextView txtPeriodo;
	private TextView txtData_anterior;
	private TextView txtDias;
	private TextView txtMedia;
	private String lcConsumoTotal = "";
	private String lcValorUnidade = "";
	private String lcValorTotal = "";
	private String lcValorRateio = "";
	private ListView listView;
	private TamplateListaCsonsumo adapterListView;
	private ArrayList<ItemListConsumo> itens;
	private Spinner sp;
	private String lcCodigo = "";
	private String[] meses;
	private String resposta = "";
	static ProgressDialog pgd;
	View rootView;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// ConfiguracaoOrietancao.setarRetrato(getActivity());
		rootView = inflater.inflate(R.layout.dados_leitura_completo, container,
				false);
		return rootView;
	}

	public void onStart() {
		super.onStart();
		Init();

	}

	public void setarpref(String MES) {
		SharedPreferences settings = this.getActivity().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("mesano", MES);

		editor.commit();

	}

	protected void Init() {
		// setContentView(R.layout.dadosleitura);

		Funcoes funcao = new Funcoes();
		// setContentView(R.layout.dadosleitura);
		// CARREGA OS CAMPOS PASSADOS DA ACTIVITY PRINCIPAL

		SharedPreferences settings = this.getActivity().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);

		codigo = settings.getString("CodigoLogin", "");
		unidade = settings.getString("UnidadeLogin", "");
		bloco = settings.getString("BlocoLogin", "");
		String d = settings.getString("Data_da_leitura", "");
		dias = settings.getString("DIAS", "");
		media_diario = settings.getString("MEDIA", "");
		data_anterior = settings.getString("ANTERIOR", "");
		fluxo_continuo = settings.getString("Fluxo", "");

		if (fluxo_continuo.equalsIgnoreCase("sim")) {

			new SweetAlertDialog(DadosLeitura.this.getActivity(), SweetAlertDialog.WARNING_TYPE)
					.setTitleText("Atenção !!!")
					.setContentText("Nesta Leitura Houve um Fluxo Contínuo de Água")
					.setConfirmText("Confirmar").show();

		}

		// mesano = mesano.substring(0, 2) + mesano.substring(3, 7);

		// mesano = "012016";

		data_da_leitura = "20" + d.substring(6, 8) + "-" + d.substring(3, 5)
				+ "-" + d.substring(0, 2);

		txtPeriodo = (TextView) getView().findViewById(R.id.DataEscolhia);
		txtData_anterior = (TextView) getView().findViewById(R.id.DataAnterior);
		txtDias = (TextView) getView().findViewById(R.id.Dias);
		txtMedia = (TextView) getView().findViewById(R.id.Media);
		edtValUnidade = (TextView) getView().findViewById(
				R.id.txt_valor_unidade);
		edtValRateio = (TextView) getView().findViewById(R.id.txt_valor_rateio);
		edtValTotal = (TextView) getView().findViewById(R.id.txt_valor_total);
		txtConsumo = (TextView) getView().findViewById(R.id.consumoTotal);

		// de chamar a url para pesquisa
		unidade = unidade.replaceAll(" ", "%20");

		// Colocado para liberar politica de o funcionamento do WebServ
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

		}

		buscar_dados();

		String data = data_da_leitura.substring(8, 10) + "/"
				+ data_da_leitura.substring(5, 7) + "/"
				+ data_da_leitura.substring(2, 4);
		String data_a = data_anterior.substring(8, 10) + "/"
				+ data_anterior.substring(5, 7) + "/"
				+ data_anterior.substring(2, 4);

		txtPeriodo.setText(data);
		txtData_anterior.setText(data_a);
		txtDias.setText(dias);
		txtMedia.setText(media_diario);
		txtConsumo.setText(lcConsumoTotal);
		edtValUnidade.setText("R$ " + lcValorUnidade);
		edtValTotal.setText("R$ " + lcValorTotal);
		if (!lcValorRateio.equals("0.00000")) {
			String val_rateio = lcValorRateio.substring(0, 4);
			edtValRateio.setText("R$  " + val_rateio);
		} else {

			edtValRateio.setText("R$ 0.00");
		}

	}

	private void createListView() {

		// Criamos nossa lista que preenchera o ListView
		// Cria o adapter
		adapterListView = new TamplateListaCsonsumo(getActivity(), itens);

		listView = (ListView) getView().findViewById(R.id.lstConsumo);
		// Define o Adapter
		listView.setAdapter(adapterListView);

		listView.setCacheColorHint(Color.TRANSPARENT);
		calculeHeightListView();
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

	private void buscar_dados() {
		// TODO Auto-generated method stub

		InputStream in = null;
		Conexao conexao = new Conexao();

		try {
			String url = "http://android.hidroluz.com.br/php/dados_leitura.php?codigo="
					+ codigo
					+ "&data_da_leitura="
					+ data_da_leitura
					+ "&unidade=" + unidade + "&bloco=" + bloco;
			in = conexao.OpenHttpConnection(url);
			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				doc = db.parse(in);
			} catch (ParserConfigurationException e) {
				// Toast.makeText(getApplicationContext(), e.getMessage(),
				// Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (Exception e) {
				// Toast.makeText(getApplicationContext(), e.getMessage(),
				// Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			doc.getDocumentElement().normalize();

			NodeList itemNodes = doc.getElementsByTagName("resposta");

			for (int i = 0; i < itemNodes.getLength(); i++) {
				Node itemNode = itemNodes.item(i);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
					NodeList numero_meses = doc.getElementsByTagName("valores");
					Element meses = (Element) numero_meses.item(0);
					itens = new ArrayList<ItemListConsumo>();

					for (int j = 0; j < numero_meses.getLength(); j++) {
						meses = (Element) numero_meses.item(j);

						lcConsumoTotal = getChildTagValue(meses, "consumototal");
						lcValorUnidade = getChildTagValue(meses, "valorunidade");
						lcValorRateio = getChildTagValue(meses, "valorrateio");
						lcValorTotal = getChildTagValue(meses, "valortotal");

						ItemListConsumo item = new ItemListConsumo(
								getChildTagValue(meses, "hidrometro"),
								Integer.valueOf(getChildTagValue(meses,
										"indiceatual")),
								Integer.valueOf(getChildTagValue(meses,
										"indiceantigo")),
								Integer.valueOf(getChildTagValue(meses,
										"consumo")));

						itens.add(item);

					}
				}
			}
		} catch (Exception e1) {
			// .LENGTH_LONG).show();
			e1.printStackTrace();
		}
		createListView();
	}

	private void buscar_meses(String lcCodigo) {

		InputStream in = null;
		Conexao conexao = new Conexao();

		try {

			// in =
			// OpenHttpConnection("http://hidroluzmedicao.ddns.net:8080/php/listar.php?codigo=1266&anomes=201502");
			String url = "http://hidroluzmedicao.ddns.net:8080/php/retorna_meses.php?codigo="
					+ lcCodigo;
			in = conexao.OpenHttpConnection(url);
			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				doc = db.parse(in);
			} catch (ParserConfigurationException e) {
				// Toast.makeText(getApplicationContext(), e.getMessage(),
				// Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				// Toast.makeText(getApplicationContext(), e.getMessage(),
				// Toast.LENGTH_LONG).show();
			}
			doc.getDocumentElement().normalize();

			NodeList itemNodes = doc.getElementsByTagName("resposta");

			for (int i = 0; i < itemNodes.getLength(); i++) {
				Node itemNode = itemNodes.item(i);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
					Element definitonElement = (Element) itemNode;
					NodeList numero_meses = (definitonElement)
							.getElementsByTagName("mes");

					// inicializa matriz para guardar os meses
					String mes[] = new String[numero_meses.getLength()];

					for (int j = 0; j < numero_meses.getLength(); j++) {

						Element acesso = (Element) numero_meses.item(j);
						NodeList textNodes = ((Node) acesso).getChildNodes();
						resposta = ((Node) textNodes.item(0)).getNodeValue();

						mes[j] = resposta;

					}
					meses = mes;

				}
			}
		} catch (Exception e1) {
			// Toast.LENGTH_LONG).show();

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

}
