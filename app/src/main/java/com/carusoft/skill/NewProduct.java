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
    ArrayList<HashMap<String, Object>> compras;
    Boolean loadedCats = true;
    Boolean loadedTipo = true;
    Boolean loadedFragancias = true;
    Boolean loadedVariedad = true;
    Boolean loadedSabor = true;
    Boolean loadedPresentacion = true;
    Boolean loadedUnidades = true;
    Boolean loadedMarcas = true;

    private Boolean modified = false;
    private LinearLayout tipoContainer;
    private LinearLayout saborContainer;
    private LinearLayout fraganciaContainer;
    private LinearLayout variedadContainer;
    private RelativeLayout fabricanteLayout;
    private RelativeLayout cantidadLayout;
    private RelativeLayout unidad2Layout;
    private RelativeLayout unidadLayout;
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
    private ArrayList<Object> requiredElements = new ArrayList<>();
    private AppCompatEditText unidad2;
    private RelativeLayout otraMonedaLayout;
    private boolean loadingBarcode = false;
    private HashMap<String, Object> itemsBarcode;
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
    private AppCompatEditText otroSabor;
    private AppCompatEditText otraFragancia;
    private AppCompatEditText otraVariedad;
    private AppCompatEditText otroTipo;
    private AppCompatEditText otraMoneda;
    private AppCompatEditText gasto;
    private AppCompatEditText peso;
    private AppCompatEditText cantidad;
    private AppCompatEditText barcode;
    private String codCat;
    private String codPresentacion;
    private String codMarca;
    private String codMedida;
    private HashMap<String, Object> compraData;
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
    private ArrayList<HashMap<String, Object>> itemsSabor = new ArrayList<HashMap<String, Object>>();
    private ArrayList<HashMap<String, Object>> itemsVariedad = new ArrayList<HashMap<String, Object>>();
    private ArrayList<HashMap<String, Object>> itemsFragancia = new ArrayList<HashMap<String, Object>>();
    private ArrayList<HashMap<String, Object>> itemsCategorias = new ArrayList<HashMap<String, Object>>();
    private ArrayList<HashMap<String, Object>> itemsMarcas = new ArrayList<HashMap<String, Object>>();
    private ArrayList<HashMap<String, Object>> itemsTipo = new ArrayList<HashMap<String, Object>>();
    private ArrayList<HashMap<String, Object>> itemsMedida = new ArrayList<HashMap<String, Object>>();
    private ArrayList<HashMap<String, Object>> itemsPresentacion  = new ArrayList<HashMap<String, Object>>();

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
        //codeTxt.setVisibility(View.VISIBLE);
        TextView legend = (TextView) findViewById(R.id.legend);
        //legend.setVisibility(View.VISIBLE);
        Button escanear = (Button) findViewById(R.id.escanear);
        //escanear.setVisibility(View.VISIBLE);

        //loadProducts("7591002000745");

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

                            String barcodeResponse = data.getStringExtra("barcode");
                            barcode.setText(barcodeResponse);
                            if (loadedCats == true) {
                                loadProducts(barcodeResponse);
                            }


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
        pesoLayout.setVisibility(View.GONE);
        presentacionLayout = (RelativeLayout) findViewById(R.id.presentacionLayout);
        //presentacionLayout.setVisibility(View.GONE);
        marcaLayout = (RelativeLayout) findViewById(R.id.marcaLayout);
        marcaLayout.setVisibility(View.GONE);
        otraMarcaLayout = (RelativeLayout) findViewById(R.id.otraMarcaLayout);
        otraMarcaLayout.setVisibility(View.GONE);
        gastoLayout = (LinearLayout) findViewById(R.id.gastoLayout);
        //gastoLayout.setVisibility(View.GONE);

        tipoContainer = (LinearLayout) findViewById(R.id.tipoContainer);
        tipoContainer.setVisibility(View.GONE);
        otroTipoLayout = (RelativeLayout) findViewById(R.id.otroTipoLayout);
        otroTipoLayout.setVisibility(View.GONE);
        otraMonedaLayout = (RelativeLayout) findViewById(R.id.otraMonedaLayout);
        otraMonedaLayout.setVisibility(View.GONE);
        saborContainer = (LinearLayout) findViewById(R.id.saborContainer);
        saborContainer.setVisibility(View.GONE);
        otroSaborLayout = (RelativeLayout) findViewById(R.id.otroSaborLayout);
        otroSaborLayout.setVisibility(View.GONE);
        fraganciaContainer = (LinearLayout) findViewById(R.id.fraganciaContainer);
        fraganciaContainer.setVisibility(View.GONE);
        otraFraganciaLayout = (RelativeLayout) findViewById(R.id.otraFraganciaLayout);
        otraFraganciaLayout.setVisibility(View.GONE);
        variedadContainer = (LinearLayout) findViewById(R.id.variedadContainer);
        variedadContainer.setVisibility(View.GONE);
        otraVariedadLayout = (RelativeLayout) findViewById(R.id.otraVariedadLayout);
        otraVariedadLayout.setVisibility(View.GONE);
        fabricanteLayout = (RelativeLayout) findViewById(R.id.fabricanteLayout);
        fabricanteLayout.setVisibility(View.GONE);
        cantidadLayout = (RelativeLayout) findViewById(R.id.cantidadLayout);
        cantidadLayout.setVisibility(View.GONE);
        codBarrasLayout = (LinearLayout) findViewById(R.id.codBarrasLayout);
        codBarrasLayout.setVisibility(View.VISIBLE);

        unidadLayout = (RelativeLayout) findViewById(R.id.unidadLayout);
        unidad2Layout = (RelativeLayout) findViewById(R.id.unidad2Layout);
        unidadLayout.setVisibility(View.VISIBLE);
        unidad2Layout.setVisibility(View.GONE);

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
        otroSabor = (AppCompatEditText) findViewById(R.id.otroSabor);
        otraFragancia = (AppCompatEditText) findViewById(R.id.otraFragancia);
        otraVariedad = (AppCompatEditText) findViewById(R.id.otraVariedad);
        otroTipo = (AppCompatEditText) findViewById(R.id.otroTipo);
        otraMoneda = (AppCompatEditText) findViewById(R.id.otraMoneda);

        gasto = (AppCompatEditText) findViewById(R.id.gasto);
        peso = (AppCompatEditText) findViewById(R.id.peso);
        cantidad = (AppCompatEditText) findViewById(R.id.cantidad);
        unidad2 = (AppCompatEditText) findViewById(R.id.unidad2);

        setPlaceholders();

        if (args.getInt("editando") == 1) {
            editando = 1;
            prodIndex = args.getInt("prodIndex");
            titulo.setText("Editar Producto");
            titulo2.setText("Editar Producto");

            gasto.setText(compraData.get("gasto").toString());
            Log.d("TAG", compraData.get("gasto").toString());
            otraMarca.setText(compraData.get("otraMarca").toString());
            otraFragancia.setText(compraData.get("otraFragancia").toString());
            otroSabor.setText(compraData.get("otroSabor").toString());
            otraVariedad.setText(compraData.get("otraVariedad").toString());
            otroTipo.setText(compraData.get("otroTipo").toString());
            otraMoneda.setText(compraData.get("otraMoneda").toString());
            cantidad.setText(compraData.get("cantidad").toString());
            peso.setText(compraData.get("peso").toString());
            fabricante.setText(compraData.get("fabricante").toString());

            unidad2.setText(compraData.get("unidad2").toString());

            barcode.setText(compraData.get("barcode").toString());

            /*codeTxt.setVisibility(View.VISIBLE);
            legend.setVisibility(View.VISIBLE);
            escanear.setVisibility(View.VISIBLE);*/
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

    private void setPlaceholders() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, new String[]{"Categoria"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(adapter);

        adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, new String[]{"Presentación/Tamaño"});
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
            dia.setText("LUNES");
        }
        if (compraData.get("day").toString().equals("2")) {
            dia.setText("MARTES");
        }
        if (compraData.get("day").toString().equals("3")) {
            dia.setText("MIERCOLES");
        }
        if (compraData.get("day").toString().equals("4")) {
            dia.setText("JUEVES");
        }
        if (compraData.get("day").toString().equals("5")) {
            dia.setText("VIERNES");
        }
        if (compraData.get("day").toString().equals("6")) {
            dia.setText("SABADO");
        }
        if (compraData.get("day").toString().equals("7")) {
            dia.setText("DOMINGO");
        }

        TextView semana = (TextView) findViewById(R.id.semana);
        semana.setText(compraData.get("week").toString());

        TextView tipoNegocio = (TextView) findViewById(R.id.tipoNegocio);
        tipoNegocio.setText(compraData.get("tipoNegocio").toString());

        TextView nombre = (TextView) findViewById(R.id.nombre);
        nombre.setText(compraData.get("nombre").toString());

        TextView estado = (TextView) findViewById(R.id.estado);
        estado.setText(compraData.get("estadoCompra").toString());

        TextView municipio = (TextView) findViewById(R.id.municipio);
        municipio.setText(compraData.get("municipioCompra").toString());

        TextView barrio = (TextView) findViewById(R.id.barrio);
        barrio.setText(compraData.get("barrioCompra").toString());
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private void saveProd(Integer action) {
        Boolean validationPassed = true;
        for (Object element : requiredElements) {
            if (element instanceof AppCompatEditText) {
                if (isEmpty((AppCompatEditText) element)) {
                    validationPassed = false;
                }
            }
            if (element instanceof SearchableSpinner) {
                Log.d("TAG", String.valueOf(((SearchableSpinner) element).getSelectedItemPosition()));
                if (((SearchableSpinner) element).getSelectedItemPosition() == 0) {
                    validationPassed = false;
                }
            }
        }

        if (validationPassed == false) {
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(NewProduct.this)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setTitle("Atención")
                    .setMessage("Por favor ingrese todos los campos.")
                    .addButton("OK", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                        dialog.dismiss();
                    });
            builder.show();
        } else {
            HashMap<String, Object> dato = new HashMap<>();
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
            dato.put("unidad2", unidad2.getText().toString());

            dato.put("fabricante", fabricante.getText().toString());
            dato.put("tipo", tipoSt);
            dato.put("otroTipo", otroTipo.getText().toString());
            dato.put("otraMoneda", otraMoneda.getText().toString());
            dato.put("fragancia", fraganciaSt);
            dato.put("otraFragancia", otraFragancia.getText().toString());
            dato.put("variedad", variedadSt);
            dato.put("otraVariedad", otraVariedad.getText().toString());
            dato.put("sabor", saborSt);
            dato.put("otroSabor", otroSabor.getText().toString());

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
                Log.d("TAG_COMPRA", String.valueOf(compras));
                startActivity(intent);
            }
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
        requiredElements = new ArrayList<>();

        if (marca.equals("1")) {
            marcaLayout.setVisibility(View.VISIBLE);
            requiredElements.add(marcas);
        } else {
            marcaLayout.setVisibility(View.GONE);
        }
        if (tipo.equals("1")) {
            tipoContainer.setVisibility(View.VISIBLE);
            requiredElements.add(this.tipo);
            getTipo(codCat);
        } else {
            tipoContainer.setVisibility(View.GONE);
        }
        if (sabor.equals("1")) {
            saborContainer.setVisibility(View.VISIBLE);
            requiredElements.add(this.sabor);
            getSabor(codCat);
        } else {
            saborContainer.setVisibility(View.GONE);
        }
        if (fragancia.equals("1")) {
            fraganciaContainer.setVisibility(View.VISIBLE);
            requiredElements.add(this.fragancia);
            getFragancia(codCat);
        } else {
            fraganciaContainer.setVisibility(View.GONE);
        }
        if (variedad.equals("1")) {
            variedadContainer.setVisibility(View.VISIBLE);
            requiredElements.add(this.variedad);
            getVariedad(codCat);
        } else {
            variedadContainer.setVisibility(View.GONE);
        }
        if (fabricante.equals("1")) {
            fabricanteLayout.setVisibility(View.VISIBLE);
            requiredElements.add(this.fabricante);
        } else {
            fabricanteLayout.setVisibility(View.GONE);
        }
        if (cantidad.equals("1")) {
            cantidadLayout.setVisibility(View.VISIBLE);
            requiredElements.add(this.cantidad);
        } else {
            cantidadLayout.setVisibility(View.GONE);
        }
        if (costo.equals("1")) {
            gastoLayout.setVisibility(View.VISIBLE);
            requiredElements.add(this.gasto);
            requiredElements.add(this.moneda);
        } else {
            gastoLayout.setVisibility(View.GONE);
        }
        if (codBarras.equals("1")) {
            codBarrasLayout.setVisibility(View.VISIBLE);
        } else {
            codBarrasLayout.setVisibility(View.VISIBLE);
        }

    }

    private void resetData() {
        String empty = "";
        presentacionSt = empty;
        pesoSt = empty;
        marcaSt = empty;
        tipoSt = empty;
        fraganciaSt = empty;
        saborSt = empty;
        variedadSt = empty;

        codPresentacion = null;
        presentacion.setSelection(0);
        codMarca = null;
        marcas.setSelection(0);
        monedaSt = 0;
        moneda.setSelection(0);

        gasto.setText(empty);

        unidad.setSelection(0);
        medidaSt = empty;
        unidad2.setText(empty);

        fabricante.setText(empty);
        tipo.setSelection(0);
        fragancia.setSelection(0);
        sabor.setSelection(0);
        variedad.setSelection(0);
        otraVariedad.setText(empty);
        otroTipo.setText(empty);
        otraMoneda.setText(empty);
        otroSabor.setText(empty);
        otraFragancia.setText(empty);

        cantidad.setText(empty);

    }

    private void loadProducts(String barcode) {
        // startLoader();
        HashMap<String, String> params = new HashMap<>();
        params.put("barcode", barcode);
        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    JSONObject resultObj = resultado.getJSONObject(0);
                    itemsBarcode = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                    Log.d("BARCODE_DATA", new Gson().toJson(itemsBarcode));

                    if (itemsBarcode.get("codCat") != null) {
                        int indexCats = -1;
                        Integer targetCodCat = Integer.valueOf(itemsBarcode.get("codCat").toString());
                        for (int i = 0; i < itemsCategorias.size(); i++) {
                            HashMap<String, Object> map = itemsCategorias.get(i);
                            if (Double.parseDouble(map.get("codCat").toString()) == Double.parseDouble(String.valueOf(targetCodCat))) {
                                indexCats = i;
                                break;
                            }
                        }

                        if (indexCats != -1) {
                            categoria.setSelection(indexCats);
                            loadingBarcode = true;
                        }
                    }
                }
            }
        }, "http://skill-ca.com/api/barcode.php", params);
    }

    private void checkLoader() {
        if (loadingBarcode == true) {

            Log.d("LOADED1", "TIPO " + String.valueOf(loadedTipo));
            Log.d("LOADED1", "FRAG " + String.valueOf(loadedFragancias));
            Log.d("LOADED1", "VARIEDAD " + String.valueOf(loadedVariedad));
            Log.d("LOADED1", "SABOR " + String.valueOf(loadedSabor));
            Log.d("LOADED1", "PRES " + String.valueOf(loadedPresentacion));
            Log.d("LOADED1", "MARCAS " + String.valueOf(loadedMarcas));
            Log.d("LOADED1", "===");

            if (loadedTipo && loadedFragancias && loadedVariedad && loadedSabor && loadedPresentacion && loadedMarcas) {

                // AUTOSELECT MARCAS
                if (itemsBarcode.get("codMarca") != null) {
                    Integer targetCodMarca = Integer.valueOf(itemsBarcode.get("codMarca").toString());
                    int indexMarcas = -1;
                    for (int i = 0; i < itemsMarcas.size(); i++) {
                        HashMap<String, Object> map = itemsMarcas.get(i);
                        if (map.get("codMarca") != null) {
                            if (Integer.parseInt(map.get("codMarca").toString()) == targetCodMarca) {
                                indexMarcas = i;
                                break;
                            }
                        }
                    }
                    if (indexMarcas != -1) {
                        marcas.setSelection(indexMarcas);
                    }
                }

                // AUTOSELECT MARCAS
                if (itemsBarcode.get("codPresentacion") != null) {
                    Integer targetCodPres = Integer.valueOf(itemsBarcode.get("codPresentacion").toString());
                    int indexPresentacion = -1;
                    for (int i = 0; i < itemsPresentacion.size(); i++) {
                        HashMap<String, Object> map = itemsPresentacion.get(i);
                        if (map.get("codPresentacion") != null) {
                            if (Integer.parseInt(map.get("codPresentacion").toString()) == targetCodPres) {
                                indexPresentacion = i;
                                break;
                            }
                        }
                    }
                    if (indexPresentacion != -1) {
                        presentacion.setSelection(indexPresentacion);
                    }
                }

                // AUTOSELECT TIPO
                if (itemsBarcode.get("codTipo") != null) {
                    Integer targetCodTipo = Integer.valueOf(itemsBarcode.get("codTipo").toString());

                    int indexTipo = -1;
                    for (int i = 0; i < itemsTipo.size(); i++) {
                        HashMap<String, Object> map = itemsTipo.get(i);
                        if (map.get("codTipo") != null) {
                            if (Integer.parseInt(map.get("codTipo").toString()) == targetCodTipo) {
                                indexTipo = i;
                                break;
                            }
                        }
                    }
                    if (indexTipo != -1) {
                        tipo.setSelection(indexTipo);
                    }
                }

                // AUTOSELECT UNIDADES
                if (itemsBarcode.get("codUnidad") != null) {
                    Integer targetCodUnidad = Integer.valueOf(itemsBarcode.get("codUnidad").toString());

                    int indexUnidad = -1;
                    for (int i = 0; i < itemsMedida.size(); i++) {
                        HashMap<String, Object> map = itemsMedida.get(i);
                        if (map.get("codMedida") != null) {
                            if (Integer.parseInt(map.get("codMedida").toString()) == targetCodUnidad) {
                                indexUnidad = i;
                                break;
                            }
                        }
                    }
                    if (indexUnidad != -1) {
                        unidad.setSelection(indexUnidad);
                    }
                }

                // AUTOSELECT UNIDADES
                Log.d("BARCODE_DATA", String.valueOf(itemsBarcode.get("codSabor")));
                if (itemsBarcode.get("codSabor") != null) {
                    Integer targetCodSabor = Integer.valueOf(itemsBarcode.get("codSabor").toString());
                    Log.d("BARCODE_DATA", String.valueOf(targetCodSabor));
                    int indexSabor = -1;
                    for (int i = 0; i < itemsSabor.size(); i++) {
                        HashMap<String, Object> map = itemsSabor.get(i);
                        if (map.get("codSabor") != null) {
                            Log.d("BARCODE_DATA", String.valueOf(map.get("codSabor")));
                            if (Integer.parseInt(map.get("codSabor").toString()) == targetCodSabor) {
                                indexSabor = i;
                                break;
                            }
                        }
                    }
                    if (indexSabor != -1) {
                        sabor.setSelection(indexSabor);
                    }
                }

                // AUTOSELECT UNIDADES
                if (itemsBarcode.get("codVariedad") != null) {
                    Integer targetCodVariedad = Integer.valueOf(itemsBarcode.get("codVariedad").toString());

                    int indexVariedad = -1;
                    for (int i = 0; i < itemsVariedad.size(); i++) {
                        HashMap<String, Object> map = itemsVariedad.get(i);
                        if (map.get("codVariedad") != null) {
                            if (Integer.parseInt(map.get("codVariedad").toString()) == targetCodVariedad) {
                                indexVariedad = i;
                                break;
                            }
                        }
                    }
                    if (indexVariedad != -1) {
                        variedad.setSelection(indexVariedad);
                    }
                }

                // AUTOSELECT UNIDADES
                if (itemsBarcode.get("codFragancia") != null) {
                    Integer targetCodFragancia = Integer.valueOf(itemsBarcode.get("codFragancia").toString());

                    int indexFragancia = -1;
                    for (int i = 0; i < itemsFragancia.size(); i++) {
                        HashMap<String, Object> map = itemsFragancia.get(i);
                        if (map.get("codFragancia") != null) {
                            if (Integer.parseInt(map.get("codFragancia").toString()) == targetCodFragancia) {
                                indexFragancia = i;
                                break;
                            }
                        }
                    }
                    if (indexFragancia != -1) {
                        fragancia.setSelection(indexFragancia);
                    }
                }

                if (itemsBarcode.get("fabricante") != null) {
                    fabricante.setText(itemsBarcode.get("fabricante").toString());
                }
            }
        }
    }

    private void getCategorias() {
        loadedCats = false;
        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    itemsCategorias = new ArrayList<HashMap<String, Object>>();

                    itemsCategorias.add(setHint("codCat", "categoria"));
                    spinnerArray[0] = "Categoria (Producto)";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        itemsCategorias.add(item);
                        spinnerArray[i + 1] = (resultObj.get("categoria").toString());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categoria.setAdapter(adapter);
                    categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Log.d("CATEGORIA_SELECTED", "CATEGORIA_SELECTEED0");
                            if (++check > 0) {
                                if (position > 0) {
                                    if (((TextView) parent.getChildAt(0)) != null) {
                                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                    }
                                    Log.d("CATEGORIA_SELECTED", "CATEGORIA_SELECTEED");
                                    codCat = itemsCategorias.get(position).get("codCat").toString();
                                    categoriaSt = itemsCategorias.get(position).get("categoria").toString();

                                    String por_unidad = itemsCategorias.get(position).get("por_unidad").toString();
                                    String sin_presentacion = itemsCategorias.get(position).get("sin_presentacion").toString();

                                    showFields(itemsCategorias.get(position), codCat);

                                    if (editando == 1) {
                                        if (check > 1) {
                                            resetData();
                                        }
                                    } else {
                                        resetData();
                                    }

                                    if (sin_presentacion.equals("1")) {
                                        checkSpecialPermission("DETALLAD", por_unidad);
                                        presentacionLayout.setVisibility(View.GONE);
                                        presentacionSt = "";
                                        presentacion.setSelection(0);
                                    } else {
                                        presentacionLayout.setVisibility(View.VISIBLE);
                                        getPresentaciones(codCat, por_unidad);
                                    }

                                    getMarcas(codCat);
                                    getUnidades(codCat);
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    loadedCats = true;

                    if (editando == 1) {
                        String categoriaCompra = compraData.get("codCat").toString();
                        codCat = categoriaCompra;

                        String categoriaCompraName = compraData.get("categoria").toString();
                        categoriaSt = categoriaCompraName.toUpperCase();
                        int spinnerPosition = adapter.getPosition(categoriaSt);
                        categoria.setSelection(spinnerPosition);

                    }
                }
            }
        }, "http://skill-ca.com/api/categorias.php", new HashMap<>());
    }

    private void getPresentaciones(String idCat, String por_unidad) {
        HashMap<String, String> params = new HashMap<>();
        params.put("codCat", idCat);
        loadedPresentacion = false;
        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    if (resultado.length() == 0) {
                        presentacionLayout.setVisibility(View.GONE);
                        pesoLayout.setVisibility(View.VISIBLE);
                    }

                    itemsPresentacion = new ArrayList<HashMap<String, Object>>();
                    itemsPresentacion.add(setHint("codPresentacion", "presentacion"));

                    String[] spinnerArray = new String[resultado.length() + 1];
                    spinnerArray[0] = "Presentacion";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        itemsPresentacion.add(item);
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

                                codPresentacion = itemsPresentacion.get(position).get("codPresentacion").toString();
                                presentacionSt = itemsPresentacion.get(position).get("presentacion").toString();

                                if (itemsPresentacion.get(position).get("pesounidad") != null) {
                                    pesoSt = itemsPresentacion.get(position).get("pesounidad").toString();
                                } else {
                                    pesoSt = "";
                                }

                                checkSpecialPermission(spinnerArray[position], por_unidad);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    loadedPresentacion = true;
                    checkLoader();

                    if (editando == 1) {
                        String codPrese = compraData.get("codPresentacion").toString();
                        codPresentacion = codPrese;

                        String presentacionCompraName = compraData.get("presentacion").toString();
                        presentacionSt = presentacionCompraName.toUpperCase();
                        int spinnerPosition = adapter.getPosition(presentacionSt);
                        presentacion.setSelection(spinnerPosition);
                    }
                }
            }
        }, "http://skill-ca.com/api/presentaciones.php", params);
    }

    private void checkSpecialPermission(String selectedText, String por_unidad) {
        if (selectedText.contains("OTR")) {
            pesoLayout.setVisibility(View.VISIBLE);
            requiredElements.add(peso);
            requiredElements.add(unidad);

        } else {
            pesoLayout.setVisibility(View.GONE);
            requiredElements.remove(peso);
            requiredElements.remove(unidad);

            if (selectedText.contains("DETALLAD")) {
                pesoLayout.setVisibility(View.VISIBLE);

                // HAY CATEGORIAS QUE EN LUGAR DE PESO/UNIDAD MUESTRAN CANTIDAD
                if (por_unidad.equals("1")) {
                    pesoLayout.setVisibility(View.GONE);
                    requiredElements.remove(peso);
                    requiredElements.remove(unidad);

                    unidad2Layout.setVisibility(View.VISIBLE);
                    requiredElements.add(unidad2);
                } else {
                    pesoLayout.setVisibility(View.VISIBLE);
                    requiredElements.add(peso);
                    requiredElements.add(unidad);

                    unidad2Layout.setVisibility(View.GONE);
                    requiredElements.remove(unidad2);

                    /*medidaSt = "";
                    unidad.setSelection(0);
                    unidad2.setText("");*/
                }
            } else {
                pesoLayout.setVisibility(View.GONE);
                unidad2Layout.setVisibility(View.GONE);

                requiredElements.remove(peso);
                requiredElements.remove(unidad);
                requiredElements.remove(unidad2);
            }
        }
    }

    private void getUnidades(String idCat) {
        HashMap<String, String> params = new HashMap<>();
        params.put("idCat", idCat);
        loadedUnidades = false;
        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    itemsMedida = new ArrayList<HashMap<String, Object>>();

                    itemsMedida.add(setHint("codMedida", "medida"));
                    spinnerArray[0] = "Medida";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        itemsMedida.add(item);
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

                                codMedida = itemsMedida.get(position).get("codMedida").toString();
                                medidaSt = itemsMedida.get(position).get("medida").toString();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    loadedUnidades = true;
                    checkLoader();

                    if (editando == 1) {
                        if (compraData.containsKey("codMedida") == true) {
                            String codigo = compraData.get("codMedida").toString();
                            codMedida = codigo;
                            String titulo = compraData.get("medida").toString().toUpperCase();
                            medidaSt = titulo.toUpperCase();

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

        String[] spinnerArray = new String[5];
        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> hintItem0 = new HashMap<String, Object>();
        hintItem0.put("moneda", "Moneda");
        items.add(hintItem0);
        spinnerArray[0] = "Moneda";

        HashMap<String, Object> hintItem = new HashMap<String, Object>();
        hintItem.put("moneda", "BOLIVARES");
        items.add(hintItem);
        spinnerArray[1] = "BOLIVARES";

        HashMap<String, Object> hintItem2 = new HashMap<String, Object>();
        hintItem2.put("moneda", "DOLARES");
        items.add(hintItem2);
        spinnerArray[2] = "DOLARES";

        HashMap<String, Object> hintItem3 = new HashMap<String, Object>();
        hintItem3.put("moneda", "PESO COLOMBIANO");
        items.add(hintItem3);
        spinnerArray[3] = "PESO COLOMBIANO";

        HashMap<String, Object> hintItem4 = new HashMap<String, Object>();
        hintItem4.put("moneda", "OTRA");
        items.add(hintItem4);
        spinnerArray[4] = "OTRA";

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

                    if (spinnerArray[position].contains("OTR")) {
                        otraMonedaLayout.setVisibility(View.VISIBLE);
                        requiredElements.add(otraMoneda);
                    } else {
                        otraMonedaLayout.setVisibility(View.GONE);
                        requiredElements.remove(otraMoneda);
                        otraMoneda.setText("");
                    }
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
        loadedMarcas = false;
        Log.d("LOADING_MARCAS", "LOADING_MARCAS");
        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    itemsMarcas = new ArrayList<HashMap<String, Object>>();

                    itemsMarcas.add(setHint("codMarca", "marca"));
                    spinnerArray[0] = "Marca";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        itemsMarcas.add(item);
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

                                codMarca = itemsMarcas.get(position).get("codMarca").toString();
                                marcaSt = itemsMarcas.get(position).get("marca").toString();

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

                    loadedMarcas = true;
                    checkLoader();

                    if (editando == 1) {
                        if (compraData.get("codMarca") != null) {
                            String codigo = compraData.get("codMarca").toString();
                            codMarca = codigo;
                        }
                        if (compraData.get("marca") != null) {
                            String titulo = compraData.get("marca").toString().toUpperCase();
                            marcaSt = titulo.toUpperCase();
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

        loadedTipo = false;
        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    itemsTipo = new ArrayList<HashMap<String, Object>>();

                    itemsTipo.add(setHint("codTipo", "tipo"));
                    spinnerArray[0] = "Tipo";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        itemsTipo.add(item);
                        spinnerArray[i + 1] = (resultObj.get("tipo").toString()).toUpperCase();
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

                                codTipo = itemsTipo.get(position).get("codTipo").toString();
                                tipoSt = itemsTipo.get(position).get("tipo").toString();

                                if (spinnerArray[position].contains("OTR")) {
                                    otroTipoLayout.setVisibility(View.VISIBLE);
                                    requiredElements.add(otroTipo);
                                } else {
                                    otroTipoLayout.setVisibility(View.GONE);
                                    requiredElements.remove(otroTipo);
                                    otroTipo.setText("");
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    loadedTipo = true;
                    checkLoader();

                    if (editando == 1) {
                        Log.d("TAG", "EDITANDO_GET_TIPO");
                        if (compraData.get("codTipo") != null) {
                            String codigo = compraData.get("codTipo").toString();
                            codTipo = codigo;
                            Log.d("TAG", "CODTIPO: " + codTipo);
                        }
                        if (compraData.get("tipo") != null) {
                            String titulo = compraData.get("tipo").toString().toUpperCase();
                            tipoSt = titulo.toUpperCase();
                            Log.d("TAG", "tipoSt: " + tipoSt.toUpperCase());
                            int spinnerPosition = adapter.getPosition(tipoSt);
                            Log.d("TAG", "tipspinnerPositionoSt: " + String.valueOf(spinnerPosition));
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
        loadedSabor = false;
        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    itemsSabor = new ArrayList<HashMap<String, Object>>();

                    itemsSabor.add(setHint("codSabor", "sabor"));
                    spinnerArray[0] = "Sabor";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        itemsSabor.add(item);
                        spinnerArray[i + 1] = (resultObj.get("sabor").toString()).toUpperCase();
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

                                codSabor = itemsSabor.get(position).get("codSabor").toString();
                                saborSt = itemsSabor.get(position).get("sabor").toString();

                                if (spinnerArray[position].contains("OTR")) {
                                    otroSaborLayout.setVisibility(View.VISIBLE);
                                    requiredElements.add(otroSabor);
                                } else {
                                    otroSaborLayout.setVisibility(View.GONE);
                                    requiredElements.remove(otroSabor);
                                    otroSabor.setText("");
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    loadedSabor = true;
                    checkLoader();

                    if (editando == 1) {
                        if (compraData.get("codSabor") != null) {
                            String codigo = compraData.get("codSabor").toString();
                            codSabor = codigo;
                        }
                        if (compraData.get("sabor") != null) {
                            String titulo = compraData.get("sabor").toString().toUpperCase();
                            saborSt = titulo.toUpperCase();
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
        loadedVariedad = false;
        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    itemsVariedad = new ArrayList<HashMap<String, Object>>();

                    itemsVariedad.add(setHint("codVariedad", "variedad"));
                    spinnerArray[0] = "Variedad";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        itemsVariedad.add(item);
                        spinnerArray[i + 1] = (resultObj.get("variedad").toString()).toUpperCase();
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    variedad.setAdapter(adapter);
                    if (editando == 1) {
                        if (compraData.get("variedad") != null) {
                            String titulo = compraData.get("variedad").toString().toUpperCase();
                            variedadSt = titulo.toUpperCase();
                            int spinnerPosition = adapter.getPosition(titulo);
                            variedad.setSelection(spinnerPosition);
                        }
                    }
                    variedad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Log.d("TAG", "TIPO_SELECTED: " + position);
                            if (position > 0) {

                                if (((TextView) parent.getChildAt(0)) != null) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                }

                                codVariedad = itemsVariedad.get(position).get("codVariedad").toString();
                                variedadSt = itemsVariedad.get(position).get("variedad").toString();

                                if (spinnerArray[position].contains("OTR")) {
                                    otraVariedadLayout.setVisibility(View.VISIBLE);
                                    requiredElements.add(otraVariedad);
                                } else {
                                    otraVariedadLayout.setVisibility(View.GONE);
                                    requiredElements.remove(otraVariedad);
                                    otraVariedad.setText("");
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    loadedVariedad = true;
                    checkLoader();
                }
            }
        }, "http://skill-ca.com/api/variedades.php", params);
    }

    private void getFragancia(String idCat) {
        HashMap<String, String> params = new HashMap<>();
        params.put("codCat", idCat);
        loadedFragancias = false;
        getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    itemsFragancia = new ArrayList<HashMap<String, Object>>();

                    itemsFragancia.add(setHint("codFragancia", "fragancia"));
                    spinnerArray[0] = "Fragancia";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        itemsFragancia.add(item);
                        spinnerArray[i + 1] = (resultObj.get("fragancia").toString()).toUpperCase();
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

                                codFragancia = itemsFragancia.get(position).get("codFragancia").toString();
                                fraganciaSt = itemsFragancia.get(position).get("fragancia").toString();

                                if (spinnerArray[position].contains("OTR")) {
                                    otraFraganciaLayout.setVisibility(View.VISIBLE);
                                    requiredElements.add(otraFragancia);
                                } else {
                                    otraFraganciaLayout.setVisibility(View.GONE);
                                    requiredElements.remove(otraFragancia);
                                    otraFragancia.setText("");
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    loadedFragancias = true;
                    checkLoader();

                    if (editando == 1) {
                        if (compraData.get("codFragancia") != null) {
                            String codigo = compraData.get("codFragancia").toString();
                            codFragancia = codigo;
                        }
                        if (compraData.get("fragancia") != null) {
                            String titulo = compraData.get("fragancia").toString().toUpperCase();
                            fraganciaSt = titulo.toUpperCase();
                            int spinnerPosition = adapter.getPosition(titulo);
                            fragancia.setSelection(spinnerPosition);
                        }
                    }

                }
            }
        }, "http://skill-ca.com/api/fragancias.php", params);
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

    public interface VolleyCallBack {
        void onSuccess(JSONObject json) throws JSONException;
    }

}