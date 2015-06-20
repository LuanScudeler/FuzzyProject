package com.example.luan.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.luan.fuzzy.MainActivity;
import com.example.luan.fuzzy.MyApplication;
import com.example.luan.fuzzy.R;
import com.example.luan.model.Marca;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luan on 20/06/2015.
 */
public class MarcaArrayAdapter  extends BaseAdapter{

    private ArrayList<Marca> lista;

    public MarcaArrayAdapter(ArrayList<Marca> lista) {
        this.lista = lista;
    }



    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Marca getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Marca marca = lista.get(position);

        LayoutInflater inflater = (LayoutInflater)MyApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.listview_marcaspinner,null);

        TextView titulo = (TextView)v.findViewById(R.id.tvNome);
        titulo.setText(marca.getNome());

        TextView username = (TextView)v.findViewById(R.id.tvId);
        username.setText(marca.getIdMarca().toString());

        return v;
    }
}
