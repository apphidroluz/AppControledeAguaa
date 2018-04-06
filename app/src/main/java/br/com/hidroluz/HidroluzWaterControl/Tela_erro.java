package br.com.hidroluz.HidroluzWaterControl;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class Tela_erro extends Fragment {
	
	View rootview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootview = inflater.inflate(R.layout.activity_tela_erro, container,
				false);
		return rootview;
	}

	public void onStart() {
		super.onStart();
		
	}

	
}
