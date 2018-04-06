package br.com.hidroluz.HidroluzWaterControl;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;


public class Cadastro_usuario extends Activity {

	private static final String PREF_NAME = "dadoslogin";
	StringBuilder sb;
	private ProgressDialog pdia;
	private String nome = "";
	private String email = "";
	private String telefone = "";
	private Integer codigo;
	private RadioButton radioEnvioButton;
	private String confirmacao;
	EditText edtTelefone;


	public int aleatoriar() {
		Random random = new Random();
		return random.nextInt((9999 - 1000) + 1) + 1000;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cadastro_usuario);
		edtTelefone = (EditText) findViewById(R.id.edt_tel_cad_usu);

		edtTelefone.addTextChangedListener(new MaskWatcher("##-#####-####"));

		restoreActionBar();
	}

	public boolean validaCampo(EditText edt) {
		if (!StringUtils.isNullOrEmpty(edt.getText().toString())) {
			return true;
		}
		edt.setError("Digite o Codigo");
		return false;
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		getActionBar().setCustomView(R.layout.action_bar_layout);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setIcon(R.drawable.sf);

	}

	public Boolean buscar() throws InterruptedException{

		InputStream in = null;
		SharedPreferences settings = getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		EditText codigo = (EditText) findViewById(R.id.edt_codigo_condo);

		if (validaCampo(codigo)) {

			try {

				Conexao conexao = new Conexao();

				in = conexao.OpenHttpConnection("http://android.hidroluz.com.br/php/busca_clientes.php?codigo="
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


				for (int i = 0; i < itemNodes.getLength(); i++) {
					Node itemNode = itemNodes.item(i);
					if (itemNode.getNodeType() == Node.ELEMENT_NODE) {

						Element definitonElement = (Element) itemNode;
						NodeList login = (definitonElement)
								.getElementsByTagName("codigo");

						for (int j = 0; j < login.getLength(); j++) {

							if (((Element) login.item(j)).getChildNodes()
									.item(0).getNodeValue().equals("false")) {

								new SweetAlertDialog(Cadastro_usuario.this, SweetAlertDialog.WARNING_TYPE).
										setTitleText("Atenção !!!").setContentText("Codigo de condominio inexistente").
										setConfirmText("Confirmar").show();

								return false;


							} else {


								SharedPreferences.Editor editor = settings
										.edit();

								editor.putString("COD_CONDOMINIO", codigo.getText().toString());
								editor.commit();

								return true;

							}

						}
					}

				}

			} catch (Exception e) {


			}


		}

		return false;

	}

	public void cadastrar(View v) throws InterruptedException {

		codigo = aleatoriar();

		EditText edtNome = (EditText) findViewById(R.id.nome_cad_usu);
		EditText edtEmail = (EditText) findViewById(R.id.edt_email_cad_usu);
		EditText edtTelefone = (EditText) findViewById(R.id.edt_tel_cad_usu);
		EditText edtSenha = (EditText) findViewById(R.id.edt_senha_cad_usu);
		radioEnvioButton = (RadioButton) findViewById(R.id.radio_SMS);
		RadioButton radioEnvioButton2 = (RadioButton) findViewById(R.id.radio_email);

		if (buscar()){



			if (radioEnvioButton.isChecked()) {

				confirmacao = radioEnvioButton.getText().toString();

			}

			if (radioEnvioButton2.isChecked()) {

				confirmacao = radioEnvioButton2.getText().toString();

			}

			telefone = edtTelefone.getText().toString();

			telefone = telefone.replaceAll("-", "");

			if (!StringUtils.filter_telefone(telefone)) {

				edtTelefone.setError("Telefone Invalido");

			}

			Log.e("ok", telefone);

			if (!validaCampo(edtNome) || !validaCampo(edtEmail)
					|| !validaCampo(edtTelefone) || !validaCampo(edtSenha)
					|| !StringUtils.filter_nome(edtNome)
					|| !StringUtils.filter_email(edtEmail)) {
				return;
			}
			if (new VerificarConexao().isConnect(getApplicationContext())) {
				try {
					this.sb = new StringBuilder();
					this.sb.append("&nome=");
					this.sb.append(URLEncoder.encode(edtNome.getText().toString(),
							"UTF-8"));
					this.sb.append("&email=");
					this.sb.append(URLEncoder.encode(edtEmail.getText().toString(),
							"UTF-8"));
					this.sb.append("&telefone=");
					this.sb.append(URLEncoder.encode(telefone, "UTF-8"));
					this.sb.append("&senha=");
					this.sb.append(URLEncoder.encode(edtSenha.getText().toString(),
							"UTF-8"));
					this.sb.append("&codigo=");
					this.sb.append(codigo);

					nome = edtNome.getText().toString();
					email = edtEmail.getText().toString();
					telefone = edtTelefone.getText().toString();

					SharedPreferences settings = getSharedPreferences(PREF_NAME,
							MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();

					editor.putString("EMAIL_USU", edtEmail.getText().toString());
					editor.putString("NOME", edtNome.getText().toString());
					editor.putString("CODIGO_CONFIR", codigo.toString());
					editor.putString("TELE_USU", edtTelefone.getText().toString());

					editor.commit();

					new CadastrarUsuario().execute();


// return;
				} catch (Exception e) {
					return;
				}


			}

		}

	}

	class CadastrarUsuario extends AsyncTask<Void, Void, String> {

		private Context context;

		SweetAlertDialog pDialog = new SweetAlertDialog(Cadastro_usuario.this,
				SweetAlertDialog.PROGRESS_TYPE);


		CadastrarUsuario() {
		}

		public CadastrarUsuario(Context context) {
			super();
			this.context = context;
		}

		protected void onPreExecute() {
			super.onPreExecute();

			pDialog.setTitleText("Aguarde");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		protected void onPostExecute(String s) {
			super.onPostExecute(s);



			if (!s.equals("NAO")) {

				Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG)
						.show();

				StringBuffer texto = new StringBuffer();
				String center = "center";
				String zero = "0";
				String seis = "600";
				String logo = "https://uploaddeimagens.com.br/images/001/089/101/full/LogoOriginal.png";

				texto.append("<table border=" + 0 + " width=" + 600
						+ " cellspacing=" + 0 + " cellpadding=" + 0 + " align="
						+ "center" + ">");
				texto.append("<tbody>");
				texto.append("<tr bgcolor=" + "#E9EBEC" + " height=" + 80 + ">");
				texto.append("<td align=" + "center" + "><img src=" + logo
						+ " alt=" + "Logo_Original" + " border=" + 0
						+ " width=" + 132 + " height=" + 48 + ">&nbsp;</td>");
				texto.append("</tr>");
				texto.append("</tbody>");
				texto.append("<tr>");

				texto.append("<table border=" + 0 + " width=" + 600
						+ " cellspacing=" + 0 + " + align=" + "center" + ">");
				texto.append("<tbody>");
				texto.append("<tr>");

				texto.append("<td align=" + "left" + " height=" + 60 + "width="
						+ 600 + ">");

				texto.append("<h4 style=" + center + ">Olá, " + nome
						+ "!;</h4>");
				texto.append("<h4 style=" + center
						+ ">Seu Código de Confirmação:.</h4>");
				texto.append("<h4 style=" + center + ">" + codigo + "</h4>");
				texto.append("</td></tr><tr><td>");
				texto.append("<table border=" + 0 + " width=" + 600
						+ " cellspacing=" + 0 + " cellpadding=" + 0 + " align="
						+ "center" + ">");
				texto.append("<tbody><tr bgcolor=" + "#E9EBEC" + " height="
						+ 80 + ">");
				texto.append("<td style=" + "width: 435px;" + "height=" + 80
						+ ">");
				texto.append("<h4>&nbsp;Conheça a Hidroluz nas Redes Sociais:</h4>");
				texto.append("</td>");
				texto.append("<td style=" + "width: 77.5px;" + " align="
						+ "center" + " valign=" + "middle " + " height="
						+ 50
						+ "><a title="
						+ "Facebook Hidroluz"
						+ " href="
						+ "https://www.facebook.com/grupohidroluz/"
						+ "rel="
						+ "noopener"
						+ " data-saferedirecturl="
						+ "https://www.google.com/url?hl=pt-BR&amp;q=https://www.facebook.com/bancointeroficial&amp;source=gmail&amp;ust=1505310262242000&amp;usg=AFQjCNHwA7jbIcJdJJzccSCvsARnMtrF9g"
						+ "><img class="
						+ "CToWUd"
						+ " src="
						+ "https://ci3.googleusercontent.com/proxy/dhmsFGOPjq7FXnyJaV73yY0xVxFpEP0iXfGDehXkMVzJvz0BA7c_mx4FBEqgae_SjZMdgjC72FujU9PhytaXIiEqdApZxf7MKJFs5I09KNm3O4Mu5Dn0boSui5Qs405am1EfJ6h8lXIZNeuKI2xNs3PwRHffqWs=s0-d-e1-ft#http://marketing.bancointer.com.br/comunicacao/e-mail-mkt-institucional/imagens/icon-facebook.png"
						+ " alt="
						+ "Facebook Hidroluz"
						+ " width="
						+ 24
						+ " height=" + 24 + " border=" + 0 + " /></a></td>");
				texto.append(" <td style=" + "width: 122.5px;" + " height="
						+ 50 + ">");
				texto.append("<td style=" + "width: 51px;" + ">&nbsp;</td>");
				texto.append("</tr></tbody></table>");
				texto.append("</td></tr>");
				texto.append("</td></tr>");
				texto.append("</tbody></table></table>");

// new EnviarEmail(Cadastro_usuario.this,
				// "Cadastro no Aplicativo Hidroluz", texto.toString(), email)

				// .execute();

				if (confirmacao.equals("SMS")) {

					pDialog.dismiss();

					new EnviarSms(Cadastro_usuario.this, "", "").execute();

				} else {
					new EnviarEmail(Cadastro_usuario.this,
							"Cadastro no Aplicativo Hidroluz",
							texto.toString(), email).execute();

				}

			} else {

				new SweetAlertDialog(Cadastro_usuario.this, SweetAlertDialog.SUCCESS_TYPE).
						setTitleText("Email Já cadastrado !!!")
						.setContentText("Este email já foi cadastrado, esqueceu sua senha ?")
						.setConfirmText("Sim").setCancelText("Não")
						.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sweetAlertDialog) {
								Intent it = new Intent(
										Cadastro_usuario.this,
										MainActivity.class);
								Cadastro_usuario.this.finish();
								Cadastro_usuario.this.startActivity(it);

							}
						}).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						Intent it = new Intent(
								Cadastro_usuario.this,
								MainActivity.class);
						Cadastro_usuario.this.finish();
						Cadastro_usuario.this.startActivity(it);

					}
				}).show();

				Intent it = new Intent(
						Cadastro_usuario.this,
						MainActivity.class);
				Cadastro_usuario.this.finish();
				Cadastro_usuario.this.startActivity(it);

			}


		}

		protected String doInBackground(Void... params) {
			String result = "";
			try {

				JSONObject obj = new JSONObject(
						new HttpRemote()
								.getPost(
										"http://android.hidroluz.com.br/php/cadastra_usuario.php",
										Cadastro_usuario.this.sb.toString()));

				result = obj.getString("resultado");

			} catch (Exception e) {

				return e.getMessage();
			}
			return result;
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

		SweetAlertDialog pDialog = new SweetAlertDialog(Cadastro_usuario.this,
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
// Showing progress dialog while sending email

			pDialog.setTitleText("Aguarde !!!");
			pDialog.setContentText("Enviando Mensagem Aguarde");
			pDialog.setCancelable(false);

		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);

			pDialog.dismiss();

			new SweetAlertDialog(Cadastro_usuario.this, SweetAlertDialog.SUCCESS_TYPE).
					setTitleText("Usuário Cadastrado")
					.setContentText("Foi enviado para o seu email, o código de confirmação.")
					.setConfirmText("OK").showCancelButton(true)
					.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {
							Intent it = new Intent(
									Cadastro_usuario.this,
									Codigo_confir.class);
							Cadastro_usuario.this.finish();
							Cadastro_usuario.this.startActivity(it);


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
				mm.setText(message, "utf-8", "html");

// Sending email
				Transport.send(mm);

			} catch (MessagingException e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	class EnviarSms extends AsyncTask<Void, Void, String> {

		private Context context;
		private String telefone;
		private String message;
		private ProgressDialog progressDialog;

		SweetAlertDialog pDialog = new SweetAlertDialog(Cadastro_usuario.this,
				SweetAlertDialog.PROGRESS_TYPE);

		public EnviarSms() {
// TODO Auto-generated constructor stub
		}

		public EnviarSms(Context context, String telefone, String message) {
			super();
			this.context = context;
			this.telefone = telefone;
			this.message = message;
		}

		@Override
		protected String doInBackground(Void... params) {

			InputStream in = null;
			String resultado = "";

			try {

				Conexao conexao = new Conexao();

				Cadastro_usuario.this.telefone = Cadastro_usuario.this.telefone.replaceAll("-", "");

				String msg = codigo + " e seu codigo de confirmação digite no Aplicativo para continuar seu cadastro. A Hidroluz Agradece.";

// in =
				// OpenHttpConnection("http://hidroluzmedicao.ddns.net:8080/php/listar.php?codigo=1266&anomes=201502");
				String url = "https://buscarcep.com.br/ws2/?telefone=" + "55"
						+ Cadastro_usuario.this.telefone + "&texto=" + codigo
						+ "&chave=" + Config.CHAVE + "&identificador="
						+ Config.IDENTIFICADOR;
				in = conexao.OpenHttpConnection(url);
				Document doc = null;
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db;
				db = dbf.newDocumentBuilder();
				doc = db.parse(in);
				doc.getDocumentElement().normalize();

			} catch (Exception e) {
// TODO: handle exception
			}

			return null;
		}

		protected void onPreExecute() {
			super.onPreExecute();

			pDialog.setTitleText("Aguarde !!!");
			pDialog.setContentText("Enviando Mensagem Aguarde");
			pDialog.setCancelable(false);
		}

		@Override
		protected void onPostExecute(String result) {
// TODO Auto-generated method stub
			super.onPostExecute(result);

			pDialog.dismiss();

			new SweetAlertDialog(Cadastro_usuario.this, SweetAlertDialog.SUCCESS_TYPE).
					setTitleText("Usuário Cadastrado")
					.setContentText("Foi enviado uma Mensaguem com o código de confirmação.")
					.setConfirmText("OK").showCancelButton(true)
					.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {
							Intent it = new Intent(
									Cadastro_usuario.this,
									Codigo_confir.class);
							Cadastro_usuario.this.finish();
							Cadastro_usuario.this.startActivity(it);


						}
					}).show();

		}

	}
}