package br.com.hidroluz.HidroluzWaterControl;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TemplateListaUnidades extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<ItemListUnidades> itens;

	public TemplateListaUnidades(Context context,
			ArrayList<ItemListUnidades> itens) {

		// Itens que preencheram o listview
		this.itens = itens;
		// responsavel por pegar o Layout do item.
		mInflater = LayoutInflater.from(context);

	}

	public int getCount() {
		// TODO Auto-generated method stub
		return itens.size();
	}

	public ItemListUnidades getItem(int position) {
		// TODO Auto-generated method stub
		return itens.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub

		ItemListUnidades item = itens.get(position);

		// infla o layout para podermos preencher os dados
		view = mInflater.inflate(R.layout.item_unidades, null);

		// atravez do layout pego pelo LayoutInflater, pegamos cada id
		// relacionado
		((TextView) view.findViewById(R.id.unid_bloco))
				.setText(item.getBloco());
		((TextView) view.findViewById(R.id.unid_unidade)).setText(item
				.getUnidade());
		((TextView) view.findViewById(R.id.unid_nome)).setText(item.getLogin());
		// ((TextView)
		// view.findViewById(R.id.unid_email)).setText(item.getEmail());
		((TextView) view.findViewById(R.id.unid_codigo)).setText(item
				.getCodigo());
		((TextView) view.findViewById(R.id.id_unidade)).setText(item
				.getId_unidade());

		if (item.getLogin() != null) {

			((TextView) view.findViewById(R.id.unid_nome))
					.setVisibility(View.VISIBLE);

			((ImageView) view.findViewById(R.id.imageView1))
					.setVisibility(View.GONE);

		}

		return view;
	}

}
