package br.com.hidroluz.HidroluzWaterControl;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Template_Todas_as_leituras extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<ItemListTodas> itens;

	public Template_Todas_as_leituras(Context context,
			ArrayList<ItemListTodas> itens) {

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
	public ItemListTodas getItem(int position)

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


		ItemListTodas item = itens.get(position);
		// infla o layout para podermos preencher os dados
		view = mInflater.inflate(R.layout.item_todas_as_leituras, null);

		String consumo = item.getConsumo() + "m³";
		// String consumo = item.getConsumo() + "" ;
		((TextView) view.findViewById(R.id.consumoTotal)).setText(consumo);
		
		String data = item.getData().substring(8, 10) + "/"
				+ item.getData().substring(5, 7) + "/"
				+ item.getData().substring(2, 4);
		((TextView) view.findViewById(R.id.Dt_es)).setText(data);

		String dias = item.getDias();
		((TextView) view.findViewById(R.id.Dias)).setText(dias);

		Double media = Double.valueOf(item.getConsumo())
				/ Double.valueOf(item.getDias());

		String diario = String.format("%.2f", media) + "m³";

		((TextView) view.findViewById(R.id.Media)).setText(diario);



		return view;

	}
}
