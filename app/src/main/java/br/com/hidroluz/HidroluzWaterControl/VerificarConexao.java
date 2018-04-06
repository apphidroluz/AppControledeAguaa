package br.com.hidroluz.HidroluzWaterControl;

import android.content.Context;
import android.net.ConnectivityManager;

public class VerificarConexao {
    public boolean isConnect(Context ctx) {
        try {
            ConnectivityManager cm;
            cm = (ConnectivityManager) ctx.getSystemService("connectivity");
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
