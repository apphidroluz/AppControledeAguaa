package br.com.hidroluz.HidroluzWaterControl;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.w3c.dom.Document;

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



public class Codigo_confir extends Activity {
	private static final String PREF_NAME = "dadoslogin";
	private String email;
	private String codigo;
	private String telefone;
	private String confirmacao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_codigo_confir);
		restoreActionBar();

		SharedPreferences settings = getSharedPreferences(PREF_NAME,
				MODE_PRIVATE);

		email = settings.getString("EMAIL_USU", "");
		codigo = settings.getString("CODIGO_CONFIR", "");
		telefone = settings.getString("TELE_USU", "");
		confirmacao = settings.getString("CONFIRM_USU", "");

	}

	public void buscar(View v) {
		
		EditText edtcod = (EditText) findViewById(R.id.edt_codigo_confir);
		
		if(edtcod.getText().toString().equals(codigo)){
			Toast.makeText(getApplicationContext(), "Código OK",
					Toast.LENGTH_LONG).show();

			Intent it = new Intent(this,
					Cadastro_unidades.class);
			Codigo_confir.this.finish();
			//it.putExtra("codigo", resposta);
			startActivity(it);
		}else{

			new SweetAlertDialog(Codigo_confir.this, SweetAlertDialog.WARNING_TYPE).
					setTitleText("Atenção !!!")
					.setContentText("Código Incorreto")
					.setConfirmText("Confirmar").show();
		}

	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		getActionBar().setCustomView(R.layout.action_bar_layout);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
	    actionBar.setIcon(R.drawable.sf);
		//actionBar.setNavigationMode(0);
	}

	public void reenviar(View v) {


		if (confirmacao.equals("SMS")){
			new Codigo_confir.ReenviarSms(Codigo_confir.this, "", "").execute();
		}else {

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

			texto.append("<h4 style=" + center + ">Olá"
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


			new Codigo_confir.ReenviarEmail(Codigo_confir.this,
					"Cadastro no Aplicativo Hidroluz",
					texto.toString(), email).execute();

		}


	}

	class ReenviarEmail extends AsyncTask<Void, Void, String> {

		private Context context;
		private Session session;

		// Information to send email
		private String email;
		private String subject;
		private String message;
		private String email_cc;

		SweetAlertDialog pDialog = new SweetAlertDialog(Codigo_confir.this,
				SweetAlertDialog.PROGRESS_TYPE);

		// Progressdialog to show while sending email
		private ProgressDialog progressDialog;

		public ReenviarEmail(Context context, String subject, String message,
						   String email) {
			this.context = context;
			this.subject = subject;
			this.message = message;
			this.email = email;
		}

		// Class Constructor
		public ReenviarEmail(Context context, String email, String subject,
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

			new SweetAlertDialog(Codigo_confir.this, SweetAlertDialog.SUCCESS_TYPE).
					setTitleText("Codigo Reenviado")
					.setContentText("Aguarde!!")
					.setConfirmText("OK").show();
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

	class ReenviarSms extends AsyncTask<Void, Void, String> {

		private Context context;
		private String telefone;
		private String message;
		private ProgressDialog progressDialog;

		SweetAlertDialog pDialog = new SweetAlertDialog(Codigo_confir.this,
				SweetAlertDialog.PROGRESS_TYPE);

		public ReenviarSms() {
			// TODO Auto-generated constructor stub
		}

		public ReenviarSms(Context context, String telefone, String message) {
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


				Codigo_confir.this.telefone = Codigo_confir.this.telefone.replaceAll("-", "");
				String msg = codigo + " e seu codigo de confirmação digite no Aplicativo para continuar seu cadastro. A Hidroluz Agradece.";

				// in =
				// OpenHttpConnection("http://hidroluzmedicao.ddns.net:8080/php/listar.php?codigo=1266&anomes=201502");
				String url = "https://buscarcep.com.br/ws2/?telefone=" + "55"
						+ Codigo_confir.this.telefone + "&texto=" + codigo
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

			new SweetAlertDialog(Codigo_confir.this, SweetAlertDialog.SUCCESS_TYPE).
					setTitleText("Codigo Reenviado")
					.setContentText("Aguarde!!")
					.setConfirmText("OK").show();

		}

	}
}
