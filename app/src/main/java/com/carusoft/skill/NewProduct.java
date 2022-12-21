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

    private LinearLayout tipoContainer;
    private LinearLayout saborContainer;
    private LinearLayout fraganciaContainer;
    private LinearLayout variedadContainer;
    private RelativeLayout fabricanteLayout;
    private RelativeLayout cantidadLayout;
    private LinearLayout codBarrasLayout;
    private String codTipo;
    private String tipoSt;
    private RelativeLayout otroTipoLayout;
    private String codSabor;
    private String saborSt;
    private RelativeLayout otroSaborLayout;

    private String codFragancia;
    private String fraganciaSt;
    private RelativeLayout otraFraganciaLayout;

    private String codVariedad;
    private String variedadSt;
    private RelativeLayout otraVariedadLayout;


    public interface VolleyCallBack {
        void onSuccess(JSONObject json) throws JSONException;
    }

    int check = 0;

    Integer validationType = 0;
    private SearchableSpinner categoria;
    private SearchableSpinner marcas;
    private SearchableSpinner unidad;
    private SearchableSpinner moneda;
    private SearchableSpinner presentacion;
    private SearchableSpinner tipo;
    private SearchableSpinner fragancia;
    private SearchableSpinner sabor;
    private SearchableSpinner variedad;

    private AppCompatEditText fabricante;
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

    private LinearLayout pesoLayout;
    private RelativeLayout presentacionLayout;
    private RelativeLayout marcaLayout;
    private LinearLayout gastoLayout;
    private RelativeLayout otraMarcaLayout;

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

        setHeaderInfo(compraData);

        pesoLayout = (LinearLayout) findViewById(R.id.pesoLayout);
        presentacionLayout = (RelativeLayout) findViewById(R.id.presentacionLayout);
        marcaLayout = (RelativeLayout) findViewById(R.id.marcaLayout);
        otraMarcaLayout = (RelativeLayout) findViewById(R.id.otraMarcaLayout);
        gastoLayout = (LinearLayout) findViewById(R.id.gastoLayout);

        tipoContainer = (LinearLayout) findViewById(R.id.tipoContainer);
        otroTipoLayout = (RelativeLayout) findViewById(R.id.otroTipoLayout);
        saborContainer = (LinearLayout) findViewById(R.id.saborContainer);
        otroSaborLayout = (RelativeLayout) findViewById(R.id.otroSaborLayout);
        fraganciaContainer = (LinearLayout) findViewById(R.id.fraganciaContainer);
        variedadContainer = (LinearLayout) findViewById(R.id.variedadContainer);
        fabricanteLayout = (RelativeLayout) findViewById(R.id.fabricanteLayout);
        cantidadLayout = (RelativeLayout) findViewById(R.id.cantidadLayout);
        codBarrasLayout = (LinearLayout) findViewById(R.id.codBarrasLayout);

        categoria = (SearchableSpinner) findViewById(R.id.categoria);
        marcas = (SearchableSpinner) findViewById(R.id.marcas);
        unidad = (SearchableSpinner) findViewById(R.id.unidad);
        moneda = (SearchableSpinner) findViewById(R.id.moneda);
        presentacion = (SearchableSpinner) findViewById(R.id.presentacion);
        tipo = (SearchableSpinner) findViewById(R.id.tipo);
        fragancia = (SearchableSpinner) findViewById(R.id.fragancia);
        sabor = (SearchableSpinner) findViewById(R.id.sabor);
        variedad = (SearchableSpinner) findViewById(R.id.variedad);

        fabricante = (AppCompatEditText) findViewById(R.id.fabricante);
        otraMarca = (AppCompatEditText) findViewById(R.id.otraMarca);
        gasto = (AppCompatEditText) findViewById(R.id.gasto);
        peso = (AppCompatEditText) findViewById(R.id.peso);
        cantidad = (AppCompatEditText) findViewById(R.id.cantidad);

        setPlaceholders();

        if (args.getInt("editando") == 1) {
            editando = 1;
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

    private void setPlaceholders(){
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, new String[]{"Categoria"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(adapter);

        adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, new String[]{"Presentacion"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        presentacion.setAdapter(adapter);

        adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, new String[]{"Unidad"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unidad.setAdapter(adapter);

        adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, new String[]{"Tipo"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(adapter);

        adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, new String[]{"Fragancia"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragancia.setAdapter(adapter);

        adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, new String[]{"Variedad"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        variedad.setAdapter(adapter);

        adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, new String[]{"Sabor"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sabor.setAdapter(adapter);

        adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, new String[]{"Marca"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        marcas.setAdapter(adapter);

        adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, new String[]{"Moneda"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moneda.setAdapter(adapter);


    }

    private void setHeaderInfo(HashMap<String, Object> compraData) {

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

        /*TextView estado = (TextView) findViewById(R.id.estado);
        estado.setText(compraData.get("estado").toString());

        TextView municipio = (TextView) findViewById(R.id.municipio);
        municipio.setText(compraData.get("municipio").toString());

        TextView barrio = (TextView) findViewById(R.id.barrio);
        barrio.setText(compraData.get("barrio").toString());*/
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
            if ((codCat != null) && !(isEmpty(gasto)) && !(isEmpty(gasto)) && (codMarca != null) && !(isEmpty(cantidad))) {
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

    private void getData(final VolleyCallBack callBack, String url, HashMap<String, String> params) {
        String packagesUrl = url;

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.getCache().clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, packagesUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject json = null;
                Log.d("TAG", response);
                try {
                    json = new JSONObject(response);
                    callBack.onSuccess(json);

                } catch (JSONException e) {
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

    private HashMap<String, Object> setHint(String cod, String name) {
        HashMap<String, Object> hintItem = new HashMap<String, Object>();
        hintItem.put(cod, 0);
        hintItem.put(name, name);
        return hintItem;
    }

    private void showFields(HashMap<String, Object> result, String codCat) {
        String marca = result.get("marca").toString();
        String tipo = result.get("tipo").toString();
        String sabor = result.get("sabor").toString();
        String fragancia = result.get("fragancia").toString();
        String variedad = result.get("variedad").toString();
        String fabricante = result.get("fabricante").toString();
        String cantidad = result.get("cantidad").toString();
        String costo = result.get("costo").toString();
        String codBarras = result.get("codBarras").toString();

        if (marca.equals("1")) {
            marcaLayout.setVisibility(View.VISIBLE);
            marcas.setTag(1);
        } else {
            marcaLayout.setVisibility(View.GONE);
            marcas.setTag(0);
        }
        if (tipo.equals("1")) {
            tipoContainer.setVisibility(View.VISIBLE);
            this.tipo.setTag(1);

            getTipo(codCat);

        } else {
            tipoContainer.setVisibility(View.GONE);
            this.tipo.setTag(0);
        }
        if (sabor.equals("1")) {
            saborContainer.setVisibility(View.VISIBLE);
            this.sabor.setTag(1);

            getSabor(codCat);

        } else {
            saborContainer.setVisibility(View.GONE);
            this.sabor.setTag(0);
        }
        if (fragancia.equals("1")) {
            fraganciaContainer.setVisibility(View.VISIBLE);
            this.fragancia.setTag(1);

            getFragancia(codCat);

        } else {
            fraganciaContainer.setVisibility(View.GONE);
            this.fragancia.setTag(0);
        }
        if (variedad.equals("1")) {
            variedadContainer.setVisibility(View.VISIBLE);
            this.variedad.setTag(1);
        } else {
            variedadContainer.setVisibility(View.GONE);
            this.variedad.setTag(0);
        }
        if (fabricante.equals("1")) {
            fabricanteLayout.setVisibility(View.VISIBLE);
            this.fabricante.setTag(1);
        } else {
            fabricanteLayout.setVisibility(View.GONE);
            this.fabricante.setTag(0);
        }
        if (cantidad.equals("1")) {
            cantidadLayout.setVisibility(View.VISIBLE);
            this.cantidad.setTag(1);
        } else {
            cantidadLayout.setVisibility(View.GONE);
            this.cantidad.setTag(0);
        }
        if (costo.equals("1")) {
            gastoLayout.setVisibility(View.VISIBLE);
            this.gasto.setTag(1);
        } else {
            gastoLayout.setVisibility(View.GONE);
            this.gasto.setTag(0);
        }
        if (codBarras.equals("1")) {
            codBarrasLayout.setVisibility(View.VISIBLE);
        } else {
            codBarrasLayout.setVisibility(View.GONE);
        }

    }

    private void resetData() {
        String empty = "";
        presentacionSt = empty;
        pesoSt = empty;
        marcaSt = empty;

        codPresentacion = null;
        presentacion.setSelection(0);
        codMarca = null;
        marcas.setSelection(0);
        monedaSt = 0;
        moneda.setSelection(0);
        gasto.setText(empty);
        marcas.setSelection(0);
        cantidad.setText(empty);
    }

    private void getCategorias() {
        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                    items.add(setHint("codCat", "categoria"));
                    spinnerArray[0] = "Categoria";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        items.add(item);
                        spinnerArray[i + 1] = (resultObj.get("categoria").toString());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categoria.setAdapter(adapter);
                    categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (++check > 0) {
                                if (position > 0) {
                                    if (((TextView) parent.getChildAt(0)) != null) {
                                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                    }

                                    codCat = items.get(position).get("codCat").toString();
                                    categoriaSt = items.get(position).get("categoria").toString();

                                    String por_unidad = items.get(position).get("por_unidad").toString();

                                    showFields(items.get(position), codCat);
                                    resetData();

                                    getPresentaciones(codCat,por_unidad);
                                    getMarcas(codCat);
                                    getUnidades(codCat);
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

                        if ((categoriaCompraName.contains("PAN DETALLADO"))) {
                            presentacionLayout.setVisibility(View.GONE);
                            pesoLayout.setVisibility(View.GONE);
                            marcaLayout.setVisibility(View.GONE);
                            gastoLayout.setVisibility(View.VISIBLE);
                        } else {
                            getPresentaciones(codCat, "0");
                            getMarcas(codCat);
                        }

                        if ((categoriaCompraName.contains("HUEVOS")) || (categoriaCompraName.contains("CUBITOS"))) { //|| (categoriaSt.contains("GALLETAS DULCES")) || (categoriaSt.contains("GALLETAS SALADAS"))){
                            presentacionLayout.setVisibility(View.VISIBLE);
                            pesoLayout.setVisibility(View.VISIBLE);
                            marcaLayout.setVisibility(View.VISIBLE);
                            getPresentaciones(codCat, "0");
                            getMarcas(codCat);
                        }

                    }
                }
            }
        }, "http://skill-ca.com/api/categorias.php", new HashMap<>());
    }

    private void getPresentaciones(String idCat, String por_unidad) {
        HashMap<String, String> params = new HashMap<>();
        params.put("codCat", idCat);

        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    if (resultado.length() == 0){
                        presentacionLayout.setVisibility(View.GONE);
                        pesoLayout.setVisibility(View.VISIBLE);
                    }

                    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
                    items.add(setHint("codPresentacion", "presentacion"));

                    String[] spinnerArray = new String[resultado.length() + 1];
                    spinnerArray[0] = "Presentacion";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
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

                                if (spinnerArray[position].contains("OTR")) {
                                    pesoLayout.setVisibility(View.VISIBLE);
                                } else {
                                    pesoLayout.setVisibility(View.GONE);
                                }

                                if (spinnerArray[position].contains("DETALLAD")) {
                                    pesoLayout.setVisibility(View.VISIBLE);

                                    if (por_unidad.equals("1")){
                                        pesoLayout.setVisibility(View.GONE);
                                        cantidadLayout.setVisibility(View.VISIBLE);
                                    }else{
                                        pesoLayout.setVisibility(View.VISIBLE);
                                        cantidadLayout.setVisibility(View.GONE);
                                    }

                                } else {
                                    pesoLayout.setVisibility(View.GONE);
                                }



                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }
        }, "http://skill-ca.com/api/presentaciones.php", params);
    }

    private void getUnidades(String idCat) {
        HashMap<String, String> params = new HashMap<>();
        params.put("idCat", idCat);

        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                    items.add(setHint("codMedida", "medida"));
                    spinnerArray[0] = "Medida";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
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
                                if (((TextView) parent.getChildAt(0)) != null) {
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
            }
        }, "http://skill-ca.com/api/medidas.php", params);
    }

    private void setupMoneda() {

        String[] spinnerArray = new String[4];
        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> hintItem = new HashMap<String, Object>();
        hintItem.put("moneda", "BOLIVARES");
        items.add(hintItem);
        spinnerArray[0] = "BOLIVARES";

        HashMap<String, Object> hintItem2 = new HashMap<String, Object>();
        hintItem2.put("moneda", "DOLARES");
        items.add(hintItem2);
        spinnerArray[1] = "DOLARES";

        HashMap<String, Object> hintItem3 = new HashMap<String, Object>();
        hintItem3.put("moneda", "PESO COLOMBIANO");
        items.add(hintItem3);
        spinnerArray[2] = "PESO COLOMBIANO";

        HashMap<String, Object> hintItem4 = new HashMap<String, Object>();
        hintItem4.put("moneda", "OTRA");
        items.add(hintItem4);
        spinnerArray[3] = "OTRA";

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moneda.setAdapter(adapter);
        moneda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    if (((TextView) parent.getChildAt(0)) != null) {
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
        HashMap<String, String> params = new HashMap<>();
        params.put("codCat", idCat);

        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                    items.add(setHint("codMarca", "marca"));
                    spinnerArray[0] = "Marca";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
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

                                if (spinnerArray[position].contains("OTRA")) {
                                    otraMarcaLayout.setVisibility(View.VISIBLE);
                                } else {
                                    otraMarcaLayout.setVisibility(View.GONE);
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
            }
        }, "http://skill-ca.com/api/marcas.php", params);
    }

    private void getTipo(String idCat) {
        HashMap<String, String> params = new HashMap<>();
        params.put("codCat", idCat);

        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                    items.add(setHint("codTipo", "tipo"));
                    spinnerArray[0] = "Tipo";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        items.add(item);
                        spinnerArray[i + 1] = (resultObj.get("tipo").toString());
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    tipo.setAdapter(adapter);
                    tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                if (((TextView) parent.getChildAt(0)) != null) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                }

                                codTipo = items.get(position).get("codTipo").toString();
                                tipoSt = items.get(position).get("tipo").toString();

                                if (spinnerArray[position].contains("OTR")) {
                                    otroTipoLayout.setVisibility(View.VISIBLE);
                                } else {
                                    otroTipoLayout.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    if (editando == 1) {
                        if (compraData.get("codTipo") != null) {
                            String codigo = compraData.get("codTipo").toString();
                            codTipo = codigo;
                        }
                        if (compraData.get("tipo") != null) {
                            String titulo = compraData.get("tipo").toString();
                            tipoSt = titulo;
                            int spinnerPosition = adapter.getPosition(titulo);
                            tipo.setSelection(spinnerPosition);
                        }
                    }

                }
            }
        }, "http://skill-ca.com/api/tipos.php", params);
    }

    private void getSabor(String idCat) {
        HashMap<String, String> params = new HashMap<>();
        params.put("codCat", idCat);

        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                    items.add(setHint("codSabor", "sabor"));
                    spinnerArray[0] = "Sabor";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        items.add(item);
                        spinnerArray[i + 1] = (resultObj.get("sabor").toString());
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sabor.setAdapter(adapter);
                    sabor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                if (((TextView) parent.getChildAt(0)) != null) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                }

                                codSabor = items.get(position).get("codSabor").toString();
                                saborSt = items.get(position).get("sabor").toString();

                                if (spinnerArray[position].contains("OTR")) {
                                    otroSaborLayout.setVisibility(View.VISIBLE);
                                } else {
                                    otroSaborLayout.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    if (editando == 1) {
                        if (compraData.get("codSabor") != null) {
                            String codigo = compraData.get("codSabor").toString();
                            codSabor = codigo;
                        }
                        if (compraData.get("sabor") != null) {
                            String titulo = compraData.get("sabor").toString();
                            saborSt = titulo;
                            int spinnerPosition = adapter.getPosition(titulo);
                            sabor.setSelection(spinnerPosition);
                        }
                    }

                }
            }
        }, "http://skill-ca.com/api/sabores.php", params);
    }

    private void getVariedad(String idCat) {
        HashMap<String, String> params = new HashMap<>();
        params.put("codCat", idCat);

        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                    items.add(setHint("codVariedad", "variedad"));
                    spinnerArray[0] = "Variedad";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        items.add(item);
                        spinnerArray[i + 1] = (resultObj.get("variedad").toString());
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    tipo.setAdapter(adapter);
                    tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                if (((TextView) parent.getChildAt(0)) != null) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                }

                                codVariedad = items.get(position).get("codVariedad").toString();
                                variedadSt = items.get(position).get("variedad").toString();

                                if (spinnerArray[position].contains("OTR")) {
                                    otraVariedadLayout.setVisibility(View.VISIBLE);
                                } else {
                                    otraVariedadLayout.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    if (editando == 1) {
                        if (compraData.get("codVariedad") != null) {
                            String codigo = compraData.get("codVariedad").toString();
                            codVariedad = codigo;
                        }
                        if (compraData.get("variedad") != null) {
                            String titulo = compraData.get("variedad").toString();
                            variedadSt = titulo;
                            int spinnerPosition = adapter.getPosition(titulo);
                            variedad.setSelection(spinnerPosition);
                        }
                    }

                }
            }
        }, "http://skill-ca.com/api/variedades.php", params);
    }

    private void getFragancia(String idCat) {
        HashMap<String, String> params = new HashMap<>();
        params.put("codCat", idCat);

        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                    items.add(setHint("codFragancia", "fragancia"));
                    spinnerArray[0] = "Fragancia";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        items.add(item);
                        spinnerArray[i + 1] = (resultObj.get("fragancia").toString());
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    fragancia.setAdapter(adapter);
                    fragancia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                if (((TextView) parent.getChildAt(0)) != null) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                }

                                codFragancia = items.get(position).get("codFragancia").toString();
                                fraganciaSt = items.get(position).get("fragancia").toString();

                                if (spinnerArray[position].contains("OTR")) {
                                    otraFraganciaLayout.setVisibility(View.VISIBLE);
                                } else {
                                    otraFraganciaLayout.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    if (editando == 1) {
                        if (compraData.get("codFragancia") != null) {
                            String codigo = compraData.get("codFragancia").toString();
                            codFragancia = codigo;
                        }
                        if (compraData.get("fragancia") != null) {
                            String titulo = compraData.get("fragancia").toString();
                            fraganciaSt = titulo;
                            int spinnerPosition = adapter.getPosition(titulo);
                            fragancia.setSelection(spinnerPosition);
                        }
                    }

                }
            }
        }, "http://skill-ca.com/api/fragancias.php", params);
    }


    private void getCategorias_old() {
        String packagesUrl = "http://skill-ca.com/api/categorias.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.getCache().clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, packagesUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
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
                        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (++check > 1) {
                                    if (position > 0) {
                                        if (((TextView) parent.getChildAt(0)) != null) {
                                            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                        }

                                        codCat = items.get(position).get("codCat").toString();
                                        categoriaSt = items.get(position).get("categoria").toString();

                                        String empty = "";
                                        presentacionSt = empty;
                                        pesoSt = empty;
                                        marcaSt = empty;

                                        codPresentacion = null;
                                        presentacion.setSelection(0);
                                        codMarca = null;
                                        marcas.setSelection(0);
                                        monedaSt = 0;
                                        moneda.setSelection(0);
                                        gasto.setText(empty);
                                        marcas.setSelection(0);
                                        cantidad.setText(empty);

                                        if ((spinnerArray[position].contains("PAN DETALLADO"))) {
                                            presentacionLayout.setVisibility(View.GONE);
                                            pesoLayout.setVisibility(View.GONE);
                                            marcaLayout.setVisibility(View.GONE);

                                            gastoLayout.setVisibility(View.VISIBLE);
                                            validationType = 3;
                                        } else {
                                            presentacionLayout.setVisibility(View.VISIBLE);
                                            marcaLayout.setVisibility(View.VISIBLE);
                                            pesoLayout.setVisibility(View.GONE);
                                            gastoLayout.setVisibility(View.GONE);

                                            validationType = 0;

                                            getPresentaciones(codCat, "0");
                                            getMarcas(codCat);
                                            getUnidades(codCat);
                                        }

                                        if ((spinnerArray[position].contains("HUEVOS")) || (spinnerArray[position].contains("CUBITOS"))) {// || (spinnerArray[position].contains("GALLETAS DULCES")) || (spinnerArray[position].contains("GALLETAS SALADAS"))){
                                            presentacionLayout.setVisibility(View.VISIBLE);
                                            pesoLayout.setVisibility(View.GONE);
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

                            if ((categoriaCompraName.contains("PAN DETALLADO"))) {
                                presentacionLayout.setVisibility(View.GONE);
                                pesoLayout.setVisibility(View.GONE);
                                marcaLayout.setVisibility(View.GONE);
                                gastoLayout.setVisibility(View.VISIBLE);
                            } else {
                                getPresentaciones(codCat, "0");
                                getMarcas(codCat);
                            }

                            if ((categoriaCompraName.contains("HUEVOS")) || (categoriaCompraName.contains("CUBITOS"))) { //|| (categoriaSt.contains("GALLETAS DULCES")) || (categoriaSt.contains("GALLETAS SALADAS"))){

                                presentacionLayout.setVisibility(View.VISIBLE);
                                pesoLayout.setVisibility(View.VISIBLE);
                                marcaLayout.setVisibility(View.VISIBLE);
                                getPresentaciones(codCat, "0");
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
    private void getPresentaciones_old(String idCat) {
        String packagesUrl = "http://skill-ca.com/api/presentaciones.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.getCache().clear();
        //startLoader();
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

                                    if (spinnerArray[position].contains("OTRA")) {
                                        pesoSt = "";
                                        gastoLayout.setVisibility(View.GONE);
                                        pesoLayout.setVisibility(View.VISIBLE);
                                        validationType = 1;
                                        if ((categoriaSt.contains("HUEVOS")) || (categoriaSt.contains("CUBITOS"))) { // || (categoriaSt.contains("GALLETAS DULCES")) || (categoriaSt.contains("GALLETAS SALADAS")) ){
                                            pesoLayout.setVisibility(View.GONE);
                                            gastoLayout.setVisibility(View.VISIBLE);
                                            validationType = 4;
                                        }
                                    } else {
                                        gastoLayout.setVisibility(View.GONE);
                                        pesoLayout.setVisibility(View.GONE);
                                    }

                                    if (spinnerArray[position].equals("DETALLADO")) {
                                        validationType = 2;
                                        gastoLayout.setVisibility(View.VISIBLE);
                                        pesoLayout.setVisibility(View.VISIBLE);
                                        if ((categoriaSt.contains("HUEVOS")) || (categoriaSt.contains("CUBITOS"))) { // || (categoriaSt.contains("GALLETAS DULCES")) || (categoriaSt.contains("GALLETAS SALADAS"))){
                                            pesoLayout.setVisibility(View.GONE);
                                            gastoLayout.setVisibility(View.VISIBLE);
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

                            if (presentacionSt.contains("OTRA")) {
                                gastoLayout.setVisibility(View.GONE);
                                pesoLayout.setVisibility(View.VISIBLE);
                                validationType = 1;
                                if ((categoriaSt.contains("HUEVOS")) || (categoriaSt.contains("CUBITOS"))) {// || (categoriaSt.contains("GALLETAS DULCES")) || (categoriaSt.contains("GALLETAS SALADAS"))){
                                    pesoLayout.setVisibility(View.GONE);
                                    gastoLayout.setVisibility(View.VISIBLE);
                                    validationType = 4;
                                }
                            } else {
                                gastoLayout.setVisibility(View.GONE);
                                pesoLayout.setVisibility(View.GONE);
                            }

                            if (presentacionSt.equals("DETALLADO")) {
                                gastoLayout.setVisibility(View.VISIBLE);
                                pesoLayout.setVisibility(View.VISIBLE);
                                validationType = 2;
                                if ((categoriaSt.contains("HUEVOS")) || (categoriaSt.contains("CUBITOS"))) {// || (categoriaSt.contains("GALLETAS DULCES")) || (categoriaSt.contains("GALLETAS SALADAS"))){
                                    pesoLayout.setVisibility(View.GONE);
                                    gastoLayout.setVisibility(View.VISIBLE);
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
    private void getUnidades_old(String codCat) {
        String packagesUrl = "http://skill-ca.com/api/medidas.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.getCache().clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, packagesUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE_MEDIDAS", response);
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
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
                                    if (((TextView) parent.getChildAt(0)) != null) {
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
    private void getMarcas_old(String idCat) {
        String packagesUrl = "http://skill-ca.com/api/marcas.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.getCache().clear();
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

                                    Log.d("OTRA MARCA", spinnerArray[position]);
                                    if (spinnerArray[position].contains("OTRA")) {
                                        otraMarcaLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        otraMarcaLayout.setVisibility(View.INVISIBLE);
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