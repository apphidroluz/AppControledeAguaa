package br.com.hidroluz.HidroluzWaterControl;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class GraficoMedio extends Fragment implements Runnable {

	private static final String PREF_NAME = "dadoslogin";
	private String codigo;
	private String unidade;
	private String bloco;
	private String mesano;
	private String lcMes;
	private String lcAno;
	private String datainicio;
	private String datafinal;
	private GraphicalView mChartView;
	private String[] descmeses;
	private Double[] valores;
	private Double limiteY;
	private String lcMesInicio1;
	View rootView;
	private String mensagem;
	static ProgressDialog pgd;
	private int count;

	SweetAlertDialog pDialog = new SweetAlertDialog(GraficoMedio.this.getActivity(),
			SweetAlertDialog.PROGRESS_TYPE);

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//ConfiguracaoOrietancao.setarPaisagem(getActivity());
		
		rootView = inflater.inflate(R.layout.graficobarra, container, false);
		
		if(savedInstanceState != null){
			count = savedInstanceState.getInt("count");
			
		}
		return rootView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("count", count);//Salvo o Estado
	}

	@Override
	public void onStart() {
		super.onStart();
		// setContentView(R.layout.graficobarra);
		// CARREGA OS CAMPOS PASSADOS DA ACTIVITY PRINCIPAL
		SharedPreferences settings = this.getActivity().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);

		codigo = settings.getString("CodigoLogin", "");
		unidade = settings.getString("UnidadeLogin", "");
		bloco = settings.getString("BlocoLogin", "");
		mesano = settings.getString("MesAno", "");
		//mesano = mesano.substring(0, 2) + mesano.substring(3, 7);
		lcMes = mesano.substring(0, 2);
		lcAno = mesano.substring(2, 6);

		Integer lcMesInicio = Integer.valueOf(mesano.substring(0, 2));
		Integer lcAnoInicio = Integer.valueOf(mesano.substring(2, 6));

		for (int contAno = 1; contAno < 12; contAno++) {
			lcMesInicio--;

			if (lcMesInicio == 0) {
				lcAnoInicio--;
				lcMesInicio = 12;
			}
		}

		if (lcMesInicio < 10) {
			lcMesInicio1 = "0" + lcMesInicio.toString();
		} else {
			lcMesInicio1 = lcMesInicio.toString();
		}

		datainicio = lcAnoInicio.toString() + lcMesInicio1 + "01";

		if (lcMes.equals("01") || lcMes.equals("03") || lcMes.equals("05")
				|| lcMes.equals("07") || lcMes.equals("08")
				|| lcMes.equals("10") || lcMes.equals("12")) {
			datafinal = lcAno + lcMes + "31";
		}

		if (lcMes.equals("04") || lcMes.equals("06") || lcMes.equals("09")
				|| lcMes.equals("11")) {
			datafinal = lcAno + lcMes + "30";
		}

		if (lcMes.equals("02")) {
			datafinal = lcAno + lcMes + "28";
		}

		// de chamar a url para pesquisa
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


		pDialog.setTitleText("Aguarde !!! Carregando Gráfico");
		pDialog.setContentText("Para uma melhor experiência visualize o gráfico com o celular na Horizontal");
		pDialog.setCancelable(false);
		pDialog.show();

		new Thread(GraficoMedio.this).start();

	}

	@Override
	public void run() {
		try {

		buscar_dados();

		} catch (Exception ex) {
			Log.d("Erro = ", "Erro = " + ex.getMessage());
		}

	}

	public void createChart() {

		CategorySeries series = new CategorySeries("");
		limiteY = 0.00;
		for (int i = 0; i < valores.length; i++) {
			series.add("Bar " + (i + 1), valores[i]);
			// armazena o valor maior para o limite do eixo Y
			if (limiteY < valores[i]) {
				limiteY = valores[i];
			}
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series.toXYSeries());

		// This is how the "Graph" itself will look like

		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();


		mRenderer.setBarSpacing(.6);
		mRenderer.setOrientation(Orientation.HORIZONTAL);
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setShowGrid(false);
		mRenderer.setGridColor(Color.WHITE);
		mRenderer.setXAxisMin(0);
		mRenderer.setXAxisMax(10);
		mRenderer.setYAxisMin(0);
		mRenderer.setYAxisMax(limiteY + 3);
		mRenderer.setXLabels(0);
		Integer c = 1;
		for (int i = 0; i < descmeses.length; i++) {
			mRenderer.addXTextLabel(c++,descmeses[i].substring(0, 4) + descmeses[i].substring(6, 8).toString());
		}

		mRenderer.setLabelsColor(Color.WHITE);
		mRenderer.setLabelsTextSize((float) 24d);
		// Customize bar 1
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setDisplayChartValues(true);
		renderer.setLineWidth((float) 16.5d);
		renderer.setChartValuesSpacing((float) 10d);
		renderer.setChartValuesTextSize((float) 26d);
		renderer.setChartValuesTextAlign(Align.CENTER);
		renderer.setColor(Color.WHITE);
		mRenderer.addSeriesRenderer(renderer);
		LinearLayout layout = (LinearLayout) getView().findViewById(
				R.id.layout_chart);
		mChartView = ChartFactory.getBarChartView(getActivity(), dataset,
				mRenderer, Type.DEFAULT);
		layout.addView(mChartView);

	}

	private void buscar_dados() {
		// TODO Auto-generated method stub

		InputStream in = null;
		Conexao conexao = new Conexao();

		try {
			String url = "http://android.hidroluz.com.br/php/grafico_valores.php?codigo="
					+ codigo
					+ "&mesano="
					+ mesano
					+ "&datainicio="
					+ datainicio
					+ "&datafinal="
					+ datafinal
					+ "&unidade="
					+ unidade + "&bloco=" + bloco;
			in = conexao.OpenHttpConnection(url);
			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				doc = db.parse(in);
			} catch (ParserConfigurationException e) {
				Toast.makeText(this.getActivity().getApplicationContext(),
						e.getMessage(), Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(this.getActivity().getApplicationContext(),
						e.getMessage(), Toast.LENGTH_LONG).show();
			}
			doc.getDocumentElement().normalize();

			NodeList itemNodes = doc.getElementsByTagName("resposta");

			for (int i = 0; i < itemNodes.getLength(); i++) {
				Node itemNode = itemNodes.item(i);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
					NodeList numero_meses = doc.getElementsByTagName("valores");
					Element meses = (Element) numero_meses.item(0);

					// inicializa matriz para guardar os meses
					String mes[] = new String[numero_meses.getLength()];
					Double valor[] = new Double[numero_meses.getLength()];

					for (int j = 0; j < numero_meses.getLength(); j++) {
						meses = (Element) numero_meses.item(j);

						String desc_mes = getChildTagValue(meses, "descmes");
						Double valortotal = Double.valueOf(getChildTagValue(
								meses, "consumomedio"));

						mes[j] = desc_mes;
						valor[j] = valortotal;

					}
					descmeses = mes;
					valores = valor;
				}
			}
		} catch (Exception e1) {
			Toast.makeText(this.getActivity().getApplicationContext(),
					"Erro de conexao", Toast.LENGTH_LONG).show();
		}
		
		h1.sendEmptyMessage(2);

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
	
	@SuppressLint("HandlerLeak")
	Handler h1 = new Handler() {
		public void handleMessage(Message msg) {
			try {
				mesagens(msg);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private void mesagens(Message msg) throws InterruptedException {

		if (msg.what == 1) {
			pgd.setProgress(pgd.getProgress() + 1);
		} else if (msg.what == 2) {
			Thread.currentThread().sleep(2000);
			createChart();
			pDialog.dismiss();
		} else if (msg.what == 3) {
			Toast.makeText(this.getActivity(),
					"Arquivo de Importação não encontrado na pasta",
					Toast.LENGTH_LONG).show();
			pDialog.dismiss();
		}
	}

}
