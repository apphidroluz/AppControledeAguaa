package br.com.hidroluz.HidroluzWaterControl;

import java.io.InputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;


public class Cadastro_unidades extends Activity {

	public String login_global;
	public String senha_global;
	private String[] blocos;
	private String[] unidades;
	private String bloco;
	private String unidade;
	private String email_usu;
	private String codigo_confi;
	private String id_usuario;
	public String codigo_condo;

	private static final String PREF_NAME = "dadoslogin";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cadastro_unidades);
		SharedPreferences settings = getSharedPreferences(PREF_NAME,
				MODE_PRIVATE);

		email_usu = settings.getString("EMAIL_USU", "");
        Log.e("test", email_usu);
        codigo_confi = settings.getString("CODIGO_CONFIR", "");

		buscar_idUsuario(email_usu);

		email_usu = settings.getString("EMAIL_USU", "");
		codigo_confi = settings.getString("CODIGO_CONFIR", "");
		codigo_condo = settings.getString("COD_CONDOMINIO", "");

		buscar_idUsuario(email_usu);

		if (!codigo_condo.isEmpty()){

			buscar_condo();

		}

		restoreActionBar();
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		getActionBar().setCustomView(R.layout.action_bar_layout);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
	    actionBar.setIcon(R.drawable.sf);
		//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

	}

	public void buscar(View v) {

		SharedPreferences settings = getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);

		InputStream in = null;
		EditText codigo = (EditText) findViewById(R.id.txt_codigo);
		TextView txt_nomfant_apel = (TextView) findViewById(R.id.nomefant_apel);
		TextView txt_endereco = (TextView) findViewById(R.id.endereco_cli);
		TextView txt_bairro = (TextView) findViewById(R.id.bairro_cli);
		TextView txt_cidade = (TextView) findViewById(R.id.cidade_cli);
		TextView txt_informe = (TextView) findViewById(R.id.txt_informe);
		Button btn_buscar = (Button) findViewById(R.id.buscar);
		TextView txt_bloco = (TextView) findViewById(R.id.textbloco);
		// EditText edt_unidade = (EditText) findViewById(R.id.txt_unidade_cad);
		TextView txt_unidade = (TextView) findViewById(R.id.textunidade);
		Button btn_solicitar = (Button) findViewById(R.id.solicitar);
		Spinner sp = (Spinner) findViewById(R.id.spBlocos);
		AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

		if (validaCampo(codigo)) {

			try {

				Conexao conexao = new Conexao();

				// in =
				// OpenHttpConnection("http://hidroluzmedicao.ddns.net:8080/php/listar.php?codigo=1266&anomes=201502");
				in = conexao
						.OpenHttpConnection("http://android.hidroluz.com.br/php/busca_clientes.php?codigo="
								+ codigo.getText().toString());
				Document doc = null;
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db;
				db = dbf.newDocumentBuilder();
				doc = db.parse(in);
				doc.getDocumentElement().normalize();

				NodeList itemNodes = doc.getElementsByTagName("resposta");
				String resposta = "";
				String cod_cli = "";
				String nomfant_apel = "";
				String endereco = "";
				String bairro = "";
				String cidade = "";
				String contato1 = "";
				String email = "";
				String todas = "";

				for (int i = 0; i < itemNodes.getLength(); i++) {
					Node itemNode = itemNodes.item(i);
					if (itemNode.getNodeType() == Node.ELEMENT_NODE) {

						Element definitonElement = (Element) itemNode;
						NodeList login = (definitonElement)
								.getElementsByTagName("codigo");

						for (int j = 0; j < login.getLength(); j++) {

							if (((Element) login.item(j)).getChildNodes()
									.item(0).getNodeValue().equals("false")) {

								new SweetAlertDialog(Cadastro_unidades.this, SweetAlertDialog.WARNING_TYPE).
										setTitleText("Atenção !!!").setContentText("Codigo de condominio inexistente").
										setConfirmText("Confirmar").show();

							} else {

								Element acesso = (Element) login.item(j);
								NodeList textNodes = ((Node) acesso)
										.getChildNodes();
								resposta = ((Node) textNodes.item(0))
										.getNodeValue();

								NodeList codNode = (definitonElement)
										.getElementsByTagName("codigo");
								Element codElment = (Element) codNode.item(0);
								NodeList codNodes = ((Node) codElment)
										.getChildNodes();
								cod_cli = ((Node) codNodes.item(0))
										.getNodeValue();

								NodeList apelNode = (definitonElement)
										.getElementsByTagName("nomfant_apel");
								Element apelElment = (Element) apelNode.item(0);
								NodeList apelNodes = ((Node) apelElment)
										.getChildNodes();
								nomfant_apel = ((Node) apelNodes.item(0))
										.getNodeValue();

								NodeList todasNode = (definitonElement)
										.getElementsByTagName("todas");
								Element todasElment = (Element) todasNode
										.item(0);
								NodeList todasNodes = ((Node) todasElment)
										.getChildNodes();
								todas = ((Node) todasNodes.item(0))
										.getNodeValue();

								NodeList endeNode = (definitonElement)
										.getElementsByTagName("endereco");
								Element endeElment = (Element) endeNode.item(0);
								NodeList endeNodes = ((Node) endeElment)
										.getChildNodes();
								endereco = ((Node) endeNodes.item(0))
										.getNodeValue();

								NodeList bairroNode = (definitonElement)
										.getElementsByTagName("bairro");
								Element bairroElment = (Element) bairroNode
										.item(0);
								NodeList bairroNodes = ((Node) bairroElment)
										.getChildNodes();
								bairro = ((Node) bairroNodes.item(0))
										.getNodeValue();

								NodeList cidadeNode = (definitonElement)
										.getElementsByTagName("cidade");
								Element cidadeElment = (Element) cidadeNode
										.item(0);
								NodeList cidadeNodes = ((Node) cidadeElment)
										.getChildNodes();
								cidade = ((Node) cidadeNodes.item(0))
										.getNodeValue();

								NodeList contatoNode = (definitonElement)
										.getElementsByTagName("contato1");
								Element contatoElment = (Element) contatoNode
										.item(0);
								NodeList contatoNodes = ((Node) contatoElment)
										.getChildNodes();
								contato1 = ((Node) contatoNodes.item(0))
										.getNodeValue();

								NodeList emailNode = (definitonElement)
										.getElementsByTagName("email");
								Element emailElment = (Element) emailNode
										.item(0);
								NodeList emailNodes = ((Node) emailElment)
										.getChildNodes();
								email = ((Node) emailNodes.item(0))
										.getNodeValue();

								SharedPreferences.Editor editor = settings
										.edit();

								editor.putString("CONTATO", contato1);
								editor.putString("EMAIL", email);
								editor.putString("CODIGO", cod_cli);
								editor.putString("APELIDO", nomfant_apel);
								editor.putString("TODAS", todas);

								editor.commit();

								txt_nomfant_apel.setText("NOME:    "
										+ nomfant_apel);
								txt_nomfant_apel.setVisibility(View.VISIBLE);

								txt_endereco.setText("ENDE:     " + endereco);
								txt_endereco.setVisibility(View.VISIBLE);

								txt_bairro.setText("BAIRRO: " + bairro);
								txt_bairro.setVisibility(View.VISIBLE);

								txt_cidade.setText("CIDADE: " + cidade);
								txt_cidade.setVisibility(View.VISIBLE);

								codigo.setVisibility(View.GONE);
								txt_informe.setVisibility(View.GONE);
								btn_buscar.setVisibility(View.GONE);

								txt_bloco.setVisibility(View.VISIBLE);
								txt_unidade.setVisibility(View.VISIBLE);
								// edt_bloco.setVisibility(View.VISIBLE);
								// edt_unidade.setVisibility(View.VISIBLE);

								btn_solicitar.setVisibility(View.VISIBLE);

								buscar_blocos(cod_cli);
								buscar_unidades(cod_cli);

								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
										this, R.layout.spinner_item, blocos);
								adapter.setDropDownViewResource(R.layout.spinner_item);
								sp.setAdapter(adapter);
								sp.setOnItemSelectedListener(new OnItemSelectedListener() {

									@Override
									public void onItemSelected(
											AdapterView<?> parent, View view,
											int position, long id) {
										bloco = parent.getItemAtPosition(
												position).toString();

									}

									@Override
									public void onNothingSelected(
											AdapterView<?> parent) {
										// TODO Auto-generated method stub

									}
								});
								sp.setVisibility(View.VISIBLE);

								final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
										this,
										android.R.layout.select_dialog_item,
										unidades);

								actv.setThreshold(1);// will start working

								// first character
								actv.setAdapter(adapter2);
								// adapter.setDropDownViewResource(R.layout.spinner_item);
								actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {

										unidade = adapter2.getItem(position)
												.toString().trim();

									}
								});
								actv.setVisibility(View.VISIBLE);

							}
						}
					}

				}

			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	public boolean validaCampo(EditText edt) {
		if (!StringUtils.isNullOrEmpty(edt.getText().toString())) {
			return true;
		}
		edt.setError("Digite o Codigo");
		return false;
	}

	public void buscar_condo() {

		SharedPreferences settings = getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);

		InputStream in = null;
		LinearLayout line = (LinearLayout) findViewById(R.id.linecad);
		EditText codigo = (EditText) findViewById(R.id.txt_codigo);
		TextView txt_nomfant_apel = (TextView) findViewById(R.id.nomefant_apel);
		TextView txt_endereco = (TextView) findViewById(R.id.endereco_cli);
		TextView txt_bairro = (TextView) findViewById(R.id.bairro_cli);
		TextView txt_cidade = (TextView) findViewById(R.id.cidade_cli);
		TextView txt_informe = (TextView) findViewById(R.id.txt_informe);
		Button btn_buscar = (Button) findViewById(R.id.buscar);
		TextView txt_bloco = (TextView) findViewById(R.id.textbloco);
