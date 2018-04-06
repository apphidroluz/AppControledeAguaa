package br.com.hidroluz.HidroluzWaterControl;

import java.io.InputStream;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Editar_Cadastro extends Fragment {

	private static final String PREF_NAME = "dadoslogin";
	private ProgressDialog pdia;
	View rootView;
	StringBuilder sb;
	String nome = "";
	String email = "";
	String telefone = "";
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// ConfiguracaoOrietancao.setarRetrato(getActivity());
		rootView = inflater.inflate(R.layout.activity_editar__cadastro,
				container, false);

		Button btn = (Button) rootView.findViewById(R.id.btn_alterar);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					alterar_cadastro();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		return rootView;
	}

	@Override
	public void onStart() {
		recuperar();

		super.onStart();
	}

	public void recuperar() {

		SharedPreferences settings = this.getActivity().getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);

		EditText edtNome = (EditText) getView().findViewById(R.id.txtnome_alt);
		EditText edtEmail = (EditText) getView()
				.findViewById(R.id.txtemail_alt);
		EditText edtTelefone = (EditText) getView().findViewById(
				R.id.txttelefone_alt);
		EditText edtlogin = (EditText) getView()
				.findViewById(R.id.txtlogin_alt);
		EditText edtsenha = (EditText) getView()
				.findViewById(R.id.txtsenha_alt);

		InputStream in = null;

		String lclogin = settings.getString("USUARIO", "");
		String lcsenha = settings.getString("SENHA", "");

		try {

			Conexao conexao = new Conexao();

			// in =
			// OpenHttpConnection("http://hidroluzmedicao.ddns.net:8080/php/listar.php?codigo=1266&anomes=201502");
			in = conexao
					.OpenHttpConnection("http://android.hidroluz.com.br/php/recupera_cadastro.php?email="
							+ lclogin);
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
								.getElementsByTagName("email");

						for (int j = 0; j < login.getLength(); j++) {

							Element acesso = (Element) login.item(j);
							NodeList textNodes = ((Node) acesso)
									.getChildNodes();
							resposta = ((Node) textNodes.item(0))
									.getNodeValue();

							

							NodeList telNode = (definitonElement)
									.getElementsByTagName("telefone");
							Element telElment = (Element) telNode.item(0);
							NodeList telNodes = ((Node) telElment)
									.getChildNodes();
							telefone = ((Node) telNodes.item(0)).getNodeValue();

							NodeList emailNode = (definitonElement)
									.getElementsByTagName("email");
							Element emailElment = (Element) emailNode.item(0);
							NodeList emailNodes = ((Node) emailElment)
									.getChildNodes();
							email = ((Node) emailNodes.item(0)).getNodeValue();

							NodeList nomeNode = (definitonElement)
									.getElementsByTagName("nome");
							Element nomeElment = (Element) nomeNode.item(0);
							NodeList nomeNodes = ((Node) nomeElment)
									.getChildNodes();
							nome = ((Node) nomeNodes.item(0)).getNodeValue();

							SharedPreferences.Editor editor = settings.edit();

							editor.putString("NOME", nome);
							editor.putString("EMAIL", email);
							editor.putString("TELEFONE", telefone);

							editor.commit();

						}
					}

				}

				edtNome.setText(nome);
				edtEmail.setText(email.toLowerCase());
				edtTelefone.setText(telefone);
				edtlogin.setText(lclogin);
				edtsenha.setText(lcsenha);

			} catch (ParserConfigurationException e) {
				Toast.makeText(getActivity().getApplicationContext(),
						e.getMessage(), Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void alterar_cadastro() throws InterruptedException {

		EditText edtNome = (EditText) getView().findViewById(R.id.txtnome_alt);
		EditText edtEmail = (EditText) getView()
				.findViewById(R.id.txtemail_alt);
		EditText edtTelefone = (EditText) getView().findViewById(
				R.id.txttelefone_alt);
		EditText edtSenha = (EditText) getView()
				.findViewById(R.id.txtsenha_alt);
		

		if (validaCampo(edtNome) && validaCampo(edtEmail)
				&& validaCampo(edtTelefone)) {

			VerificarConexao conexao = new VerificarConexao();
			if (conexao.isConnect(getActivity().getApplicationContext())) {
				try {
					sb = new StringBuilder();

					

					sb.append("&senha_nova=");
					sb.append(URLEncoder.encode(edtSenha.getText().toString(),
							"UTF-8"));

					sb.append("&email=");
					sb.append(edtEmail.getText().toString()
							);

					sb.append("&telefone=");
					sb.append(URLEncoder.encode(edtTelefone.getText()
							.toString().trim(), "UTF-8"));

					

					CadastrarUsuario cadastrarUsuario = new CadastrarUsuario();
					// // Executar o web service em outra thread
					cadastrarUsuario.execute();

					SharedPreferences settings = this.getActivity()
							.getSharedPreferences(PREF_NAME,
									Context.MODE_PRIVATE);

					SharedPreferences.Editor editor = settings.edit();
					
					editor.putString("SENHA", edtSenha.getText().toString());

					editor.commit();

				} catch (Exception e) {

				}
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"SEM INTERNET", Toast.LENGTH_LONG).show();
			}

		}

	}

	public boolean validaCampo(EditText edt) {

		if (StringUtils.isNullOrEmpty(edt.getText().toString())) {
			edt.setError("O Campo é Obrigatório");
			return false;
		}
		return true;
	}

	class CadastrarUsuario extends AsyncTask<Void, Void, String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(Editar_Cadastro.this
                .getActivity(), SweetAlertDialog.PROGRESS_TYPE);

		// Chamado antes da comunicacao ocorrer
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
            pDialog.setTitleText("Aguarde");
            pDialog.setCancelable(false);
            pDialog.show();


		}

		// Chamado depois da comunicacao
		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);

			pDialog.dismiss();
			Toast.makeText(getActivity().getApplicationContext(), s,
					Toast.LENGTH_LONG).show();

		}

		@Override
		protected String doInBackground(Void... params) {

			String result = "";
			String teste = sb.toString();

			try {
				HttpRemote httpRemote = new HttpRemote();
				// {"resultado":"Filme cadastrado com sucesso"}

				String resp = httpRemote
						.getPost(
								"http://android.hidroluz.com.br/php/edita_cadastro.php",
								sb.toString());

				JSONObject obj = new JSONObject(resp);
				result = obj.getString("resultado");

			} catch (Exception e) {
				result = e.getMessage();
			}
			return result;

		}
	}

}
