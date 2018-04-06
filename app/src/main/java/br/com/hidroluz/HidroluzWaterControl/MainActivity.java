package br.com.hidroluz.HidroluzWaterControl;

import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends Activity implements Serializable {


	private static final long serialVersionUID = 1L;
	private static final String PREF_NAME = "dadoslogin";
	public static ProgressDialog progressDialog;
	boolean clicado = false;
	private List<String> Unidades;
	View view;
	LoginButton login_button;
    CallbackManager callbackManager;
    String nome_global;
    String email_global;
    String id_global;
    String foto_global;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

		restoreActionBar();

		login_button = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();

        loginWithFb();


		setContentView(R.layout.activity_main);

		// Colocado para liberar politica de o funcionamento do WebServ
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		// CheckBox chek = (CheckBox) findViewById(R.id.checkBox1);

		SharedPreferences settings = getSharedPreferences(PREF_NAME,
				MODE_PRIVATE);
		String logado = settings.getString("logado", "0");
		Boolean click = settings.getBoolean("Click", true);
		String login = settings.getString("USUARIO", "");
		String senha = settings.getString("SENHA", "");

		if (click) {

			EditText lclogin = (EditText) findViewById(R.id.editUser);
			EditText lcsenha = (EditText) findViewById(R.id.editPass);
			//TextView conta = (TextView) findViewById(R.id.OutraConta);

			lclogin.setText(login);
			lcsenha.setText(senha);

			if (login.length() > 0) {
				//conta.setVisibility(View.VISIBLE);
			}

		}

		if (logado.equals("1") && click) {
			Intent it = new Intent(getApplicationContext(),
					DrawerActivity.class);
			startActivity(it);
		} else {
			if (logado.equals("2") && click) {
				Intent it = new Intent(getApplicationContext(),
						Drawer_adm.class);
				startActivity(it);
			}
		}

	}

	private void loginWithFb(){

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                System.out.println("onSuccess");
				SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this,
						SweetAlertDialog.PROGRESS_TYPE);
				pDialog.setTitleText("Aguarde !!!");
				pDialog.setContentText("Procesando dados...");
				pDialog.setCancelable(false);
				pDialog.show();
                String accessToken = loginResult.getAccessToken().getToken();

                Log.i("accessToken", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("TESTE", "ENTROU");
                        Bundle bFacebookData = getFacebookData(object);
                        cadastro_email_face();
                    }
                });

                Log.i("TESTE", "ENTROU1");
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAndWait();






                progressDialog.cancel();



            }

            @Override
            public void onCancel() {

                Toast.makeText(MainActivity.this, "Cancelado",
                        Toast.LENGTH_LONG).show();


            }

            @Override
            public void onError(FacebookException error) {


                Toast.makeText(MainActivity.this, "Erro",
                        Toast.LENGTH_LONG).show();


            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
	
	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.hide();


	}



	public void entrar(View v) {
		Funcoes funcao = new Funcoes();
		if (!funcao.netWorkdisponibilidade(this)) {
			Toast.makeText(this, "Sem conexão com rede, verifique",
					Toast.LENGTH_LONG).show();
		} else {

			final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this,
					SweetAlertDialog.PROGRESS_TYPE);
			pDialog.setTitleText("Aguarde !!!");
			pDialog.setContentText("Processando login.....");
			pDialog.setCancelable(false);
			pDialog.show();

			new Thread() {
				public void run() {
					try {

						logar();
					} catch (Exception e) {
						Log.e("tag", e.getMessage());
					}
					// encerra progress dialog
					pDialog.dismiss();
				}
			}.start();
		}

	}

	private void cadastro_email_face(){

        InputStream in = null;

            try {

                Conexao conexao = new Conexao();

                // in =
                // OpenHttpConnection("http://hidroluzmedicao.ddns.net:8080/php/listar.php?codigo=1266&anomes=201502");
                in = conexao
                        .OpenHttpConnection("http://android.hidroluz.com.br/php/login_facebook.php?usuario="
                                + email_global.toUpperCase() +"&nome="+nome_global+"&foto="+foto_global+"&id="+id_global);
                Document doc = null;
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
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
                String codigo = "";
                String resposta = "";
                String bloco = "";
                String unidade = "";
                String nome = "";
                String nomfant_apel = "";
                String nome_condo = "";
                String acesso_cadastrado = "";
                String usuario = "";
                String endereco = "";
                String bairro = "";
                String todas = "";

                Unidades = new ArrayList<String>();

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

                            if (resposta.equals("cadastro")) {
                                SharedPreferences settings = getSharedPreferences(PREF_NAME,
                                        MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();

                                editor.putString("EMAIL_USU", email_global);
                                editor.putString("USUARIO", email_global);
                                editor.putString("NOME", nome_global);
                                editor.commit();

                                Intent it = new Intent(this,
                                        Cadastro_unidades.class);
                                MainActivity.this.finish();
                                //it.putExtra("codigo", resposta);
                                startActivity(it);

                                ///CADASTRO

                            } else {

                                    NodeList codNode = (definitonElement)
                                            .getElementsByTagName("codigo");
                                    Element codElment = (Element) codNode
                                            .item(j);
                                    NodeList codNodes = ((Node) codElment)
                                            .getChildNodes();
                                    codigo = ((Node) codNodes.item(0))
                                            .getNodeValue();

                                    NodeList unidNode = (definitonElement)
                                            .getElementsByTagName("unidade");
                                    Element unidElment = (Element) unidNode
                                            .item(j);
                                    NodeList unidNodes = ((Node) unidElment)
                                            .getChildNodes();
                                    unidade = ((Node) unidNodes.item(0))
                                            .getNodeValue();

                                    NodeList blocoNode = (definitonElement)
                                            .getElementsByTagName("bloco");
                                    Element blocoElment = (Element) blocoNode
                                            .item(j);
                                    NodeList blocoNodes = ((Node) blocoElment)
                                            .getChildNodes();
                                    bloco = ((Node) blocoNodes.item(0))
                                            .getNodeValue();

                                    NodeList nomeNode = (definitonElement)
                                            .getElementsByTagName("nome");
                                    Element nomeElment = (Element) nomeNode
                                            .item(j);
                                    NodeList nomeNodes = ((Node) nomeElment)
                                            .getChildNodes();
                                    nome = ((Node) nomeNodes.item(0))
                                            .getNodeValue();

                                    NodeList apelNode = (definitonElement)
                                            .getElementsByTagName("nomfant_apel");
                                    Element apelElment = (Element) apelNode
                                            .item(j);
                                    NodeList apelNodes = ((Node) apelElment)
                                            .getChildNodes();
                                    nomfant_apel = ((Node) apelNodes.item(0))
                                            .getNodeValue();

                                    NodeList todasNode = (definitonElement)
                                            .getElementsByTagName("todas");
                                    Element todasElment = (Element) todasNode
                                            .item(j);
                                    NodeList todasNodes = ((Node) todasElment)
                                            .getChildNodes();
                                    todas = ((Node) todasNodes.item(0))
                                            .getNodeValue();

                                    Unidades.add(codigo + ";"
                                            + nomfant_apel.trim() + ";" + bloco
                                            + ";" + unidade);

                                }

                            }


                        if (!resposta.equals("cadastro") && !resposta.equals("false") ) {

                            if (login.getLength() > 1) {

                                Intent it = new Intent(this,
                                        Escolha_unidade.class);
                                MainActivity.this.finish();

                                SharedPreferences settings = getSharedPreferences(
                                        PREF_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings
                                        .edit();

                                Set<String> contactSet = new HashSet<String>();
                                contactSet.addAll(Unidades);
                                editor.putStringSet("UNI", contactSet);
                                editor.putString("NOME", nome);
                                editor.commit();

                                // it.putExtra("UNI", (Serializable) Unidades);

                                startActivity(it);

                            } else {

                                SharedPreferences settings = getSharedPreferences(
                                        PREF_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings
                                        .edit();

                                editor.putString("logado", "1");
                                editor.putString("USUARIO", email_global);
                                editor.putString("CodigoLogin", resposta);
                                editor.putString("UnidadeLogin", unidade);
                                editor.putString("BlocoLogin", bloco);
                                editor.putString("NOME", nome);
                                editor.putString("APELIDO", nomfant_apel);
                                editor.putString("ACESSO", acesso_cadastrado);
                                editor.putString("TODAS", todas);
                                Set<String> contactSet = new HashSet<String>();
                                contactSet.addAll(Unidades);
                                editor.putStringSet("UNI", contactSet);
                                editor.commit();

                                Intent it = new Intent(this,
                                        DrawerActivity.class);
                                MainActivity.this.finish();
                                it.putExtra("codigo", resposta);

                                startActivity(it);

                            }

                        }

                    }

                    }

            } catch (Exception e1) {
                h1.sendEmptyMessage(1);
            }



    }

	public void logar() throws InterruptedException {
		InputStream in = null;
		EditText lclogin = (EditText) findViewById(R.id.editUser);
		EditText lcsenha = (EditText) findViewById(R.id.editPass);

		if (lclogin.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "Informe seu login",
					Toast.LENGTH_SHORT).show();
			lclogin.requestFocus();
		} else {

			try {

				Conexao conexao = new Conexao();

				// in =
				// OpenHttpConnection("http://hidroluzmedicao.ddns.net:8080/php/listar.php?codigo=1266&anomes=201502");
				in = conexao
						.OpenHttpConnection("http://android.hidroluz.com.br/php/login_usuario.php?usuario="
								+ lclogin.getText().toString().toUpperCase()
										.trim().replaceAll(" ", "%20")
								+ "&senha=" + lcsenha.getText().toString());
				Document doc = null;
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
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
				String codigo = "";
				String resposta = "";
				String bloco = "";
				String unidade = "";
				String nome = "";
				String nomfant_apel = "";
				String nome_condo = "";
				String acesso_cadastrado = "";
				String usuario = "";
				String endereco = "";
				String bairro = "";
				String todas = "";

				Unidades = new ArrayList<String>();

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

							if (resposta.equals("false")) {
								h1.sendEmptyMessage(2);

							} else {
								if (resposta.equals("adm")) {

									NodeList endNode = (definitonElement)
											.getElementsByTagName("endereco");
									Element endElment = (Element) endNode
											.item(0);
									NodeList endNodes = ((Node) endElment)
											.getChildNodes();
									endereco = ((Node) endNodes.item(0))
											.getNodeValue();

									NodeList bairroNode = (definitonElement)
											.getElementsByTagName("bairro");
									Element bairroElment = (Element) bairroNode
											.item(0);
									NodeList bairroNodes = ((Node) bairroElment)
											.getChildNodes();
									bairro = ((Node) bairroNodes.item(0))
											.getNodeValue();

									NodeList nome_condoNode = (definitonElement)
											.getElementsByTagName("nomfant_apel");
									Element nome_condoElment = (Element) nome_condoNode
											.item(0);
									NodeList nome_condoNodes = ((Node) nome_condoElment)
											.getChildNodes();
									nome_condo = ((Node) nome_condoNodes
											.item(0)).getNodeValue();

									SharedPreferences settings = getSharedPreferences(
											PREF_NAME, MODE_PRIVATE);
									SharedPreferences.Editor editor = settings
											.edit();

									editor.putString("USUARIO", lclogin
											.getText().toString());
									editor.putString("EMAIL_USU", lclogin
											.getText().toString());
									editor.putString("logado", "2");
									editor.putString("SENHA", lcsenha.getText()
											.toString());
									editor.putString("ENDERECO", endereco);
									editor.putString("BAIRRO", bairro);
									editor.putString("CONDO", nome_condo);
									editor.commit();

									Intent it = new Intent(this,
											Drawer_adm.class);
									MainActivity.this.finish();

									startActivity(it);

								} else {

									NodeList codNode = (definitonElement)
											.getElementsByTagName("codigo");
									Element codElment = (Element) codNode
											.item(j);
									NodeList codNodes = ((Node) codElment)
											.getChildNodes();
									codigo = ((Node) codNodes.item(0))
											.getNodeValue();

									NodeList unidNode = (definitonElement)
											.getElementsByTagName("unidade");
									Element unidElment = (Element) unidNode
											.item(j);
									NodeList unidNodes = ((Node) unidElment)
											.getChildNodes();
									unidade = ((Node) unidNodes.item(0))
											.getNodeValue();

									NodeList blocoNode = (definitonElement)
											.getElementsByTagName("bloco");
									Element blocoElment = (Element) blocoNode
											.item(j);
									NodeList blocoNodes = ((Node) blocoElment)
											.getChildNodes();
									bloco = ((Node) blocoNodes.item(0))
											.getNodeValue();

									NodeList nomeNode = (definitonElement)
											.getElementsByTagName("nome");
									Element nomeElment = (Element) nomeNode
											.item(j);
									NodeList nomeNodes = ((Node) nomeElment)
											.getChildNodes();
									nome = ((Node) nomeNodes.item(0))
											.getNodeValue();

									NodeList apelNode = (definitonElement)
											.getElementsByTagName("nomfant_apel");
									Element apelElment = (Element) apelNode
											.item(j);
									NodeList apelNodes = ((Node) apelElment)
											.getChildNodes();
									nomfant_apel = ((Node) apelNodes.item(0))
											.getNodeValue();

									NodeList todasNode = (definitonElement)
											.getElementsByTagName("todas");
									Element todasElment = (Element) todasNode
											.item(j);
									NodeList todasNodes = ((Node) todasElment)
											.getChildNodes();
									todas = ((Node) todasNodes.item(0))
											.getNodeValue();

									Unidades.add(codigo + ";"
											+ nomfant_apel.trim() + ";" + bloco
											+ ";" + unidade);

								}

							}
						}

						if (!resposta.equals("adm") && !resposta.equals("false")) {

							if (login.getLength() > 1) {

								Intent it = new Intent(this,
										Escolha_unidade.class);
								MainActivity.this.finish();

								SharedPreferences settings = getSharedPreferences(
										PREF_NAME, MODE_PRIVATE);
								SharedPreferences.Editor editor = settings
										.edit();

								Set<String> contactSet = new HashSet<String>();
								contactSet.addAll(Unidades);
								editor.putStringSet("UNI", contactSet);
								editor.putString("EMAIL_USU", lclogin
										.getText().toString());
								editor.putString("NOME", nome);
								editor.commit();

								// it.putExtra("UNI", (Serializable) Unidades);

								startActivity(it);

							} else {

								SharedPreferences settings = getSharedPreferences(
										PREF_NAME, MODE_PRIVATE);
								SharedPreferences.Editor editor = settings
										.edit();

								editor.putString("logado", "1");
								editor.putString("USUARIO", lclogin.getText()
										.toString());
								editor.putString("EMAIL_USU", lclogin
										.getText().toString());
								editor.putString("SENHA", lcsenha.getText()
										.toString());
								editor.putString("CodigoLogin", resposta);
								editor.putString("UnidadeLogin", unidade);
								editor.putString("BlocoLogin", bloco);
								editor.putString("NOME", nome);
								editor.putString("APELIDO", nomfant_apel);
								editor.putString("ACESSO", acesso_cadastrado);
								editor.putString("TODAS", todas);
								Set<String> contactSet = new HashSet<String>();
								contactSet.addAll(Unidades);
								editor.putStringSet("UNI", contactSet);
								editor.commit();

								Intent it = new Intent(this,
										DrawerActivity.class);
								MainActivity.this.finish();
								it.putExtra("codigo", resposta);

								startActivity(it);

							}

						}

					}
				}
			} catch (Exception e1) {
				h1.sendEmptyMessage(1);
			}

		}
	}

	private String getChildTagValue(Element acesso, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {

			finish();
		}
		return false;
	}

	@SuppressLint("HandlerLeak")
	Handler h1 = new Handler() {
		public void handleMessage(Message msg) {
			mesagens(msg);
		}
	};

	private void mesagens(Message msg) {

		if (msg.what == 1) {
			Toast.makeText(this, "Erro de conexão", Toast.LENGTH_LONG).show();
		} else if (msg.what == 2) {
			Toast.makeText(this, "Login ou senha inválidos, tente novamente",
					Toast.LENGTH_LONG).show();
		} else if (msg.what == 3) {
		}
	}

	public void OutraConta(View v) {

		SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
		settings.edit().clear().commit();

		Intent it = new Intent(this, MainActivity.class);

		startActivity(it);

	}

	public void cadastrar(View v) {
		Intent it = new Intent(this, Cadastro_usuario.class);

		startActivity(it);

	}
	
	public void esquecisenha(View v) {
		Intent it = new Intent(this, Esqueceu_senha.class);

		startActivity(it);

	}

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");
            id_global = object.getString("id");


            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
                foto_global = profile_pic.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))

                bundle.putString("first_name", object.getString("first_name"));
                nome_global = object.getString("first_name");
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            email_global = object.getString("email");
             if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        }
        catch(JSONException e) {
            Log.d("","Error parsing JSON");
        }
        return null;
    }

	
}
