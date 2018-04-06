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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class Editar_Dados_Adm extends Fragment implements Runnable {

	private static final String PREF_NAME = "dadoslogin";
	private String usuario;
	private String passaword;
	private String[] valores;
	private String razsoc_nome;
	private String bloco;
	private String unidade;
	private String login;
	private String senha;
	private String email;
	private String id_unidade;
	private ListView listView;
	private TemplateListaUnidades adapterListView;
	private ArrayList<ItemListUnidades> itens;
	private TextView razsoc;
	static ProgressDialog pgd;
	private SweetAlertDialog pDialog;



	View rootview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// ConfiguracaoOrietancao.setarRetrato(getActivity());
		
		rootview = inflater.inflate(R.layout.activity_editar__dados__adm,
				container, false);
		setRetainInstance(true);
		return rootview;
	}

	public void onStart() {
		super.onStart();
		SharedPreferences settings = getActivity().getSharedPreferences(
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

		pDialog = new SweetAlertDialog(Editar_Dados_Adm.this.getActivity(),
				SweetAlertDialog.PROGRESS_TYPE);

        pDialog.setTitleText("Aguarde !!!");
        pDialog.setContentText("Carregando Unidades");
        pDialog.setCancelable(false);
        pDialog.show();

		new Thread(Editar_Dados_Adm.this).start();

	}

	private void listar_unidades() throws InterruptedException {
	
		InputStream in = null;
		Conexao c = new Conexao();

		try {
			in = OpenHttpConnection("http://android.hidroluz.com.br/php/login_adm_novo.php?usuario="
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
						login = getChildTagValue(meses, "login");
						email = getChildTagValue(meses, "email");
						id_unidade = getChildTagValue(meses, "id_unidade");

						ItemListUnidades item = new ItemListUnidades(
								getChildTagValue(meses, "bloco"),
								getChildTagValue(meses, "id_unidade"),
								getChildTagValue(meses, "login"),
								getChildTagValue(meses, "razsoc_nome"),
								getChildTagValue(meses, "unidade"),
								getChildTagValue(meses, "email"));

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

	public void limpar() throws InterruptedException {
		InputStream in = null;
		try {

			Conexao conexao = new Conexao();

			// in =
			// OpenHttpConnection("http://hidroluzmedicao.ddns.net:8080/php/listar.php?codigo=1266&anomes=201502");
			in = conexao
					.OpenHttpConnection("http://android.hidroluz.com.br/php/limpa_dados.php?id_unidade="
							+ id_unidade);
			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				doc = db.parse(in);

				doc.getDocumentElement().normalize();

				NodeList itemNodes = doc.getElementsByTagName("resposta");
				String resposta = "";

				for (int i = 0; i < itemNodes.getLength(); i++) {
					Node itemNode = itemNodes.item(i);
					if (itemNode.getNodeType() == Node.ELEMENT_NODE) {

						Element definitonElement = (Element) itemNode;
						NodeList login = (definitonElement)
								.getElementsByTagName("codigo");

						for (int j = 0; j < login.getLength(); j++) {

							Element acesso = (Element) login.item(j);
							NodeList textNodes = ((Node) acesso)
									.getChildNodes();
							resposta = ((Node) textNodes.item(0))
									.getNodeValue();

							if (resposta.equalsIgnoreCase("OK")) {

                                new SweetAlertDialog(Editar_Dados_Adm.this.getActivity(), SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Atenção !!!")
                                        .setContentText("Unidade Atualizada com Sucesso")
                                        .setConfirmText("Confirmar").show();

								getFragmentManager()
										.beginTransaction()
										.replace(R.id.container,
												new Editar_Dados_Adm())
										.addToBackStack(null).commit();

							}
						}

					}
				}

			} catch (Exception e1) {
			}

		} catch (Exception e1) {

		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void createListView() {
		// Criamos nossa lista que preenchera o ListView
		// Cria o adapter
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

					id_unidade = (String) ((TextView) view
							.findViewById(R.id.id_unidade)).getText()
							.toString();

                    new SweetAlertDialog(Editar_Dados_Adm.this.getActivity(), SweetAlertDialog.SUCCESS_TYPE).
                            setTitleText("Limpar").setContentText("Você deseja limpar os dados dessa unidade ?")
                            .setConfirmText("Sim").setCancelText("Não")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            try {
                                limpar();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch
                                // block
                                e.printStackTrace();
                            }

                        }
                    }).showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            }).show();
				}
			});

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
