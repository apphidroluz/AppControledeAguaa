package br.com.hidroluz.HidroluzWaterControl;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
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

public class GraficoConsumo extends Fragment implements Runnable {

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
	private Integer[] valores;
	private Double[] medias;
	private Integer limiteY;
	private String lcMesInicio1;
	View rootView;
	private String mensagem;
	static ProgressDialog pgd;
	public SweetAlertDialog pDialog;


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// ConfiguracaoOrietancao.setarPaisagem(getActivity());
		rootView = inflater.inflate(R.layout.graficobarra, container, false);
		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		/*
		 * super.onCreate(savedInstanceState);
		 * setContentView(R.layout.graficobarra);
		 */
		// CARREGA OS CAMPOS PASSADOS DA ACTIVITY PRINCIPAL
		SharedPreferences settings = this.getActivity().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);

		codigo = settings.getString("CodigoLogin", "");
		unidade = settings.getString("UnidadeLogin", "");
		bloco = settings.getString("BlocoLogin", "");
		mesano = settings.getString("MesAno", "");
		// mesano = mesano.substring(0, 2) + mesano.substring(3, 7);
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

		pDialog =  new SweetAlertDialog(GraficoConsumo.this.getActivity(),
				SweetAlertDialog.PROGRESS_TYPE);;

		pDialog.setTitleText("Aguarde !!! Carregando Gráfico");
		pDialog.setContentText("Para uma melhor experiência visualize o gráfico com o celular na Horizontal");
		pDialog.setCancelable(false);
		pDialog.show();

		new Thread(GraficoConsumo.this).start();

	}

	@Override
	public void run() {
		try {

			buscar_dados();

		} catch (Exception ex) {
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new Tela_erro())
					.addToBackStack(null).commit();
		}

	}

	public void createChart() {

		CategorySeries series = new CategorySeries("Consumo Unidade");
		limiteY = 0;
		for (int i = 0; i < valores.length; i++) {
			series.add("Bar " + (i + 1), valores[i]);
			//series.add("Bar2 " + (i + 1), medias[i]);
			// armazena o valor maior para o limite do eixo Y
			if (limiteY < valores[i]) {
				limiteY = valores[i];
			}
		}
		
		CategorySeries series2 = new CategorySeries("Média Condominio");
		Double limiteYY = 0.00;
		for (int i = 0; i < medias.length; i++) {
			series2.add("Bar2 " + (i + 1), medias[i]);
			// armazena o valor maior para o limite do eixo Y
			if (limiteYY < medias[i]) {
				limiteYY = medias[i];
			}
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series.toXYSeries());
		dataset.addSeries(series2.toXYSeries());
		
		XYSeriesRenderer renderer = new XYSeriesRenderer();
	    renderer.setPointStyle(PointStyle.CIRCLE);
	    renderer.setFillPoints(true);
	    renderer.setColor(Color.GREEN);
	    //renderer.setDisplayChartValues(true);
		renderer.setLineWidth((float) 16.5d);
		renderer.setChartValuesSpacing((float) 10d);
		renderer.setChartValuesTextSize((float) 26d);
		renderer.setChartValuesTextAlign(Align.CENTER);
		renderer.setLineWidth(5f);
	 

	    XYSeriesRenderer renderer2= new XYSeriesRenderer();//renderer for 2
	    renderer2.setPointStyle(PointStyle.CIRCLE);
	    renderer2.setFillPoints(true);
	    renderer2.setColor(Color.WHITE);
	   // renderer2.setDisplayChartValues(true);
		renderer2.setLineWidth((float) 16.5d);
		renderer2.setChartValuesSpacing((float) 10d);
		renderer2.setChartValuesTextSize((float) 26d);
		renderer2.setChartValuesTextAlign(Align.CENTER);
		renderer2.setLineWidth(5f);

		// This is how the "Graph" itself will look like

	    XYMultipleSeriesRenderer seriesRenderer = new XYMultipleSeriesRenderer();
	    seriesRenderer.addSeriesRenderer(renderer);
	    seriesRenderer.addSeriesRenderer(renderer2); 
	    seriesRenderer.setOrientation(Orientation.HORIZONTAL);
	    seriesRenderer.setZoomButtonsVisible(false);
	    seriesRenderer.setApplyBackgroundColor(true);
	    seriesRenderer.setBackgroundColor(Color.BLACK);
	    seriesRenderer.setShowGrid(true);
	    seriesRenderer.setAxisTitleTextSize((float) 26d);
	    seriesRenderer.setLegendTextSize((float) 28d);
	    seriesRenderer.setLabelsTextSize((float) 15d);
	    seriesRenderer.setGridColor(Color.WHITE);
	    seriesRenderer.setXAxisMin(0);
	    seriesRenderer.setXAxisMax(10);
	    seriesRenderer.setYAxisMin(0);
		
	    seriesRenderer.setYAxisMax(limiteY + 5.00);
	    seriesRenderer.setXLabels(0);
	    
	  
		Integer c = 1;

		for (int i = 0; i < descmeses.length; i++) {
			Log.e("DATA", descmeses[i]);
			seriesRenderer.addXTextLabel(c++, descmeses[i].substring(8, 10) + "-"
					+ descmeses[i].substring(5, 7)+ "-" + descmeses[i].substring(2, 4));
		}

		
		LinearLayout layout = (LinearLayout) getView().findViewById(
				R.id.layout_chart);
		mChartView = ChartFactory.getLineChartView(getActivity(), dataset,
				seriesRenderer);
		layout.addView(mChartView);

	}

	private void buscar_dados() {
		// TODO Auto-generated method stub

		InputStream in = null;
		Conexao conexao = new Conexao();

		try {
			String url = "http://android.hidroluz.com.br/php/grafico_valores.php?codigo="
					+ codigo
					+ "&unidade=" + unidade 
					+ "&bloco=" + bloco;
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
					Integer valor[] = new Integer[numero_meses.getLength()];
					Double media[] = new Double[numero_meses.getLength()];

					for (int j = 0; j < numero_meses.getLength(); j++) {
						meses = (Element) numero_meses.item(j);

						String desc_mes = getChildTagValue(meses, "data_da_leitura");
						
						Integer valortotal = Integer.valueOf(getChildTagValue(
								meses, "consumototal"));
						
						Double media_condo = Double.valueOf(getChildTagValue(
								meses, "media_condo"));

						mes[j] = desc_mes;
						valor[j] = valortotal;
						media[j] = media_condo;

					}
					descmeses = mes;
					valores = valor;
					medias = media;
				}
			}
		} catch (Exception e1) {
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new Tela_erro())
					.addToBackStack(null).commit();
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
				getFragmentManager().beginTransaction()
						.replace(R.id.container, new Tela_erro())
						.addToBackStack(null).commit();
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
