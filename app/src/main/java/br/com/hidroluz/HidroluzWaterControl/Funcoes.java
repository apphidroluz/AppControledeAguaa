package br.com.hidroluz.HidroluzWaterControl;

import android.content.Context;
import android.net.ConnectivityManager;

public class Funcoes {
    public boolean netWorkdisponibilidade(Context cont) {
        ConnectivityManager conmag = (ConnectivityManager) cont.getSystemService("connectivity");
        conmag.getActiveNetworkInfo();
        if (conmag.getNetworkInfo(1).isConnected()) {
            return true;
        }
        if (conmag.getNetworkInfo(0).isConnected()) {
            return true;
        }
        return false;
    }
}
