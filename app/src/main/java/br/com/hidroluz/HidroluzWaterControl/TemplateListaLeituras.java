package br.com.hidroluz.HidroluzWaterControl;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.DownloadManager.Request;
import android.content.ClipData.Item;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TemplateListaLeituras extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<ItemListLeituras> itens;

	public TemplateListaLeituras(Context context,
			ArrayList<ItemListLeituras> itens) {

		// Itens que preencheram o listview
		this.itens = itens;
		// responsavel por pegar o Layout do item.
		mInflater = LayoutInflater.from(context);

	}

	/**
	 * Retorna a quantidade de itens
	 * 
	 * @return
	 */
	public int getCount() {

		return itens.size();
	}

	/**
	 * Retorna o item de acordo com a posicao dele na tela.
	 * 
	 * @param position
	 * @return
	 */
	public ItemListLeituras getItem(int position)

	{

		return itens.get(position);
	}

	/**
	 * 
	 * @param position
	 * @return
	 */
	public long getItemId(int position) {

		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {

		// Pega o item de acordo com a pos��o.
		ItemListLeituras item = itens.get(position);
		// infla o layout para podermos preencher os dados
		view = mInflater.inflate(R.layout.item_lista_leitura, null);

		// atravez do layout pego pelo LayoutInflater, pegamos cada id
		// relacionado

		String consumo = item.getConsumo() + "m³";
		// String consumo = item.getConsumo() + "" ;
		((TextView) view.findViewById(R.id.consumoTotal)).setText(consumo);

		String dias = item.getDias();
		((TextView) view.findViewById(R.id.Dias)).setText(dias);

		Double media = Double.valueOf(item.getConsumo())
				/ Double.valueOf(item.getDias());

		String diario = String.format("%.2f", media) + "m³";

		((TextView) view.findViewById(R.id.Media)).setText(diario);

		String data = item.getData().substring(8, 10) + "/"
				+ item.getData().substring(5, 7) + "/"
				+ item.getData().substring(2, 4);
		((TextView) view.findViewById(R.id.Dt_es)).setText(data);

		((TextView) view.findViewById(R.id.Dt_anterior)).setText(item
				.getData_anterior());

		((TextView) view.findViewById(R.id.mesano2)).setText(item.getMesano());

		((TextView) view.findViewById(R.id.fluxo_continuo)).setText(item
				.getFluxo_continuo());

		if (item.getFluxo_continuo().equalsIgnoreCase("nao")) {
			((ImageView) view.findViewById(R.id.imageView2))
					.setVisibility(View.GONE);
		} else {
			((ImageView) view.findViewById(R.id.imageView1))
					.setVisibility(View.GONE);

		}

		return view;
	}

}
