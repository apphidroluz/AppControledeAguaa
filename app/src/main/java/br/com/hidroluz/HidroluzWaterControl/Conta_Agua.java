package br.com.hidroluz.HidroluzWaterControl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class Conta_Agua extends Fragment {

	View rootView;
	ImageView imageView;
	File caminhoFoto;
	String nomefoto;
	String imagemPath;
	private String nome;
	private Bitmap mImageBitmap;
	private Uri uriImagem = null;
	private static final String PREF_NAME = "dadoslogin";
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

	public Conta_Agua() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_conta__agua, container,
				false);

		SharedPreferences settings = this.getActivity().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);

		nome = settings.getString("CONDO", "");

		imageView = (ImageView) v.findViewById(R.id.imagemcamera);
		Button btn = (Button) v.findViewById(R.id.btncamera);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// caminhoFoto = new File(file, nomefoto);
				// Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imaFile));
				// startActivity(it);
				// startActivityForResult(it, 7);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// Cria um arquivo para salvar a imagem.
				uriImagem = ProcessaImagens.getOutputMediaFileUri(
						ProcessaImagens.MEDIA_TYPE_IMAGE, getActivity()
								.getApplicationContext());
				// Passa para intent um objeto URI contendo o caminho e o nome
				// do arquivo onde desejamos salvar a imagem. Pegaremos atraves
				// do parametro data do metodo onActivityResult().
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImagem);
				// Inicia a intent para captura de imagem e espera pelo
				// resultado.
				startActivityForResult(intent,
						CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		});

		Button btn2 = (Button) v.findViewById(R.id.btnenviar);
		btn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e("OKKKKKK", "OKKKKKK");

				new EnviarEmail(getActivity(), "CONTA DE ÁGUA DO CONDOMINIO "
						+ nome, "EM ANEXO.", "contas.agua@hidroluz.com.br")
						.execute();

			}
		});

		return v;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Se finalizou a activity em startForActivityResult.
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
				imagemPath = uriImagem.getPath();

				String timeStamp = new SimpleDateFormat("yyyyMMdd_HH")
						.format(new Date());
				// File mediaFile;

				// mediaFile =

				nomefoto = "IMG_" + timeStamp + ".jpg";
				// ela retorna tanto um bitmap como um array de bytes.
				List<Object> imagemCompactada = ProcessaImagens
						.compactarImagem(uriImagem.getPath());
				Bitmap imagemBitmap = (Bitmap) imagemCompactada.get(0);
				byte[] imagemBytes = (byte[]) imagemCompactada.get(1);

				imageView.setImageBitmap(imagemBitmap);

			}
		}
		// Se cancelou a activity em startForActivityResult.
		else if (resultCode == Activity.RESULT_CANCELED) {
			if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
				// Usuario cancelou a captura da imagem.
				Log.d(getTag(), "Captura de imagem CANCELADA!");
			}
		}
		// Se ocorreu algum erro na activity em startForActivityResult.
		else {
			// Captura da imagem falhou, avisa ao usuario.
			Toast.makeText(getActivity().getApplicationContext(),
					"FALHA! A captura da imagem falhou!", Toast.LENGTH_LONG)
					.show();
			Log.e(getTag(), "FALHA! A captura da imagem falhou!");
		}
	}

	@Override
	public void onStart() {

		super.onStart();
	}

	// public void EnviarConta(View v) throws InterruptedException {
	//
	// Log.e("OKKKKKK", "OKKKKKK");
	//
	// new EnviarEmail(getActivity(), "Conta Agua", "ok",
	// "conta.agua@hidroluz.com.br").execute();
	//
	// }

	class EnviarEmail extends AsyncTask<Void, Void, String> {

		private Context context;
		private Session session;

		// Information to send email
		private String email;
		private String subject;
		private String message;
		private String email_cc;

        SweetAlertDialog pDialog = new SweetAlertDialog(Conta_Agua.this.getActivity(),
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
            pDialog.setTitleText("Aguarde");
            pDialog.setCancelable(false);
            pDialog.show();
		}

		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			pDialog.dismiss();

            new SweetAlertDialog(Conta_Agua.this.getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Obrigado")
                    .setContentText("Conta enviada com Sucesso")
                    .setConfirmText("Confirmar").show();

		}

		@Override
		protected String doInBackground(Void... arg0) {

			Log.e("OKKKKKK2", "OKKKKKK2");
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

				Log.e("OKKKKKK4", "OKKKKKK4");

				Multipart multipart = new MimeMultipart();

				MimeBodyPart attachmentBodyPart = new MimeBodyPart();

				DataSource source = new FileDataSource(imagemPath); // ex
																	// :
																	// "C:\\test.pdf"
				attachmentBodyPart.setDataHandler(new DataHandler(source));

				attachmentBodyPart.setFileName(nomefoto); // ex : "test.pdf"

				multipart.addBodyPart(attachmentBodyPart); // add the
															// attachement part

				mm.setContent(multipart);

				// Sending email
				Transport.send(mm);

			} catch (MessagingException e) {
				e.printStackTrace();
			}

			return null;

		}

	}

}
