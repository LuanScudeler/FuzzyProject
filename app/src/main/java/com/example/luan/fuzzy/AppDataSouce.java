package com.example.luan.fuzzy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.luan.model.Marca;

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

    private Marca cursorToMarca(Cursor cursor) {
        Marca marca = new Marca();
        marca.setIdMarca(cursor.getLong(0));
        marca.setNome(cursor.getString(1));

        return marca;
    }

    public ArrayList<Marca> findAllMarcas() {
        ArrayList<Marca> marcas = new ArrayList<Marca>();

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
}
