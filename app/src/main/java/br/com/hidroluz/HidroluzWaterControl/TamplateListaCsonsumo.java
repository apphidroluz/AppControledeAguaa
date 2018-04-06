package br.com.hidroluz.HidroluzWaterControl;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class TamplateListaCsonsumo extends BaseAdapter{
	
    private LayoutInflater mInflater;
    private ArrayList<ItemListConsumo> itens;
 
    public TamplateListaCsonsumo(Context context, ArrayList<ItemListConsumo> itens)
    {
        //Itens que preencheram o listview
        this.itens = itens;
        //responsavel por pegar o Layout do item.
        mInflater = LayoutInflater.from(context);
    }
 
    /**
     * Retorna a quantidade de itens
     *
     * @return
     */
    public int getCount()
    {
        return itens.size();
    }
 
    /**
     * Retorna o item de acordo com a posicao dele na tela.
     *
     * @param position
     * @return
     */
    public ItemListConsumo getItem(int position)
    {
        return itens.get(position);
    }
 
    /**
     *
     * @param position
     * @return
     */
    public long getItemId(int position)
    {
     	Log.d("@@@@", "A");
        return position;
    }
 
    public View getView(int position, View view, ViewGroup parent)
    {
    	
     	Log.d("@@@@", "OOOOO");
    	ItemListConsumo item = itens.get(position);
        //infla o layout para podermos preencher os dados
        view = mInflater.inflate(R.layout.item_lista_consumo, null);
 
        //atravez do layout pego pelo LayoutInflater, pegamos cada id relacionado
        ((TextView) view.findViewById(R.id.localizacao)).setText(item.getLocalizacao());
        ((TextView) view.findViewById(R.id.indatual)).setText(String.valueOf(item.getIndatual()));
        ((TextView) view.findViewById(R.id.indantigo)).setText(String.valueOf(item.getIndantigo()));
        ((TextView) view.findViewById(R.id.consumo)).setText(String.valueOf(item.getConsumo()));
 
        return view;
    }
}