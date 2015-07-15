package com.example.luan.fuzzy;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.luan.adapters.MarcaArrayAdapter;
import com.example.luan.adapters.ModeloArrayAdapter;
import com.example.luan.model.Marca;
import com.example.luan.model.Modelo;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
  //  private static Context context;
    protected ArrayList<Marca> marcas;
    protected ArrayList<Modelo> modelos;
    protected ArrayList<String> anos;
    private AppDataSouce dataSouce;
    private Long idMarca;
    private Long idModelo;
    private Double fuzzyResult;
    private Double finalResult;
    private Double priceResult;

    private double pcFunilariaPintura = 0;
    private double pcMotor = 0;
    private double pcMecanicaInterior = 0;

    private SeekBar sbFunilariaPintura, sbMotor, sbMecanicaInterior;
    private Spinner sMarca, sModelo, sTipoCombustivel, sAno;
    private Button btnCalcular;
    private TextView tvPreco, tvFuzzyValue, tvFPvalue, tvMvalue, tvMIvalue, tvResult;

    private static double MAX_PERCENT = 0.2;
    private static double MIN_PERCENT = 0.25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sModelo = (Spinner)findViewById(R.id.sModelo);
        sTipoCombustivel = (Spinner)findViewById(R.id.sTipoCombustivel);
        sMarca = (Spinner)findViewById(R.id.sMarca);
        sAno = (Spinner)findViewById(R.id.sAno);

        sbFunilariaPintura = (SeekBar)findViewById(R.id.sbFunilariaPintura);
        sbMotor = (SeekBar)findViewById(R.id.sbMotor);
        sbMecanicaInterior = (SeekBar)findViewById(R.id.sbMecanicaInterior);

        btnCalcular = (Button)findViewById(R.id.btnCalcular);

        tvPreco = (TextView)findViewById(R.id.tvPreco);
        tvFuzzyValue = (TextView)findViewById(R.id.tvFuzzyValue);
        tvFPvalue = (TextView)findViewById(R.id.tvFPvalue);
        tvMvalue = (TextView)findViewById(R.id.tvMvalue);
        tvMIvalue = (TextView)findViewById(R.id.tvMIvalue);
        tvResult = (TextView)findViewById(R.id.tvResult);

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

        addItemsOnsMarca();

        sMarca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idMarca = marcas.get(position).getIdMarca();
                Log.d("idMarca SELECTED: ", "" + idMarca);

                addItemsOnModelo(idMarca);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Setting default value for marca spinner
                //  sMarca.setSelection(0);
            }
        });

        sModelo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Search for the year related to de marca and modelo chosen
                idModelo = modelos.get(position).getIdModelo();
                Log.d("idModelo SELECTED: ", "" + idModelo);

                addItemsOnTipoCombustivel(idModelo);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Setting default value for modelo spinner
               // sModelo.setSelection(0);
            }
        });


        sTipoCombustivel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tipoSelected = sTipoCombustivel.getSelectedItem().toString();
                if(position == 4){
                    tipoSelected = "Gasolina";
                }

                Log.d("TipoFuel - ", tipoSelected);

                addItemsOnAno(tipoSelected, idModelo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sAno.post(new Runnable() {
            public void run() {
                sAno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("Selected", " - SIM");
                        String anoSelected = sAno.getSelectedItem().toString();
                        priceResult = findPriceVeiculo(anoSelected, idModelo);
                        if (priceResult != null)
                            tvPreco.setText(String.format("R$ %.2f", priceResult ));


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        sbFunilariaPintura.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pcFunilariaPintura = progress;
                pcFunilariaPintura /= 100;

                if(progress >= 0 && progress <= 20)
                    tvFPvalue.setText("Muito Ruim  - " + String.valueOf(pcFunilariaPintura));
                if(progress >= 21 && progress <= 40)
                    tvFPvalue.setText("Ruim  - " + String.valueOf(pcFunilariaPintura));
                if(progress >= 41 && progress <= 60)
                    tvFPvalue.setText("Bom  - " + String.valueOf(pcFunilariaPintura));
                if(progress >= 61 && progress <= 80)
                    tvFPvalue.setText("Muito Bom  - " + String.valueOf(pcFunilariaPintura));
                if(progress >= 81 && progress <= 100)
                    tvFPvalue.setText("Excelente  - " + String.valueOf(pcFunilariaPintura));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fuzzyProcess();
            }
        });

        sbMotor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pcMotor = progress;
                pcMotor/=100;

                if(progress >= 0 && progress <= 20)
                    tvMvalue.setText("Muito Ruim  - " + String.valueOf(pcMotor));
                if(progress >= 21 && progress <= 40)
                    tvMvalue.setText("Ruim  - " + String.valueOf(pcMotor));
                if(progress >= 41 && progress <= 60)
                    tvMvalue.setText("Bom  - " + String.valueOf(pcMotor));
                if(progress >= 61 && progress <= 80)
                    tvMvalue.setText("Muito Bom  - " + String.valueOf(pcMotor));
                if(progress >= 81 && progress <= 100)
                    tvMvalue.setText("Excelente  - " + String.valueOf(pcMotor));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fuzzyProcess();
            }
        });

        sbMecanicaInterior.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                pcMecanicaInterior = progress;
                pcMecanicaInterior/=100;

                if(progress >= 0 && progress <= 20)
                    tvMIvalue.setText("Muito Ruim  - " + String.valueOf(pcMecanicaInterior));
                if(progress >= 21 && progress <= 40)
                    tvMIvalue.setText("Ruim  - " + String.valueOf(pcMecanicaInterior));
                if(progress >= 41 && progress <= 60)
                    tvMIvalue.setText("Bom  - " + String.valueOf(pcMecanicaInterior));
                if(progress >= 61 && progress <= 80)
                    tvMIvalue.setText("Muito Bom  - " + String.valueOf(pcMecanicaInterior));
                if(progress >= 81 && progress <= 100)
                    tvMIvalue.setText("Excelente  - " + String.valueOf(pcMecanicaInterior));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fuzzyProcess();
                //tvMIvalue.setText(String.valueOf(pcMecanicaInterior));
            }
        });


    /*TOOK OFF TEMPORARY FOR TESTTING THE DATABASE************************************/

    }

    public void fuzzyProcess(){

        boolean first = true;
        InputStream in = null;
        ArrayList parameterList = new ArrayList();

        try {
            in = MyApplication.getAppContext().getAssets().open("fuzzy.fcl");
        } catch (IOException e) {
            e.printStackTrace();
        };

        // Load from 'FCL' file
        String fileName = "fuzzy.fcl";
        FIS fis = FIS.load(in, true);

        // Set inputs
        fis.setVariable("FunilariaPintura", pcFunilariaPintura);
        fis.setVariable("MecanicaInterior", pcMecanicaInterior);
        fis.setVariable("Motor", pcMotor);

        // Show each rule (and degree of support)
        for( Rule r : fis.getFunctionBlock("fuzzyQuality").getFuzzyRuleBlock("No1").getRules() ) {
            if (first)
                r.setWeight(1.5);

            //System.out.println(r);
            first = false;
        }

        // Error while loading?
        if( fis == null ) {
            System.err.println("Can't load file: '" + fileName + "'");
            return;
        }

        // Evaluate
        fis.evaluate();

        // Show output variable's chart
        Variable qualidade = fis.getFunctionBlock("fuzzyQuality").getVariable("Qualidade");

        // Print ruleSet
        // System.out.println(fis);
        Log.d("Saida: ", "" + qualidade.defuzzify());
        fuzzyResult = qualidade.defuzzify();

        Double minValue = priceResult * MIN_PERCENT;
        finalResult = ((priceResult-minValue)*fuzzyResult)+minValue;

        // Show output variable
        System.out.println("Output value:" + fis.getVariable("Qualidade").getValue());
        tvFuzzyValue.setText(String.format("%.2f", fuzzyResult ));
        tvResult.setText(String.format("R$ %.2f", finalResult));


        /********FUZZY LOGIC********/
    }

    public void btnCalcularClick (View v){
        tvPreco.setText(fuzzyResult.toString());
        //gerarRuleBlock();
    }

    public void gerarRuleBlock(){
        String[] qualidade = {"MuitoRuim", "Ruim", "Bom", "MuitBom", "Excelente"};
        int cont = 1;
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                for(int k = 0; k < 5; k++){
                    for(int l = 0; l < 5; l++){
                        System.out.println(" RULE "+ String.valueOf(cont++) +" : IF FunilariaPintura IS " + qualidade[i]
                                +" AND MecanicaInterior IS " + qualidade[j]
                                + " AND Motor " + qualidade[k] +" THEN Qualidade IS " + qualidade[l]);
                       /* Log.d("Out", " RULE "+ String.valueOf(cont++) +" : IF FunilariaPintura IS " + qualidade[i]
                                            +" AND MecanicaInterior IS " + qualidade[j]
                                            + " AND Motor " + qualidade[k] +" THEN Qualidade IS " + qualidade[l]);*/
                    }
                }
            }
        }
    }

    private Double findPriceVeiculo(String anoSelected, Long idModelo) {
        Log.d("AnoSelected:", anoSelected + " - IdModelo:" + idModelo.toString());

        Double price = dataSouce.findPrice(anoSelected, idModelo);
        return price;
    }

    private void addItemsOnAno(String tipoSelected, Long idModelo) {

        Log.d("TipoCombSelected:", tipoSelected + " - IdModelo:" + idModelo.toString());
        anos = dataSouce.findAllAnosByTipoCombustivel(tipoSelected, idModelo);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, anos);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sAno.setAdapter(dataAdapter);
    }

    private void addItemsOnTipoCombustivel(long idModelo) {

        //TODO: Create ArrayAdapter for this spinner and anoSpinner too, to equalize height of all spinner's items
        ArrayList<String> list = dataSouce.findTipoCombustivel(idModelo);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sTipoCombustivel.setAdapter(dataAdapter);
    }

    private void addItemsOnsMarca() {

        marcas = dataSouce.findAllMarcas();

        /*for (int i = 0 ; i < marcas.size() ; i++)
            Log.d("Marcas: ", marcas.get(i).getNome().toString() + "Id: " + marcas.get(i).getIdMarca().toString());*/

        MarcaArrayAdapter adapter = new MarcaArrayAdapter(marcas);

        sMarca.setAdapter(adapter);
    }

    private void addItemsOnModelo(Long marcaId){
        sModelo = (Spinner)findViewById(R.id.sModelo);

        modelos = dataSouce.findModelosByMarcaId(marcaId);

        /*for (int i = 0 ; i < modelos.size() ; i++)
            Log.d("Modelos: ", modelos.get(i).getNome().toString() + "Id: " + modelos.get(i).getIdModelo().toString());*/

        ModeloArrayAdapter adapter = new ModeloArrayAdapter(modelos);

        sModelo.setAdapter(adapter);
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
