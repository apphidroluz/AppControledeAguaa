package br.com.hidroluz.HidroluzWaterControl;

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

import org.json.JSONObject;

import br.com.hidroluz.HidroluzWaterControl.Cadastro_usuario.CadastrarUsuario;
import br.com.hidroluz.HidroluzWaterControl.Cadastro_usuario.EnviarEmail;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Esqueceu_senha extends Activity {

	private Integer senha;
	StringBuilder sb;
	private ProgressDialog pdia;
	private String email;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		restoreActionBar();
		setContentView(R.layout.activity_esqueceu_senha);
	}

	public void buscar(View v) {

		senha = aleatoriar();

		EditText edtemail = (EditText) findViewById(R.id.edt_email_senha);

		email = edtemail.getText().toString();

		// if (!validaCampo(edtemail) || StringUtils.filter_email(edtemail)) {
		// return;
		// }

		if (new VerificarConexao().isConnect(getApplicationContext())) {

			try {

				this.sb = new StringBuilder();
				this.sb.append("&email=");
				this.sb.append(URLEncoder.encode(edtemail.getText().toString(),
						"UTF-8"));
				this.sb.append("&senha=");
				this.sb.append(URLEncoder.encode(senha.toString(), "UTF-8"));

				new EnviaSenha().execute();

			} catch (Exception e) {

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

	public int aleatoriar() {
		Random random = new Random();
		return random.nextInt((9999 - 1000) + 1) + 1000;
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		getActionBar().setCustomView(R.layout.action_bar_layout);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setIcon(R.drawable.sf);

	}

	class EnviaSenha extends AsyncTask<Void, Void, String> {

		private Context context;

		SweetAlertDialog pDialog = new SweetAlertDialog(Esqueceu_senha.this,
				SweetAlertDialog.PROGRESS_TYPE);

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setTitleText("Aguarde");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = "";
			try {

				JSONObject obj = new JSONObject(
						new HttpRemote()
								.getPost(
										"http://android.hidroluz.com.br/php/atualiza_senha.php",
										Esqueceu_senha.this.sb.toString()));

				result = obj.getString("resultado");

			} catch (Exception e) {

				return e.getMessage();
			}
			return result;
		}

		protected void onPostExecute(String s) {
			super.onPostExecute(s);


			if (s.equals("SIM")) {



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

				texto.append("<h4 style=" + center + ">Olá !</h4>");
				texto.append("<h4 style="
						+ center
						+ ">Não consegue lembrar sua senha? Não tem problema, acontece com todos nós.</h4>");
				texto.append("<h4 style=" + center
						+ ">Abaixo sua nova Senha:</h4>");
				texto.append("<h4 style=" + center + ">Usuário: " + email
						+ "</h4>");
				texto.append("<h4 style=" + center + ">Senha: " + senha
						+ "</h4>");
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

				new EnviarEmail(Esqueceu_senha.this,
						"Cadastro no Aplicativo Hidroluz", texto.toString(),
						email).execute();

			} else if (s.equals("NAO")) {

				pDialog.dismiss();

				new SweetAlertDialog(Esqueceu_senha.this, SweetAlertDialog.WARNING_TYPE)
						.setTitleText("Atenção !!!")
						.setContentText("O email não foi encontrado no nosso Cadastro.")
						.setConfirmText("Confirmar").show();



			} else {

				Intent it = new Intent(Esqueceu_senha.this, Tela_erro.class);
				Esqueceu_senha.this.finish();

				startActivity(it);
			}

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

		// Progressdialog to show while sending email
		SweetAlertDialog pDialog = new SweetAlertDialog(Esqueceu_senha.this, SweetAlertDialog.PROGRESS_TYPE);

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

			pDialog.setTitleText("Enviando");
			pDialog.setCancelable(false);
			pDialog.show();
			// Showing progress dialog while sending email

		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);

			pDialog.dismiss();


			new SweetAlertDialog(Esqueceu_senha.this, SweetAlertDialog.SUCCESS_TYPE).
					setTitleText("Senha Enviada com Sucesso !!!")
					.setContentText("Foi enviado para o seu email, a sua nova senha.")
					.setConfirmText("OK")
					.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sDialog) {
							Intent it = new Intent(Esqueceu_senha.this,
									MainActivity.class);
							Esqueceu_senha.this.finish();
							Esqueceu_senha.this.startActivity(it);
						}
					})
					.show();

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
}