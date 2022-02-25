package com.carusoft.skill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

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

    private SearchableSpinner categoria;
    private SearchableSpinner marcas;
    private SearchableSpinner unidad;
    private SearchableSpinner presentacion;
    private AppCompatEditText otraMarca;
    private AppCompatEditText otraPresentacion;
    private AppCompatEditText gasto;
    private AppCompatEditText peso;
    private AppCompatEditText cantidad;


    private String codCat;
    private String codPresentacion;
    private String codMarca;
    private String codMedida;
    private HashMap<String, Object> compraData;

    ArrayList<HashMap<String, Object>> compras;
    private String categoriaSt;
    private String marcaSt;
    private String presentacionSt;
    private String medidaSt;

    private Integer editando;
    private Integer prodIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");

        TextView titulo = (TextView) findViewById(R.id.titulo);
        TextView titulo2 = (TextView) findViewById(R.id.titulo2);



        compras = (ArrayList<HashMap<String, Object>>) args.getSerializable("compras");

        String compra = args.getString("compra");
        compraData = new Gson().fromJson(compra, new TypeToken<HashMap<String, Object>>() {}.getType());

        TextView fecha = (TextView) findViewById(R.id.fecha);
        fecha.setText(compraData.get("fecha").toString());

        TextView dia = (TextView) findViewById(R.id.dia);
        compraData.get("dia");

        TextView tipoNegocio = (TextView) findViewById(R.id.tipoNegocio);
        tipoNegocio.setText(compraData.get("tipoNegocio").toString());

        TextView nombre = (TextView) findViewById(R.id.nombre);
        nombre.setText(compraData.get("nombre").toString());

        TextView lugar = (TextView) findViewById(R.id.lugar);
        lugar.setText(compraData.get("lugar").toString());

        categoria = (SearchableSpinner) findViewById(R.id.categoria);
        marcas = (SearchableSpinner) findViewById(R.id.marcas);
        unidad = (SearchableSpinner) findViewById(R.id.unidad);
        presentacion = (SearchableSpinner) findViewById(R.id.presentacion);
        otraMarca = (AppCompatEditText) findViewById(R.id.otraMarca);
        otraPresentacion = (AppCompatEditText) findViewById(R.id.otraPresentacion);
        gasto = (AppCompatEditText) findViewById(R.id.gasto);
        peso = (AppCompatEditText) findViewById(R.id.peso);
        cantidad = (AppCompatEditText) findViewById(R.id.cantidad);


        if (args.getInt("editando") == 1){
            editando = 1;
            prodIndex = args.getInt("prodIndex");
            titulo.setText("Editar Producto");
            titulo2.setText("Editar Producto");


            gasto.setText(compraData.get("gasto").toString());
            otraMarca.setText(compraData.get("otraMarca").toString());
            otraPresentacion.setText(compraData.get("otraPresentacion").toString());
            cantidad.setText(compraData.get("cantidad").toString());
            peso.setText(compraData.get("peso").toString());

        }

        getCategorias();
        getUnidades();

        Button nuevoProd = (Button) findViewById(R.id.nuevoProd);
        nuevoProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProd(1);
            }
        });

        Button finCompra = (Button) findViewById(R.id.finCompra);
        finCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProd(2);
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


    }


    private void saveProd(Integer action){
        HashMap<String, Object> dato = new HashMap<>();

        if ((codCat != null) && (codPresentacion != null) && (codMarca != null) && (codMedida != null) && (!peso.getText().equals("")) && (!gasto.getText().equals("")) && (!cantidad.getText().equals(""))){
            dato = compraData;

            dato.put("codCat", codCat);
            dato.put("categoria", categoriaSt);
            dato.put("codPresentacion", codPresentacion);
            dato.put("presentacion", presentacionSt);
            dato.put("codMarca", codMarca);
            dato.put("marca", marcaSt);
            dato.put("codMedida", codMedida);
            dato.put("medida", medidaSt);
            dato.put("otraPresentacion", otraPresentacion.getText().toString());
            dato.put("otraMarca", otraMarca.getText().toString());
            dato.put("peso", peso.getText().toString());
            dato.put("gasto", gasto.getText().toString());
            dato.put("cantidad", cantidad.getText().toString());

            if (editando == 1){
                compras.set(prodIndex, dato);
            }else{
                compras.add(dato);
            }

            if (action == 1){
                Intent intent = new Intent(NewProduct.this, NewProduct.class);
                Bundle args = new Bundle();
                args.putSerializable("compras",(Serializable)compras);
                args.putString("compra", new Gson().toJson(compraData));
                intent.putExtra("BUNDLE",args);
                startActivity(intent);
            }else {
                Intent intent = new Intent(NewProduct.this, FinishPurchase.class);
                Bundle args = new Bundle();
                args.putSerializable("compras", (Serializable) compras);
                args.putString("compra", new Gson().toJson(compraData));
                intent.putExtra("BUNDLE", args);
                startActivity(intent);
            }
        }else {
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

                        String[] spinnerArray = new String[resultado.length()+1];
                        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                        HashMap<String, Object> hintItem  = new HashMap<String, Object>();
                        hintItem.put("codCat", 0);
                        hintItem.put("categoria", "Categoria");
                        items.add(hintItem);
                        spinnerArray[0] =  "Categoria";

                        for (int i = 0 ; i < resultado.length(); i++) {
                            JSONObject resultObj = resultado.getJSONObject(i);

                            HashMap<String, Object> item  = new HashMap<String, Object>();
                            item.put("codCat", Integer.parseInt(resultObj.get("codCat").toString()));
                            item.put("categoria", (resultObj.get("categoria").toString()));
                            items.add(item);

                            spinnerArray[i+1] =  (resultObj.get("categoria").toString());

                        }
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        categoria.setAdapter(adapter);
                        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                    codCat = items.get(position).get("codCat").toString();
                                    categoriaSt = items.get(position).get("categoria").toString();

                                    String titulo = "";
                                    presentacionSt = titulo;
                                    codPresentacion = null;
                                    presentacion.setSelection(0);

                                    marcaSt = titulo;
                                    codMarca = null;
                                    marcas.setSelection(0);

                                    getPresentaciones(codCat);
                                    getMarcas(codCat);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        if (editando == 1){
                            String categoriaCompra = compraData.get("codCat").toString();
                            codCat = categoriaCompra;

                            getPresentaciones(codCat);
                            getMarcas(codCat);

                            String categoriaCompraName = compraData.get("categoria").toString();
                            categoriaSt = categoriaCompraName;
                            int spinnerPosition = adapter.getPosition(categoriaCompraName);
                            categoria.setSelection(spinnerPosition);
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

                        String[] spinnerArray = new String[resultado.length()+1];
                        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                        HashMap<String, Object> hintItem  = new HashMap<String, Object>();
                        hintItem.put("codPresentacion", 0);
                        hintItem.put("presentacion", "Presentacion");
                        items.add(hintItem);
                        spinnerArray[0] =  "Presentacion";

                        for (int i = 0 ; i < resultado.length(); i++) {
                            JSONObject resultObj = resultado.getJSONObject(i);

                            HashMap<String, Object> item  = new HashMap<String, Object>();
                            item.put("codPresentacion", Integer.parseInt(resultObj.get("codPresentacion").toString()));
                            item.put("presentacion", (resultObj.get("presentacion").toString()));
                            items.add(item);

                            spinnerArray[i+1] =  (resultObj.get("presentacion").toString());

                        }
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        presentacion.setAdapter(adapter);
                        presentacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                    codPresentacion = items.get(position).get("codPresentacion").toString();
                                    presentacionSt = items.get(position).get("presentacion").toString();
                                    ShadowLayout otraPresentacionView = findViewById(R.id.otraPresentacionView);
                                    if (spinnerArray[position].equals("OTRA")){
                                        otraPresentacionView.setVisibility(View.VISIBLE);
                                    }else{
                                        otraPresentacionView.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        if (editando == 1){
                            String codigo = compraData.get("codPresentacion").toString();
                            codPresentacion = codigo;
                            String titulo = compraData.get("presentacion").toString();
                            presentacionSt = titulo;
                            int spinnerPosition = adapter.getPosition(titulo);
                            presentacion.setSelection(spinnerPosition);
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
                params.put("codCat",idCat);
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

    private void getUnidades() {
        String packagesUrl = "http://skill-ca.com/api/unidades.php";

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

                        String[] spinnerArray = new String[resultado.length()];
                        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
                        for (int i = 0 ; i < resultado.length(); i++) {
                            JSONObject resultObj = resultado.getJSONObject(i);

                            HashMap<String, Object> item  = new HashMap<String, Object>();
                            item.put("codMedida", Integer.parseInt(resultObj.get("codMedida").toString()));
                            item.put("medida", (resultObj.get("medida").toString()));
                            items.add(item);

                            spinnerArray[i] =  (resultObj.get("medida").toString());

                        }
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        unidad.setAdapter(adapter);
                        unidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                    codMedida = items.get(position).get("codMedida").toString();
                                    medidaSt = items.get(position).get("medida").toString();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        if (editando == 1){
                            String codigo = compraData.get("codMedida").toString();
                            codMedida = codigo;
                            String titulo = compraData.get("medida").toString();
                            medidaSt = titulo;
                            int spinnerPosition = adapter.getPosition(titulo);
                            unidad.setSelection(spinnerPosition);
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

                        String[] spinnerArray = new String[resultado.length()+1];
                        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                        HashMap<String, Object> hintItem  = new HashMap<String, Object>();
                        hintItem.put("codMarca", 0);
                        hintItem.put("marca", "Marca");
                        items.add(hintItem);
                        spinnerArray[0] =  "Marca";

                        for (int i = 0 ; i < resultado.length(); i++) {
                            JSONObject resultObj = resultado.getJSONObject(i);

                            HashMap<String, Object> item  = new HashMap<String, Object>();
                            item.put("codMarca", Integer.parseInt(resultObj.get("codMarca").toString()));
                            item.put("marca", (resultObj.get("marca").toString()));
                            items.add(item);

                            spinnerArray[i+1] =  (resultObj.get("marca").toString());

                        }
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        marcas.setAdapter(adapter);
                        marcas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                    codMarca = items.get(position).get("codMarca").toString();
                                    marcaSt = items.get(position).get("marca").toString();
                                    ShadowLayout otraMarcaView = findViewById(R.id.otraMarcaView);
                                    if (spinnerArray[position].equals("OTRA")){
                                        otraMarcaView.setVisibility(View.VISIBLE);
                                    }else{
                                        otraMarcaView.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        if (editando == 1){
                            String codigo = compraData.get("codMarca").toString();
                            codMarca = codigo;
                            String titulo = compraData.get("marca").toString();
                            marcaSt = titulo;
                            int spinnerPosition = adapter.getPosition(titulo);
                            marcas.setSelection(spinnerPosition);
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
                params.put("codCat",idCat);
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

    private void logout(){

        SharedPreferences mPrefs = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.remove("idHogar").apply();
        prefsEditor.remove("grupo").apply();
        prefsEditor.remove("estado").apply();
        prefsEditor.remove("municipio").apply();
        prefsEditor.remove("ciudad").apply();

        Intent intent = new Intent(NewProduct.this, SignInActivity.class);
        startActivity(intent);
    }




}