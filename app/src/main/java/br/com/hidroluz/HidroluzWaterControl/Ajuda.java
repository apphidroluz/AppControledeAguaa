package br.com.hidroluz.HidroluzWaterControl;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Ajuda extends Fragment {
    View rootview;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootview = inflater.inflate(R.layout.activity_ajuda, container, false);
        return this.rootview;
    }

    public void onStart() {
        super.onStart();
    }
}
