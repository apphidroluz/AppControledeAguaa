package br.com.hidroluz.HidroluzWaterControl;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

class DrawerListAdapter extends BaseAdapter {
    private List<NavItem> itens;
    private LayoutInflater mInflater;

    public DrawerListAdapter(Context context, List<NavItem> NavItems) {
        this.itens = NavItems;
        this.mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return this.itens.size();
    }

    public NavItem getItem(int position) {
        return (NavItem) this.itens.get(position);
    }

    public long getItemId(int position) {
        Log.d("@@@@", "A");
        return (long) position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        NavItem item = (NavItem) this.itens.get(position);
        view = this.mInflater.inflate(R.layout.drawer_item, null);
        ((TextView) view.findViewById(R.id.title3)).setText(item.getmTitle());
        ((ImageView) view.findViewById(R.id.icones)).setImageResource(item.getmIcon());
        return view;
    }
}
