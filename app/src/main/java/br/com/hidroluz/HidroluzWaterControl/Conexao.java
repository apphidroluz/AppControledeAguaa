package br.com.hidroluz.HidroluzWaterControl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Conexao {
    public InputStream OpenHttpConnection(String urlString) throws IOException {
        InputStream in = null;
        URLConnection conn = new URL(urlString).openConnection();
        conn.setConnectTimeout(15000);
        if (conn instanceof HttpURLConnection) {
            try {
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                if (httpConn.getResponseCode() == 200) {
                    in = httpConn.getInputStream();
                }
                return in;
            } catch (Exception e) {
                throw new IOException("Erro de conexão2016");
            }
        }
        throw new IOException("Não conectado");
    }
}