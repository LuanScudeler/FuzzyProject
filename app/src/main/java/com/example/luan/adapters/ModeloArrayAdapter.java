package com.example.luan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.luan.fuzzy.MyApplication;
import com.example.luan.fuzzy.R;
import com.example.luan.model.Modelo;

import java.util.ArrayList;

/**
 * Created by Luan on 21/06/2015.
 */
public class ModeloArrayAdapter extends BaseAdapter {

    ArrayList<Modelo> lista;

    public ModeloArrayAdapter(ArrayList<Modelo> lista) {
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return  lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Modelo modelo = lista.get(position);

        LayoutInflater inflater = (LayoutInflater) MyApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.listview_modelospinner,null);

        TextView nomeModelo = (TextView)v.findViewById(R.id.tvNome);
        nomeModelo.setText(modelo.getNome());

        TextView idModelo = (TextView)v.findViewById(R.id.tvIdModelo);
        idModelo.setText(modelo.getIdModelo().toString());

        return v;

    }
}
