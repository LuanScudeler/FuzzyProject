package com.example.luan.fuzzy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.AvoidXfermode;
import android.util.Log;

import com.example.luan.model.Marca;
import com.example.luan.model.Modelo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Luan on 19/06/2015.
 */
public class AppDataSouce {

    // Database fields
    private SQLiteDatabase database;
    private DataBaseHelper dbHelper;

    public AppDataSouce(Context context) {
        dbHelper = new DataBaseHelper(context);
    }

    public void createDB() throws IOException{
        dbHelper.createDataBase();
    }

    public void open() throws SQLException {
       // database = dbHelper.openDataBase();
        database = dbHelper.getReadableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public List<Marca> findAllByMarcaId (Long id){
        List<Marca> marcas = new ArrayList<Marca>();

        Log.d("ID: ", id.toString());

        Cursor cursor = database.
                rawQuery("SELECT * FROM marca WHERE _id = ?", new String[] { id.toString() });

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Marca marca = cursorToMarca(cursor);
            marcas.add(marca);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        return marcas;
    }

    public ArrayList<Marca> findAllMarcas() {
        ArrayList<Marca> marcas = new ArrayList<Marca>();

        //Default Value for spinner
        Marca marcaDefaultValue = new Marca();
        marcaDefaultValue.setNome("Selecione uma marca");
        marcaDefaultValue.setIdMarca((long) 0);
        marcas.add(marcaDefaultValue);

        Cursor cursor = database.
                rawQuery("SELECT * FROM marca", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Marca marca = cursorToMarca(cursor);
            marcas.add(marca);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        return marcas;
    }

    private Marca cursorToMarca(Cursor cursor) {
        Marca marca = new Marca();
        marca.setIdMarca(cursor.getLong(0));
        marca.setNome(cursor.getString(1));

        return marca;
    }

    public ArrayList<Modelo> findModelosByMarcaId(Long marcaId) {
        ArrayList<Modelo> modelos = new ArrayList<Modelo>();

        //Default Value for spinner
        Modelo modeloDefaultValue = new Modelo();
        modeloDefaultValue.setNome("Selecione um modelo");
        modeloDefaultValue.setIdMarca((long) 0);
        modeloDefaultValue.setIdModelo((long) 0);
        modelos.add(modeloDefaultValue);

        Cursor cursor = database.
                rawQuery("SELECT * FROM modelo WHERE marca = ?", new String[]{marcaId.toString()} );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Modelo modelo = cursorToModelo(cursor);
            Log.d("Modelos: ", "idModelo: " + modelo.getIdModelo().toString() + " - Nome: " + modelo.getNome() + " - idMarca: " + modelo.getIdMarca().toString());
            modelos.add(modelo);
            cursor.moveToNext();
        }
        cursor.close();

        return modelos;
    }

    private Modelo cursorToModelo(Cursor cursor) {
        Modelo modelo = new Modelo();
        modelo.setIdModelo(cursor.getLong(0));
        modelo.setNome(cursor.getString(1));
        modelo.setIdMarca(cursor.getLong(2));

        return modelo;
    }

    public ArrayList<String> findAllAnosByTipoCombustivel(String tipoCombustivel, Long idModelo) {

        ArrayList<String> anos = new ArrayList<String>();

        //Default Value for spinner
        anos.add("Selecione um ano");

        Cursor cursor = database.
                rawQuery("SELECT ano from ano_modelo WHERE combustivel = ? and modelo = ?", new String[]{tipoCombustivel, idModelo.toString()} );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String ano = cursorToAno(cursor);
            Log.d("Ano - ", ano.toString());
            anos.add(ano);
            cursor.moveToNext();
        }
        cursor.close();

        return anos;
    }

    private String cursorToAno(Cursor cursor) {
        String ano = null;

        ano = cursor.getString(0);

        return ano;
    }

    public Double findPrice(String anoSelected, Long idModelo) {

        Double result = null;

        Cursor cursor = database.
                rawQuery("SELECT valor FROM ano_modelo WHERE ano = ? AND modelo = ?", new String[]{anoSelected, idModelo.toString()} );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result = cursorToPrice(cursor);
            Log.d("Price - ", result.toString());
            cursor.moveToNext();
        }
        cursor.close();

        return result;
    }

    private Double cursorToPrice(Cursor cursor) {
        Log.d("Cursor Method", " - Entrou");
        Double price = null;

        price = cursor.getDouble(0);
        Log.d("Cursor: Price: ", price.toString());
        return price;
    }
}
