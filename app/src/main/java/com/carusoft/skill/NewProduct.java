package com.carusoft.skill;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.RenderMode;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.carusoft.skill.authentication.SignInActivity;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import net.orandja.shadowlayout.ShadowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewProduct extends AppCompatActivity {
    int check = 0;

    Integer validationType = 0;
    private SearchableSpinner categoria;
    private SearchableSpinner marcas;
    private SearchableSpinner unidad;
    private SearchableSpinner moneda;
    private SearchableSpinner presentacion;
    private AppCompatEditText otraMarca;

    private AppCompatEditText gasto;
    private AppCompatEditText peso;
    private AppCompatEditText cantidad;
    private AppCompatEditText barcode;


    private String codCat;
    private String codPresentacion;
    private String codMarca;
    private String codMedida;
    private HashMap<String, Object> compraData;

    ArrayList<HashMap<String, Object>> compras;
    private String categoriaSt;
    private String marcaSt;
    private String presentacionSt;
    private String pesoSt;
    private String medidaSt;

    private Integer editando = 0;
    private Integer prodIndex;
    private LottieAnimationView loader;
    private RelativeLayout overlay;
    private Integer monedaSt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");

        TextView titulo = (TextView) findViewById(R.id.titulo);
        TextView titulo2 = (TextView) findViewById(R.id.titulo2);


        ShadowLayout codeTxt = (ShadowLayout) findViewById(R.id.scannerLayout);
        codeTxt.setVisibility(View.GONE);
        TextView legend = (TextView) findViewById(R.id.legend);
        legend.setVisibility(View.GONE);
        Button escanear = (Button) findViewById(R.id.escanear);
        escanear.setVisibility(View.GONE);


        barcode = findViewById(R.id.barcode);


        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            // doSomeOperations();
                            Log.d("GOT_BACK", data.getStringExtra("barcode"));
                            barcode.setText(data.getStringExtra("barcode"));
                        }
                    }
                });


        escanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewProduct.this, ScannerActivity.class);
                //startActivityForResult(intent,  2,null); // suppose requestCode == 2
                someActivityResultLauncher.launch(intent);

                // startActivity(intent);
            }
        });


        compras = (ArrayList<HashMap<String, Object>>) args.getSerializable("compras");

        String compra = args.getString("compra");
        compraData = new Gson().fromJson(compra, new TypeToken<HashMap<String, Object>>() {
        }.getType());

        TextView fecha = (TextView) findViewById(R.id.fecha);
        fecha.setText(compraData.get("fecha").toString());

        TextView dia = (TextView) findViewById(R.id.dia);
        if (compraData.get("day").toString().equals("1")) {
            dia.setText("Lunes");
        }
        if (compraData.get("day").toString().equals("2")) {
            dia.setText("Martes");
        }
        if (compraData.get("day").toString().equals("3")) {
            dia.setText("Miercoles");
        }
        if (compraData.get("day").toString().equals("4")) {
            dia.setText("Jueves");
        }
        if (compraData.get("day").toString().equals("5")) {
            dia.setText("Viernes");
        }
        if (compraData.get("day").toString().equals("6")) {
            dia.setText("Sabado");
        }
        if (compraData.get("day").toString().equals("7")) {
            dia.setText("Domingo");
        }

        TextView semana = (TextView) findViewById(R.id.semana);
        semana.setText(compraData.get("week").toString());

        TextView tipoNegocio = (TextView) findViewById(R.id.tipoNegocio);
        tipoNegocio.setText(compraData.get("tipoNegocio").toString());

        TextView nombre = (TextView) findViewById(R.id.nombre);
        nombre.setText(compraData.get("nombre").toString());

        TextView lugar = (TextView) findViewById(R.id.lugar);
        lugar.setText(compraData.get("lugar").toString());

        categoria = (SearchableSpinner) findViewById(R.id.categoria);
        marcas = (SearchableSpinner) findViewById(R.id.marcas);
        unidad = (SearchableSpinner) findViewById(R.id.unidad);
        moneda = (SearchableSpinner) findViewById(R.id.moneda);

        presentacion = (SearchableSpinner) findViewById(R.id.presentacion);
        otraMarca = (AppCompatEditText) findViewById(R.id.otraMarca);

        gasto = (AppCompatEditText) findViewById(R.id.gasto);
        peso = (AppCompatEditText) findViewById(R.id.peso);
        cantidad = (AppCompatEditText) findViewById(R.id.cantidad);


        if (args.getInt("editando") == 1) {
            editando = 1;
            Log.d("EDITANDO", "EDITANDO");

            prodIndex = args.getInt("prodIndex");
            titulo.setText("Editar Producto");
            titulo2.setText("Editar Producto");

            gasto.setText(compraData.get("gasto").toString());

            otraMarca.setText(compraData.get("otraMarca").toString());

            cantidad.setText(compraData.get("cantidad").toString());

            peso.setText(compraData.get("peso").toString());

            barcode.setText(compraData.get("barcode").toString());

            codeTxt.setVisibility(View.VISIBLE);
            legend.setVisibility(View.VISIBLE);
            escanear.setVisibility(View.VISIBLE);
        }

        getCategorias();
        setupMoneda();
        Button nuevoProd = (Button) findViewById(R.id.nuevoProd);
        nuevoProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barcode.getText().toString().isEmpty()) {
                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(NewProduct.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setTitle("Atención")
                            .setMessage("Tiene código de barra el producto?")
                            .addButton("Si, escanear", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                dialog.dismiss();
                                ShadowLayout codeTxt = (ShadowLayout) findViewById(R.id.scannerLayout);
                                codeTxt.setVisibility(View.VISIBLE);
                                TextView legend = (TextView) findViewById(R.id.legend);
                                legend.setVisibility(View.VISIBLE);
                                Button escanear = (Button) findViewById(R.id.escanear);
                                escanear.setVisibility(View.VISIBLE);

                            }).addButton("No posee", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                saveProd(1);
                                dialog.dismiss();
                            });
                    builder.show();
                } else {
                    saveProd(1);
                }

            }
        });

        Button finCompra = (Button) findViewById(R.id.finCompra);
        finCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barcode.getText().toString().isEmpty()) {
                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(NewProduct.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setTitle("Atención")
                            .setMessage("Tiene código de barra el producto?")
                            .addButton("Si, escanear", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                dialog.dismiss();
                                ShadowLayout codeTxt = (ShadowLayout) findViewById(R.id.scannerLayout);
                                codeTxt.setVisibility(View.VISIBLE);
                                TextView legend = (TextView) findViewById(R.id.legend);
                                legend.setVisibility(View.VISIBLE);
                                Button escanear = (Button) findViewById(R.id.escanear);
                                escanear.setVisibility(View.VISIBLE);

                            }).addButton("No posee", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                saveProd(2);
                                dialog.dismiss();
                            });
                    builder.show();
                } else {
                    saveProd(2);
                }
            }
        });

        ImageView logout = (ImageView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(NewProduct.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .setTitle("Atención")
                        .setMessage("Esta seguro que desea salir?")
                        .addButton("Si, salir", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                            logout();
                        }).addButton("No, cancelar", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                            dialog.dismiss();
                        });
                builder.show();
            }
        });

        Button editarCompra = (Button) findViewById(R.id.editarCompra);
        editarCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(NewProduct.this, NewPurchase.class);
                Bundle args = new Bundle();
                args.putSerializable("compras", (Serializable) compras);
                args.putString("compra", new Gson().toJson(compraData));
                args.putString("editando", "1");
                intent.putExtra("BUNDLE", args);
                startActivity(intent);


            }
        });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private void saveProd(Integer action) {
        HashMap<String, Object> dato = new HashMap<>();
        Boolean passed = false;

        if (validationType == 0) {
            Log.d("CANTIDAD", "CANT");
            Log.d("CANTIDAD", cantidad.getText().toString());
            if (cantidad != null) {
                Log.d("CANTIDAD", "NOT NULL");
            }
            if ((codCat != null) && (codPresentacion != null) && (codMarca != null) && !(isEmpty(cantidad))) {
                Log.d("VERIF_TYPE", "0");
                passed = true;
            }
        }
        if (validationType == 1) {
            if ((codCat != null) && (codPresentacion != null) && (codMarca != null) && (codMedida != null) && !(isEmpty(peso)) && !(isEmpty(cantidad))) {
                Log.d("VERIF_TYPE", "1");
                passed = true;
            }
        }
        if (validationType == 2) {
            if ((codCat != null) && (codPresentacion != null) && (codMarca != null) && (codMedida != null) && !(isEmpty(peso)) && !(isEmpty(gasto)) && !(isEmpty(cantidad))) {
                Log.d("VERIF_TYPE", "2");
                passed = true;
            }
        }
        if (validationType == 3) {
            if ((codCat != null) && !(isEmpty(gasto)) && !(isEmpty(cantidad))) {
                Log.d("VERIF_TYPE", "3");
                passed = true;
            }
        }

        if (validationType == 4) {
            if ((codCat != null)  && !(isEmpty(gasto)) && !(isEmpty(gasto)) && (codMarca != null) &&  !(isEmpty(cantidad))) {
                Log.d("VERIF_TYPE", "4");
                passed = true;
            }
        }

        if (passed == true) {
            dato = compraData;

            dato.put("codCat", codCat);
            dato.put("categoria", categoriaSt);
            dato.put("codPresentacion", codPresentacion);
            dato.put("presentacion", presentacionSt);
            dato.put("pesounidad", pesoSt);
            dato.put("codMarca", codMarca);
            dato.put("marca", marcaSt);
            dato.put("codMedida", codMedida);
            dato.put("medida", medidaSt);
            dato.put("gasto", gasto.getText().toString());
            dato.put("moneda", monedaSt);
            dato.put("peso", peso.getText().toString());
            dato.put("otraMarca", otraMarca.getText().toString());
            dato.put("barcode", barcode.getText().toString());
            dato.put("cantidad", cantidad.getText().toString());

            if (editando == 1) {
                compras.set(prodIndex, dato);
            } else {
                compras.add(dato);
            }

            if (action == 1) {
                Intent intent = new Intent(NewProduct.this, NewProduct.class);
                Bundle args = new Bundle();
                args.putSerializable("compras", (Serializable) compras);
                args.putString("compra", new Gson().toJson(compraData));
                intent.putExtra("BUNDLE", args);
                startActivity(intent);
            } else {
                Intent intent = new Intent(NewProduct.this, FinishPurchase.class);
                Bundle args = new Bundle();
                args.putSerializable("compras", (Serializable) compras);
                args.putString("compra", new Gson().toJson(compraData));
                intent.putExtra("BUNDLE", args);
                startActivity(intent);
            }
        } else {
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(NewProduct.this)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setTitle("Atención")
                    .setMessage("Por favor ingrese todos los campos.")
                    .addButton("OK", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                        dialog.dismiss();
                    });
            builder.show();

        }

    }

    private void getCategorias() {
        String packagesUrl = "http://skill-ca.com/api/categorias.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.getCache().clear();
        //startLoader();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, packagesUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE", response);
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                    //stopLoader();
                    if (json.getInt("code") == 1) {
                        JSONArray resultado = json.getJSONArray("result");

                        String[] spinnerArray = new String[resultado.length() + 1];
                        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                        HashMap<String, Object> hintItem = new HashMap<String, Object>();
                        hintItem.put("codCat", 0);
                        hintItem.put("categoria", "Categoria");
                        items.add(hintItem);
                        spinnerArray[0] = "Categoria";

                        for (int i = 0; i < resultado.length(); i++) {
                            JSONObject resultObj = resultado.getJSONObject(i);

                            HashMap<String, Object> item = new HashMap<String, Object>();
                            item.put("codCat", Float.parseFloat(resultObj.get("codCat").toString()));
                            item.put("categoria", (resultObj.get("categoria").toString()));
                            items.add(item);

                            spinnerArray[i + 1] = (resultObj.get("categoria").toString());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        categoria.setAdapter(adapter);
                        //categoria.setSelection(0,false);
                        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (++check > 1) {
                                    if (position > 0) {
                                        Log.d("onItemSelected", "CAT_onItemSelected");
                                        if (((TextView) parent.getChildAt(0)) != null){
                                            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                        }

                                        codCat = items.get(position).get("codCat").toString();
                                        categoriaSt = items.get(position).get("categoria").toString();

                                        Log.d("codCat", "CAT_onItemSelected");

                                        String titulo = "";
                                        presentacionSt = titulo;
                                        pesoSt = titulo;
                                        codPresentacion = null;
                                        presentacion.setSelection(0);

                                        marcaSt = titulo;
                                        codMarca = null;
                                        marcas.setSelection(0);

                                        monedaSt = 0;
                                        moneda.setSelection(0);

                                        gasto.setText("");

                                        marcas.setSelection(0);

                                        cantidad.setText("");

                                        LinearLayout pesoLayoutRow = findViewById(R.id.pesoLayoutRow);
                                        ShadowLayout presentacionLayout = findViewById(R.id.presentacionLayout);
                                        ShadowLayout marcaLayout = findViewById(R.id.marcaLayout);
                                        ShadowLayout gastoView = findViewById(R.id.gastoView);
                                        LinearLayout gastoStack = findViewById(R.id.gastoLayoutRow);

                                        if ((spinnerArray[position].contains("PAN DETALLADO"))) {
                                            presentacionLayout.setVisibility(View.GONE);
                                            pesoLayoutRow.setVisibility(View.GONE);
                                            marcaLayout.setVisibility(View.GONE);

                                            gastoStack.setVisibility(View.VISIBLE);
                                            validationType = 3;
                                        } else {
                                            presentacionLayout.setVisibility(View.VISIBLE);
                                            marcaLayout.setVisibility(View.VISIBLE);
                                            pesoLayoutRow.setVisibility(View.GONE);
                                            gastoStack.setVisibility(View.GONE);

                                            validationType = 0;

                                            getPresentaciones(codCat);
                                            getMarcas(codCat);
                                            getUnidades(codCat);
                                        }

                                         if ((spinnerArray[position].contains("HUEVOS")) || (spinnerArray[position].contains("CUBITOS")) || (spinnerArray[position].contains("GALLETAS DULCES")) || (spinnerArray[position].contains("GALLETAS SALADAS"))){
                                            presentacionLayout.setVisibility(View.VISIBLE);
                                            pesoLayoutRow.setVisibility(View.GONE);
                                            marcaLayout.setVisibility(View.VISIBLE);
                                        }

                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        if (editando == 1) {
                            String categoriaCompra = compraData.get("codCat").toString();
                            codCat = categoriaCompra;

                            String categoriaCompraName = compraData.get("categoria").toString();
                            categoriaSt = categoriaCompraName;
                            int spinnerPosition = adapter.getPosition(categoriaCompraName);
                            categoria.setSelection(spinnerPosition);

                            LinearLayout pesoLayoutRow = findViewById(R.id.pesoLayoutRow);
                            ShadowLayout presentacionLayout = findViewById(R.id.presentacionLayout);
                            ShadowLayout marcaLayout = findViewById(R.id.marcaLayout);
                            ShadowLayout gastoView = findViewById(R.id.gastoView);
                            LinearLayout gastoStack = findViewById(R.id.gastoLayoutRow);

                            if ((categoriaCompraName.contains("PAN DETALLADO"))) {
                                presentacionLayout.setVisibility(View.GONE);
                                pesoLayoutRow.setVisibility(View.GONE);
                                marcaLayout.setVisibility(View.GONE);
                                gastoStack.setVisibility(View.VISIBLE);
                            } else {
                                getPresentaciones(codCat);
                                getMarcas(codCat);
                            }

                            if ((categoriaCompraName.contains("HUEVOS")) || (categoriaCompraName.contains("CUBITOS"))|| (categoriaSt.contains("GALLETAS DULCES")) || (categoriaSt.contains("GALLETAS SALADAS"))){

                                presentacionLayout.setVisibility(View.VISIBLE);
                                pesoLayoutRow.setVisibility(View.VISIBLE);
                                marcaLayout.setVisibility(View.VISIBLE);
                                getPresentaciones(codCat);
                                getMarcas(codCat);
                            }

                        }
                    }
                } catch (JSONException e) {
                    Log.d("ERROR", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("VOLLey error", volleyError.getMessage());
                if (volleyError instanceof TimeoutError) {

                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            public Request.Priority getPriority() {
                return Request.Priority.IMMEDIATE;
            }
        };

        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    private void getPresentaciones(String idCat) {
        String packagesUrl = "http://skill-ca.com/api/presentaciones.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.getCache().clear();
        //startLoader();
        Log.d("getPresentaciones", "getPresentaciones");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, packagesUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE_PRES", response);
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                    //stopLoader();
                    if (json.getInt("code") == 1) {
                        JSONArray resultado = json.getJSONArray("result");

                        String[] spinnerArray = new String[resultado.length() + 1];
                        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                        HashMap<String, Object> hintItem = new HashMap<String, Object>();
                        hintItem.put("codPresentacion", 0);
                        hintItem.put("presentacion", "Presentacion");
                        hintItem.put("pesounidad", "0");
                        items.add(hintItem);
                        spinnerArray[0] = "Presentacion";

                        for (int i = 0; i < resultado.length(); i++) {
                            JSONObject resultObj = resultado.getJSONObject(i);

                            HashMap<String, Object> item = new HashMap<String, Object>();
                            item.put("codPresentacion", Integer.parseInt(resultObj.get("codPresentacion").toString()));
                            item.put("presentacion", (resultObj.get("presentacion").toString()));
                            item.put("pesounidad", (resultObj.get("peso").toString()));
                            items.add(item);

                            spinnerArray[i + 1] = (resultObj.get("presentacion").toString());

                        }
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        presentacion.setAdapter(adapter);
                        presentacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {

                                    if (((TextView) parent.getChildAt(0)) != null) {
                                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                    }

                                        codPresentacion = items.get(position).get("codPresentacion").toString();
                                        presentacionSt = items.get(position).get("presentacion").toString();
                                        pesoSt = items.get(position).get("pesounidad").toString();

                                        validationType = 0;

                                        LinearLayout pesoLayoutRow = findViewById(R.id.pesoLayoutRow);
                                        ShadowLayout gastoView = findViewById(R.id.gastoView);
                                        LinearLayout gastoStack = findViewById(R.id.gastoLayoutRow);
                                        if (spinnerArray[position].contains("OTRA")) {
                                            pesoSt = "";
                                            gastoStack.setVisibility(View.GONE);
                                            pesoLayoutRow.setVisibility(View.VISIBLE);
                                            validationType = 1;
                                            if ((categoriaSt.contains("HUEVOS")) || (categoriaSt.contains("CUBITOS")) || (categoriaSt.contains("GALLETAS DULCES")) || (categoriaSt.contains("GALLETAS SALADAS")) ){
                                                pesoLayoutRow.setVisibility(View.GONE);
                                                gastoStack.setVisibility(View.VISIBLE);
                                                validationType = 4;
                                            }
                                        } else {
                                            gastoStack.setVisibility(View.GONE);
                                            pesoLayoutRow.setVisibility(View.GONE);
                                        }

                                        if (spinnerArray[position].equals("DETALLADO")) {
                                            validationType = 2;
                                            gastoStack.setVisibility(View.VISIBLE);
                                            pesoLayoutRow.setVisibility(View.VISIBLE);
                                            if ((categoriaSt.contains("HUEVOS")) || (categoriaSt.contains("CUBITOS")) || (categoriaSt.contains("GALLETAS DULCES")) || (categoriaSt.contains("GALLETAS SALADAS"))){
                                                pesoLayoutRow.setVisibility(View.GONE);
                                                gastoStack.setVisibility(View.VISIBLE);
                                                validationType = 4;
                                            }
                                        }
                                    }



                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        if (editando == 1) {

                            LinearLayout pesoLayoutRow = findViewById(R.id.pesoLayoutRow);

                            if (compraData.get("codPresentacion") != null) {
                                String codigo = compraData.get("codPresentacion").toString();
                                codPresentacion = codigo;
                                String titulo = compraData.get("presentacion").toString();
                                presentacionSt = titulo;
                                String pesounidad = compraData.get("pesounidad").toString();
                                pesoSt = pesounidad;
                                int spinnerPosition = adapter.getPosition(titulo);
                                presentacion.setSelection(spinnerPosition);
                            }


                            ShadowLayout gastoView = findViewById(R.id.gastoView);
                            LinearLayout gastoStack = findViewById(R.id.gastoLayoutRow);
                            if (presentacionSt.contains("OTRA")) {
                                gastoStack.setVisibility(View.GONE);
                                pesoLayoutRow.setVisibility(View.VISIBLE);
                                validationType = 1;
                                if ((categoriaSt.contains("HUEVOS")) || (categoriaSt.contains("CUBITOS")) || (categoriaSt.contains("GALLETAS DULCES")) || (categoriaSt.contains("GALLETAS SALADAS"))){
                                    pesoLayoutRow.setVisibility(View.GONE);
                                    gastoStack.setVisibility(View.VISIBLE);
                                    validationType = 4;
                                }
                            } else {
                                gastoStack.setVisibility(View.GONE);
                                pesoLayoutRow.setVisibility(View.GONE);
                            }

                            if (presentacionSt.equals("DETALLADO")) {
                                gastoStack.setVisibility(View.VISIBLE);
                                pesoLayoutRow.setVisibility(View.VISIBLE);
                                validationType = 2;
                                if ((categoriaSt.contains("HUEVOS")) || (categoriaSt.contains("CUBITOS")) || (categoriaSt.contains("GALLETAS DULCES")) || (categoriaSt.contains("GALLETAS SALADAS"))){
                                    pesoLayoutRow.setVisibility(View.GONE);
                                    gastoStack.setVisibility(View.VISIBLE);
                                    validationType = 4;
                                }
                            }



                            getUnidades(codCat);


                        }

                    }
                } catch (JSONException e) {
                    Log.d("ERROR", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("volleyError", String.valueOf(volleyError.toString()));
                if (volleyError instanceof TimeoutError) {

                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("codCat", idCat);
                return params;
            }

            @Override
            public Request.Priority getPriority() {
                return Request.Priority.IMMEDIATE;
            }
        };

        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    private void getUnidades(String codCat) {
        Log.d("getUnidades", "getUnidades");
        String packagesUrl = "http://skill-ca.com/api/medidas.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.getCache().clear();
        //startLoader();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, packagesUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE_MEDIDAS", response);
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                    //stopLoader();

                    if (json.getInt("code") == 1) {
                        JSONArray resultado = json.getJSONArray("result");
                        String[] spinnerArray = new String[resultado.length() + 1];

                        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                        HashMap<String, Object> hintItem = new HashMap<String, Object>();
                        hintItem.put("codMedida", 0);
                        hintItem.put("medida", "Medida");
                        items.add(hintItem);
                        spinnerArray[0] = "Medida";

                        for (int i = 0; i < resultado.length(); i++) {
                            JSONObject resultObj = resultado.getJSONObject(i);

                            HashMap<String, Object> item = new HashMap<String, Object>();
                            item.put("codMedida", Integer.parseInt(resultObj.get("codMedida").toString()));
                            item.put("medida", (resultObj.get("medida").toString()));
                            items.add(item);
                            spinnerArray[i + 1] = (resultObj.get("medida").toString());


                        }
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        unidad.setAdapter(adapter);
                        unidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {
                                    if (((TextView) parent.getChildAt(0)) != null){
                                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                    }


                                    codMedida = items.get(position).get("codMedida").toString();
                                    medidaSt = items.get(position).get("medida").toString();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        if (editando == 1) {
                            if (compraData.containsKey("codMedida") == true) {
                                String codigo = compraData.get("codMedida").toString();
                                codMedida = codigo;
                                String titulo = compraData.get("medida").toString();
                                medidaSt = titulo;

                                Log.d("MEDIDA_ST", titulo);
                                int spinnerPosition = adapter.getPosition(titulo);
                                unidad.setSelection(spinnerPosition);
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.d("ERROR", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("VOLLey error", volleyError.getMessage());
                if (volleyError instanceof TimeoutError) {

                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("idCat", codCat);
                return params;
            }

            @Override
            public Request.Priority getPriority() {
                return Request.Priority.IMMEDIATE;
            }
        };

        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }


    private void setupMoneda() {

        String[] spinnerArray = new String[2];
        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> hintItem = new HashMap<String, Object>();
        hintItem.put("moneda", "BOLIVARES");
        items.add(hintItem);
        spinnerArray[0] = "BOLIVARES";

        HashMap<String, Object> hintItem2 = new HashMap<String, Object>();
        hintItem2.put("moneda", "DOLARES");
        items.add(hintItem2);
        spinnerArray[1] = "DOLARES";

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moneda.setAdapter(adapter);
        moneda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    if (((TextView) parent.getChildAt(0)) != null){
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                    }


                    Log.d("moneda_position", String.valueOf(position));
                    monedaSt = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Log.d("editando_moneda", String.valueOf(editando));
        if (editando == 1) {
            if (compraData.containsKey("moneda") == true) {
                Double titulo = Double.parseDouble(compraData.get("moneda").toString());
                monedaSt = Integer.valueOf(titulo.intValue());
                moneda.setSelection(monedaSt);
            }
        }
    }

    private void getMarcas(String idCat) {
        String packagesUrl = "http://skill-ca.com/api/marcas.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.getCache().clear();
        //startLoader();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, packagesUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE", response);
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                    //stopLoader();
                    if (json.getInt("code") == 1) {
                        JSONArray resultado = json.getJSONArray("result");

                        String[] spinnerArray = new String[resultado.length() + 1];
                        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                        HashMap<String, Object> hintItem = new HashMap<String, Object>();
                        hintItem.put("codMarca", 0);
                        hintItem.put("marca", "Marca");
                        items.add(hintItem);
                        spinnerArray[0] = "Marca";

                        for (int i = 0; i < resultado.length(); i++) {
                            JSONObject resultObj = resultado.getJSONObject(i);

                            HashMap<String, Object> item = new HashMap<String, Object>();
                            item.put("codMarca", Integer.parseInt(resultObj.get("codMarca").toString()));
                            item.put("marca", (resultObj.get("marca").toString()));
                            items.add(item);

                            spinnerArray[i + 1] = (resultObj.get("marca").toString());

                        }
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        marcas.setAdapter(adapter);
                        marcas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {
                                    if (((TextView) parent.getChildAt(0)) != null) {
                                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                    }
                                        codMarca = items.get(position).get("codMarca").toString();
                                        marcaSt = items.get(position).get("marca").toString();
                                        ShadowLayout otraMarcaView = findViewById(R.id.otraMarcaView);
                                        Log.d("OTRA MARCA", spinnerArray[position]);
                                        if (spinnerArray[position].contains("OTRA")) {
                                            otraMarcaView.setVisibility(View.VISIBLE);
                                        } else {
                                            otraMarcaView.setVisibility(View.INVISIBLE);
                                        }
                                    }


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        if (editando == 1) {
                            if (compraData.get("codMarca") != null) {
                                String codigo = compraData.get("codMarca").toString();
                                codMarca = codigo;
                            }
                            if (compraData.get("marca") != null) {
                                String titulo = compraData.get("marca").toString();
                                marcaSt = titulo;
                                int spinnerPosition = adapter.getPosition(titulo);
                                marcas.setSelection(spinnerPosition);
                            }

                        }

                    }
                } catch (JSONException e) {
                    Log.d("ERROR", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("VOLLey error", volleyError.getMessage());
                if (volleyError instanceof TimeoutError) {

                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("codCat", idCat);
                return params;
            }

            @Override
            public Request.Priority getPriority() {
                return Request.Priority.IMMEDIATE;
            }
        };

        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    private void logout() {

        SharedPreferences mPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.remove("idHogar").apply();
        prefsEditor.remove("grupo").apply();
        prefsEditor.remove("estado").apply();
        prefsEditor.remove("municipio").apply();
        prefsEditor.remove("ciudad").apply();

        Intent intent = new Intent(NewProduct.this, SignInActivity.class);
        startActivity(intent);
    }


    private void startLoader() {
        loader = findViewById(R.id.animation_view);
        overlay = findViewById(R.id.overlay);
        overlay.setVisibility(View.VISIBLE);
        loader.setVisibility(View.VISIBLE);
        loader.setRenderMode(RenderMode.HARDWARE);
    }

    private void stopLoader() {
        loader.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
    }

}