// EditText edt_unidade = (EditText) findViewById(R.id.txt_unidade_cad);
		TextView txt_unidade = (TextView) findViewById(R.id.textunidade);
		Button btn_solicitar = (Button) findViewById(R.id.solicitar);
		Spinner sp = (Spinner) findViewById(R.id.spBlocos);
		AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);



		try {

			Conexao conexao = new Conexao();

// in =
			// OpenHttpConnection("http://hidroluzmedicao.ddns.net:8080/php/listar.php?codigo=1266&anomes=201502");
			in = conexao
					.OpenHttpConnection("http://android.hidroluz.com.br/php/busca_clientes.php?codigo="
							+ codigo_condo);
			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			doc = db.parse(in);
			doc.getDocumentElement().normalize();

			NodeList itemNodes = doc.getElementsByTagName("resposta");
			String resposta = "";
			String cod_cli = "";
			String nomfant_apel = "";
			String endereco = "";
			String bairro = "";
			String cidade = "";
			String contato1 = "";
			String email = "";
			String todas = "";

			for (int i = 0; i < itemNodes.getLength(); i++) {
				Node itemNode = itemNodes.item(i);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {

					Element definitonElement = (Element) itemNode;
					NodeList login = (definitonElement)
							.getElementsByTagName("codigo");

					for (int j = 0; j < login.getLength(); j++) {

						if (((Element) login.item(j)).getChildNodes()
								.item(0).getNodeValue().equals("false")) {

							new SweetAlertDialog(Cadastro_unidades.this, SweetAlertDialog.WARNING_TYPE).
									setTitleText("Atenção !!!").setContentText("Codigo de condominio inexistente").
									setConfirmText("Confirmar").show();

						} else {

							Element acesso = (Element) login.item(j);
							NodeList textNodes = ((Node) acesso)
									.getChildNodes();
							resposta = ((Node) textNodes.item(0))
									.getNodeValue();

							NodeList codNode = (definitonElement)
									.getElementsByTagName("codigo");
							Element codElment = (Element) codNode.item(0);
							NodeList codNodes = ((Node) codElment)
									.getChildNodes();
							cod_cli = ((Node) codNodes.item(0))
									.getNodeValue();

							NodeList apelNode = (definitonElement)
									.getElementsByTagName("nomfant_apel");
							Element apelElment = (Element) apelNode.item(0);
							NodeList apelNodes = ((Node) apelElment)
									.getChildNodes();
							nomfant_apel = ((Node) apelNodes.item(0))
									.getNodeValue();

							NodeList todasNode = (definitonElement)
									.getElementsByTagName("todas");
							Element todasElment = (Element) todasNode
									.item(0);
							NodeList todasNodes = ((Node) todasElment)
									.getChildNodes();
							todas = ((Node) todasNodes.item(0))
									.getNodeValue();

							NodeList endeNode = (definitonElement)
									.getElementsByTagName("endereco");
							Element endeElment = (Element) endeNode.item(0);
							NodeList endeNodes = ((Node) endeElment)
									.getChildNodes();
							endereco = ((Node) endeNodes.item(0))
									.getNodeValue();

							NodeList bairroNode = (definitonElement)
									.getElementsByTagName("bairro");
							Element bairroElment = (Element) bairroNode
									.item(0);
							NodeList bairroNodes = ((Node) bairroElment)
									.getChildNodes();
							bairro = ((Node) bairroNodes.item(0))
									.getNodeValue();

							NodeList cidadeNode = (definitonElement)
									.getElementsByTagName("cidade");
							Element cidadeElment = (Element) cidadeNode
									.item(0);
							NodeList cidadeNodes = ((Node) cidadeElment)
									.getChildNodes();
							cidade = ((Node) cidadeNodes.item(0))
									.getNodeValue();

							NodeList contatoNode = (definitonElement)
									.getElementsByTagName("contato1");
							Element contatoElment = (Element) contatoNode
									.item(0);
							NodeList contatoNodes = ((Node) contatoElment)
									.getChildNodes();
							contato1 = ((Node) contatoNodes.item(0))
									.getNodeValue();

							NodeList emailNode = (definitonElement)
									.getElementsByTagName("email");
							Element emailElment = (Element) emailNode
									.item(0);
							NodeList emailNodes = ((Node) emailElment)
									.getChildNodes();
							email = ((Node) emailNodes.item(0))
									.getNodeValue();

							SharedPreferences.Editor editor = settings
									.edit();

							editor.putString("CONTATO", contato1);
							editor.putString("EMAIL", email);
							editor.putString("CODIGO", cod_cli);
							editor.putString("APELIDO", nomfant_apel);
							editor.putString("TODAS", todas);

							editor.commit();

							txt_nomfant_apel.setText("NOME:    "
									+ nomfant_apel);
							txt_nomfant_apel.setVisibility(View.VISIBLE);

							txt_endereco.setText("ENDE:     " + endereco);
							txt_endereco.setVisibility(View.VISIBLE);

							txt_bairro.setText("BAIRRO: " + bairro);
							txt_bairro.setVisibility(View.VISIBLE);

							txt_cidade.setText("CIDADE: " + cidade);
							txt_cidade.setVisibility(View.VISIBLE);

							codigo.setVisibility(View.GONE);
							txt_informe.setVisibility(View.GONE);
							btn_buscar.setVisibility(View.GONE);

							txt_bloco.setVisibility(View.VISIBLE);
							txt_unidade.setVisibility(View.VISIBLE);

// edt_bloco.setVisibility(View.VISIBLE);
							// edt_unidade.setVisibility(View.VISIBLE);

							btn_solicitar.setVisibility(View.VISIBLE);

							buscar_blocos(cod_cli);
							buscar_unidades(cod_cli);

							ArrayAdapter<String> adapter = new ArrayAdapter<String>(
									this, R.layout.spinner_item, blocos);
							adapter.setDropDownViewResource(R.layout.spinner_item);
							sp.setAdapter(adapter);
							sp.setOnItemSelectedListener(new OnItemSelectedListener() {

								@Override
								public void onItemSelected(
										AdapterView<?> parent, View view,
										int position, long id) {
									bloco = parent.getItemAtPosition(
											position).toString();

								}

								@Override
								public void onNothingSelected(
										AdapterView<?> parent) {
// TODO Auto-generated method stub

								}
							});
							sp.setVisibility(View.VISIBLE);

							final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
									this,
									android.R.layout.select_dialog_item,
									unidades);

							actv.setThreshold(1);// will start working

							// first character
							actv.setAdapter(adapter2);
// adapter.setDropDownViewResource(R.layout.spinner_item);
							actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

								@Override
								public void onItemClick(
										AdapterView<?> parent, View view,
										int position, long id) {

									unidade = adapter2.getItem(position)
											.toString().trim();

								}
							});
							actv.setVisibility(View.VISIBLE);

						}
					}
				}

			}

		} catch (Exception e) {
// TODO: handle exception
		}


	}

	public void solicitar(View v) {

		InputStream in = null;

		SharedPreferences settings = getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);

		String codigo = settings.getString("CODIGO", "");

		try {

			Conexao conexao = new Conexao();

			// in =
			// OpenHttpConnection("http://hidroluzmedicao.ddns.net:8080/php/listar.php?codigo=1266&anomes=201502");
			in = conexao
					.OpenHttpConnection("http://android.hidroluz.com.br/php/busca_unidades.php?codigo="
							+ codigo
							+ "&bloco="
							+ bloco
							+ "&unidade="
							+ unidade + "&idUsuario=" + id_usuario);
			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			doc = db.parse(in);
			doc.getDocumentElement().normalize();

			NodeList itemNodes = doc.getElementsByTagName("resposta");

			String login = "";
			String senha = "";

			for (int i = 0; i < itemNodes.getLength(); i++) {
				Node itemNode = itemNodes.item(i);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {

					Element definitonElement = (Element) itemNode;
					NodeList login2 = (definitonElement)
							.getElementsByTagName("login");

					for (int j = 0; j < login2.getLength(); j++) {

						Builder builder;
						if (((Element) login2.item(j)).getChildNodes().item(0)
								.getNodeValue().equals("false")) {

							new SweetAlertDialog(Cadastro_unidades.this, SweetAlertDialog.WARNING_TYPE).
									setTitleText("Atenção !!!").setContentText("Não foi encontrada essa unidade para o Condominio").
									setConfirmText("Confirmar").show();

						} else {

							if (((Element) definitonElement
									.getElementsByTagName("acesso_cadastrado")
									.item(0)).getChildNodes().item(0)
									.getNodeValue().equalsIgnoreCase("sim")) {

								new SweetAlertDialog(Cadastro_unidades.this, SweetAlertDialog.WARNING_TYPE).
										setTitleText("Atenção !!!").setContentText("Essa unidade já teve seu cadastro efetuado").
										setConfirmText("Confirmar").show();
							} else {

								SharedPreferences.Editor editor = settings
										.edit();

								editor.putString("logado", "1");
								editor.putString("CodigoLogin", codigo);
								editor.putString("UnidadeLogin", unidade);
								editor.putString("BlocoLogin", bloco);

								editor.commit();

								new SweetAlertDialog(Cadastro_unidades.this, SweetAlertDialog.SUCCESS_TYPE).
										setTitleText("Cadastrado Realizado com Sucesso!!!").setContentText("Parabéns você completou o cadastro no aplicativo Hidroluz.").
										setConfirmText("OK").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sweetAlertDialog) {
										Intent it = new Intent(
												Cadastro_unidades.this,
												DrawerActivity.class);
										Cadastro_unidades.this
												.finish();
									}
								}).show();
							}

						}
					}

				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void buscar_blocos(String lcCodigo) {

		InputStream in = null;
		String resposta = "";

		try {

			Conexao conexao = new Conexao();

			// in =
			// OpenHttpConnection("http://hidroluzmedicao.ddns.net:8080/php/listar.php?codigo=1266&anomes=201502");
			String url = "http://android.hidroluz.com.br/php/retorna_blocos.php?codigo="
					+ lcCodigo;
			in = conexao.OpenHttpConnection(url);
			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				doc = db.parse(in);
			} catch (ParserConfigurationException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
			doc.getDocumentElement().normalize();

			NodeList itemNodes = doc.getElementsByTagName("resposta");

			for (int i = 0; i < itemNodes.getLength(); i++) {
				Node itemNode = itemNodes.item(i);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
					Element definitonElement = (Element) itemNode;
					NodeList numero_meses = (definitonElement)
							.getElementsByTagName("bloco");

					// inicializa matriz para guardar os meses
					String bloco[] = new String[numero_meses.getLength()];

					for (int j = 0; j < numero_meses.getLength(); j++) {

						Element acesso = (Element) numero_meses.item(j);
						NodeList textNodes = ((Node) acesso).getChildNodes();
						resposta = ((Node) textNodes.item(0)).getNodeValue();

						bloco[j] = resposta;

					}
					blocos = bloco;
				}
			}
		} catch (Exception e1) {
			Toast.makeText(getApplicationContext(), "Erro de conexão",
					Toast.LENGTH_LONG).show();
		}

	}

	private void buscar_idUsuario(String email) {

		InputStream in = null;
		String resposta = "";

		try {

			Conexao conexao = new Conexao();

			// in =
			// OpenHttpConnection("http://hidroluzmedicao.ddns.net:8080/php/listar.php?codigo=1266&anomes=201502");
			String url = "http://android.hidroluz.com.br/php/retorna_idUsuario.php?email="
					+ email;
			in = conexao.OpenHttpConnection(url);
			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				doc = db.parse(in);
			} catch (ParserConfigurationException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
			doc.getDocumentElement().normalize();

			NodeList itemNodes = doc.getElementsByTagName("resposta");

			for (int i = 0; i < itemNodes.getLength(); i++) {
				Node itemNode = itemNodes.item(i);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
					Element definitonElement = (Element) itemNode;
					NodeList numero_meses = (definitonElement)
							.getElementsByTagName("id_usuario");

					// inicializa matriz para guardar os meses
					// String bloco[] = new String[numero_meses.getLength()];

					for (int j = 0; j < numero_meses.getLength(); j++) {

						Element acesso = (Element) numero_meses.item(j);
						NodeList textNodes = ((Node) acesso).getChildNodes();
						resposta = ((Node) textNodes.item(0)).getNodeValue();

						id_usuario = resposta;

					}

				}
			}
		} catch (Exception e1) {
			Toast.makeText(getApplicationContext(), "Erro de conexão",
					Toast.LENGTH_LONG).show();
		}

	}

	private void buscar_unidades(String lcCodigo) {

		InputStream in = null;
		String resposta = "";

		try {

			Conexao conexao = new Conexao();

			// in =
			// OpenHttpConnection("http://hidroluzmedicao.ddns.net:8080/php/listar.php?codigo=1266&anomes=201502");
			String url = "http://android.hidroluz.com.br/php/retorna_unidades.php?codigo="
					+ lcCodigo;
			in = conexao.OpenHttpConnection(url);
			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				doc = db.parse(in);
			} catch (ParserConfigurationException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
			doc.getDocumentElement().normalize();

			NodeList itemNodes = doc.getElementsByTagName("resposta");

			for (int i = 0; i < itemNodes.getLength(); i++) {
				Node itemNode = itemNodes.item(i);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
					Element definitonElement = (Element) itemNode;
					NodeList numero_meses = (definitonElement)
							.getElementsByTagName("unidade");

					// inicializa matriz para guardar os meses
					String unidade[] = new String[numero_meses.getLength()];

					for (int j = 0; j < numero_meses.getLength(); j++) {

						Element acesso = (Element) numero_meses.item(j);
						NodeList textNodes = ((Node) acesso).getChildNodes();
						resposta = ((Node) textNodes.item(0)).getNodeValue();

						unidade[j] = resposta;

					}
					unidades = unidade;
				}
			}
		} catch (Exception e1) {
			Toast.makeText(getApplicationContext(), "Erro de conexão",
					Toast.LENGTH_LONG).show();
		}

	}

	class EnviarEmail extends AsyncTask<Void, Void, String> {

		private Context context;
		private Session session;

		// Information to send email
		private String email;
		private String subject;
		private String message;
		private String email_cc;

		SweetAlertDialog pDialog = new SweetAlertDialog(Cadastro_unidades.this,
				SweetAlertDialog.PROGRESS_TYPE);

		// Progressdialog to show while sending email
		private ProgressDialog progressDialog;

		public EnviarEmail(Context context, String subject, String message,
				String email) {
			this.context = context;
			this.subject = subject;
			this.message = message;
			this.email = email;
		}

		// Class Constructor
		public EnviarEmail(Context context, String email, String subject,
				String message, String email_cc) {
			// Initializing variables
			this.context = context;
			this.email = email;
			this.subject = subject;
			this.message = message;
			this.email_cc = email_cc;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();


			pDialog.setTitleText("Aguarde !!!");
			pDialog.setContentText("Enviando Mensagem Aguarde");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);

			pDialog.dismiss();

			new SweetAlertDialog(Cadastro_unidades.this, SweetAlertDialog.SUCCESS_TYPE).
					setTitleText("Parabéns !!!").setContentText("Foi enviado para o seu email, os dados de acesso da sua unidade, lembramos que a solicitação de cadastro é informada ao condominio.").
					setConfirmText("OK").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
				@Override
				public void onClick(SweetAlertDialog sweetAlertDialog) {
					Intent it = new Intent(
							Cadastro_unidades.this,
							MainActivity.class);

					Cadastro_unidades.this.finish();
					Cadastro_unidades.this.startActivity(it);

				}
			}).show();

		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub

			Properties props = new Properties();

			// Configuring properties for gmail
			// If you are not using gmail you may need to change the values
			props.put("mail.smtp.host", "smtp.hidroluz.com.br");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "587");

			session = Session.getDefaultInstance(props,
					new javax.mail.Authenticator() {
						// Authenticating the password
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(Config.EMAIL,
									Config.PASSWORD);
						}
					});

			try {
				// Creating MimeMessage object
				MimeMessage mm = new MimeMessage(session);

				// Setting sender address
				mm.setFrom(new InternetAddress(Config.EMAIL));
				// Adding receiver
				mm.addRecipient(Message.RecipientType.TO, new InternetAddress(
						this.email));

				// Adding subject
				mm.setSubject(subject);
				// Adding message
				mm.setText(message);

				// Sending email
				Transport.send(mm);

			} catch (MessagingException e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	class EnviarEmailADM extends AsyncTask<Void, Void, String> {
		private Context context;
		private String email;
		private String email_cc;
		private String message;
		private ProgressDialog progressDialog;
		private Session session;
		private String subject;

		SweetAlertDialog pDialog = new SweetAlertDialog(Cadastro_unidades.this,
				SweetAlertDialog.PROGRESS_TYPE);

		public EnviarEmailADM(Context context, String subject, String message,
				String email_cc) {
			this.context = context;
			this.subject = subject;
			this.message = message;
			this.email_cc = email_cc;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setTitleText("Aguarde");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected void onPostExecute(String s) {
			super.onPostExecute(s);


			new EnviarEmail(
					Cadastro_unidades.this,
					"Cadastro no Aplicativo Hidroluz",
					"Parabens voce acaba de se cadastrar no aplicativo da HIDROLUZ \nSeu usuario é: "
							+ Cadastro_unidades.this.login_global
							+ "\n"
							+ "Sua senha é: "
							+ Cadastro_unidades.this.senha_global
							+ "\n"
							+ "Acesse o aplicativo e complete seu cadastro.",
					email).execute();

			pDialog.dismiss();
		}

		protected String doInBackground(Void... params) {
			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.hidroluz.com.br");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "587");

			this.session = Session.getDefaultInstance(props,
					new javax.mail.Authenticator() {
						// Authenticating the password
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(Config.EMAIL,
									Config.PASSWORD);
						}
					});
			try {
				MimeMessage mm = new MimeMessage(this.session);
				mm.setFrom(new InternetAddress(Config.EMAIL));
				mm.addRecipient(Message.RecipientType.TO, new InternetAddress(
						this.email_cc));
				mm.setSubject(this.subject);
				mm.setText(this.message);
				Transport.send(mm);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}
