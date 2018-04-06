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

public class TemplateListaMenu extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<ItemListMenu> itens;

	public TemplateListaMenu(Context context, ArrayList<ItemListMenu> itens) {

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
	public ItemListMenu getItem(int position)

	{

		return itens.get(position);
	}

	/**
	 * @param position
	 * @return
	 */
	public long getItemId(int position) {

		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {

		// Pega o item de acordo com a pos��o.
		ItemListMenu item = itens.get(position);
		// infla o layout para podermos preencher os dados
		view = mInflater.inflate(R.layout.item_lista_menu, null);

		// atravez do layout pego pelo LayoutInflater, pegamos cada id
		// relacionado
		
		String[] nomes = new String[]{"Leituras", "Valores", "Consumo", "Media", "Noticias"};

		Integer[] icones = new Integer[] { R.drawable.home, R.drawable.graphic,
				R.drawable.graphic, R.drawable.graphic, R.drawable.multimedia,};

		

		((TextView) view.findViewById(R.id.name)).setText(String.valueOf(nomes));

		
		//((ImageView) view.findViewById(R.id.icon)).setText(Integer.valueOf(icones));

		

		return view;
	}

}