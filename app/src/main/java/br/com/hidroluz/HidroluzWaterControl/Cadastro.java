package br.com.hidroluz.HidroluzWaterControl;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.net.URLEncoder;
import org.json.JSONObject;



public class Cadastro extends Activity {
    private static final String PREF_NAME = "dadoslogin";
    private String apelido;
    private String bloco;
    private String codigo;
    private String lcCodigo;
    private ProgressDialog pdia;
    StringBuilder sb;
    private TextView txtApelido;
    private TextView txtBloco;
    private TextView txtUnidade;
    private String unidade;
    private String usuario;

    class CadastrarUsuario extends AsyncTask<Void, Void, String> {

        SweetAlertDialog pDialog = new SweetAlertDialog(Cadastro.this,
                SweetAlertDialog.PROGRESS_TYPE);

        CadastrarUsuario() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setTitleText("Aguarde");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            Toast.makeText(Cadastro.this.getApplicationContext(), s, 1).show();
        }

        protected String doInBackground(Void... params) {
            String result = "";
            try {
                return new JSONObject(new HttpRemote().getPost("http://android.hidroluz.com.br/php/listar_clientes.php", Cadastro.this.sb.toString())).getString("resultado");
            } catch (Exception e) {
                return e.getMessage();
            }
        }
    }

    public Cadastro() {
        this.lcCodigo = "";
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        this.lcCodigo = getIntent().getStringExtra("codigo");
        SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
        restoreActionBar();
        this.codigo = settings.getString("CodigoLogin", "");
        this.unidade = settings.getString("UnidadeLogin", "");
        this.bloco = settings.getString("BlocoLogin", "");
        this.apelido = settings.getString("APELIDO", "");
        this.usuario = settings.getString("USUARIO", "");
        this.txtApelido = (TextView) findViewById(R.id.apelido2);
        this.txtBloco = (TextView) findViewById(R.id.bloco2);
        this.txtUnidade = (TextView) findViewById(R.id.unidade2);
        this.txtApelido.setText(this.apelido);
        this.txtBloco.setText(this.bloco);
        this.txtUnidade.setText(this.unidade);
        if (VERSION.SDK_INT > 9) {
            StrictMode.setThreadPolicy(new Builder().permitAll().build());
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        getActionBar().setCustomView(R.layout.action_bar_layout);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setIcon(R.drawable.sf);

    }

    public void cadastrar(View v) throws InterruptedException {
        EditText edtNome = (EditText) findViewById(R.id.txtnome);
        EditText edtEmail = (EditText) findViewById(R.id.txtemail);
        EditText edtTelefone = (EditText) findViewById(R.id.txttelefone);
        if (!validaCampo(edtNome) || !validaCampo(edtEmail) || !validaCampo(edtTelefone) || !StringUtils.filter_nome(edtNome) || !StringUtils.filter_email(edtEmail) ) {
            return;
        }
        if (new VerificarConexao().isConnect(getApplicationContext())) {
            try {
                this.sb = new StringBuilder();
                this.sb.append("id_unidade=");
                this.sb.append(URLEncoder.encode(this.usuario, "UTF-8"));
                this.sb.append("&nome=");
                this.sb.append(URLEncoder.encode(edtNome.getText().toString(), "UTF-8"));
                this.sb.append("&email=");
                this.sb.append(URLEncoder.encode(edtEmail.getText().toString(), "UTF-8"));
                this.sb.append("&telefone=");
                this.sb.append(URLEncoder.encode(edtTelefone.getText().toString(), "UTF-8"));
                new CadastrarUsuario().execute(new Void[0]);
                Intent it = new Intent(this, DrawerActivity.class);
                finish();
                startActivity(it);
                return;
            } catch (Exception e) {
                return;
            }
        }
        Toast.makeText(getApplicationContext(), "SEM INTERNET", Toast.LENGTH_LONG).show();
    }

    public boolean validaCampo(EditText edt) {
        if (!StringUtils.isNullOrEmpty(edt.getText().toString())) {
            return true;
        }
        edt.setError("O campo é Obrigátorio");
        return false;
    }
}
