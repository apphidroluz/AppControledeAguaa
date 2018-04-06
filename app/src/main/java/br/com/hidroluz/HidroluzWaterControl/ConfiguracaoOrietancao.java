package br.com.hidroluz.HidroluzWaterControl;

import android.app.Activity;

public class ConfiguracaoOrietancao {
    public static void setarRetrato(Activity activity) {
        activity.setRequestedOrientation(1);
    }

    public static void setarPaisagem(Activity activity) {
        activity.setRequestedOrientation(0);
    }
}
