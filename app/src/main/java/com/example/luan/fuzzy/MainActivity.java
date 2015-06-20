package com.example.luan.fuzzy;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.luan.adapters.MarcaArrayAdapter;
import com.example.luan.model.Marca;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.optimization.Parameter;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends ActionBarActivity {
  //  private static Context context;
    protected ArrayList<Marca> marcas;
    private AppDataSouce dataSouce;
    private Spinner sMarca, sModelo;
    private Long idMarca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sModelo = (Spinner)findViewById(R.id.sModelo);

        dataSouce =  new AppDataSouce(this);

        //CREATE DATABASE
        try {
            dataSouce.createDB();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //OPEN DATABASE
        try {
            dataSouce.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Hardcoded ID for tests
        Long id = 1L;

        addItemsOnsMarca();

        sMarca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idMarca = marcas.get(position).getIdMarca();
                Log.d("Id SELECTED: ", "" + idMarca);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    /*TOOK OFF TEMPORARY FOR TESTTING THE DATABASE***********************************
        boolean first = true;
        InputStream in = null;
        ArrayList parameterList = new ArrayList();

        try {
            in = MyApplication.getAppContext().getAssets().open("tipper.fcl");
        } catch (IOException e) {
            e.printStackTrace();
        };

        // Load from 'FCL' file
        String fileName = "tipper.fcl";
        FIS fis = FIS.load(in, true);

        // Set inputs
        fis.setVariable("service", 3);
        fis.setVariable("food", 7);

        // Show each rule (and degree of support)
        for( Rule r : fis.getFunctionBlock("tipper").getFuzzyRuleBlock("No1").getRules() ) {
            if (first)
                r.setWeight(1.5);


            System.out.println(r);
            first = false;
        }

        // Error while loading?
        if
        ( fis == null ) {
            System.err.println("Can't load file: '" + fileName + "'");
            return;
        }

        // Evaluate
        fis.evaluate();

        // Show output variable's chart
        Variable tip = fis.getFunctionBlock("tipper").getVariable("tip");

        // Print ruleSet
        // System.out.println(fis);
        Log.d("Saida: ", "" + tip.defuzzify());

        // Show output variable
        System.out.println("Output value:" + fis.getVariable("tip").getValue());*/
    }

    private void addItemsOnsMarca() {
        sMarca = (Spinner)findViewById(R.id.sMarca);

         marcas = dataSouce.findAllMarcas();

        if(marcas != null){
            Log.d("Status: ", "SUCCESS!!!");
        }
        for (int i = 0 ; i < marcas.size() ; i++)
            Log.d("Marcas: ", marcas.get(i).getNome().toString() + "Id: " + marcas.get(i).getIdMarca().toString());

        MarcaArrayAdapter adapter = new MarcaArrayAdapter(marcas);
        sMarca.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
