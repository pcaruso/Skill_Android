package com.carusoft.skill;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class NewPurchase extends AppCompatActivity {
    final Calendar calendario = Calendar.getInstance();
    int anio = calendario.get(Calendar.YEAR);
    int mes = calendario.get(Calendar.MONTH);
    int diaDelMes = calendario.get(Calendar.DAY_OF_MONTH);

    ArrayList<ClassNegocio> items = new ArrayList<>();

    private LottieAnimationView loader;
    private RelativeLayout overlay;

    private SearchableSpinner tipoNegocio;
    private SearchableSpinner municipios;
    private SearchableSpinner estados;
    private AppCompatEditText barrio;

    private AppCompatEditText fecha;

    private AppCompatEditText nombre;
    private AppCompatEditText lugar;
    private Integer codNegocio;
    private String tipoNegocioName;

    ArrayList<HashMap<String, Object>> compras;
    private DatePickerDialog dialogoFecha;
    private int editando = 0;
    private HashMap<String, Object> compraData;
    private String codEstado;
    private String estadoSt;
    private String codMunicipio;
    private String municipioSt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_purchase);
        getSupportActionBar().hide();

        if (compras == null) {
            compras = new ArrayList<HashMap<String, Object>>();
        }
        getTipoNegocios();
        getEstados();


        lugar = (AppCompatEditText) findViewById(R.id.lugar);
        nombre = (AppCompatEditText) findViewById(R.id.nombre);
        barrio = (AppCompatEditText) findViewById(R.id.barrioCompra);
        fecha = (AppCompatEditText) findViewById(R.id.fecha);

        Intent intent = getIntent();

        if (intent.getBundleExtra("BUNDLE") != null) {
            Bundle args = intent.getBundleExtra("BUNDLE");

            if (args.getString("editando") != null) {
                if (args.getString("editando").equals("1")) {
                    editando = 1;

                    TextView titulo = (TextView) findViewById(R.id.titulo);
                    titulo.setText("Editar Compra");

                    Button next = (Button) findViewById(R.id.next);
                    next.setText("Guardar");

                    compras = (ArrayList<HashMap<String, Object>>) args.getSerializable("compras");
                    Log.d("compras", String.valueOf(compras));

                    String compra = args.getString("compra");

                    compraData = new Gson().fromJson(compra, new TypeToken<HashMap<String, Object>>() {
                    }.getType());

                    String fechaSt = compraData.get("fecha").toString();
                    fecha.setText(fechaSt);

                    String nombreSt = compraData.get("nombre").toString();
                    nombre.setText(nombreSt);

                    /*String lugarSt = compraData.get("lugar").toString();
                    lugar.setText(lugarSt);*/

                    String barrioSt = compraData.get("barrioCompra").toString();
                    barrio.setText(barrioSt);

                }
            }
        }


        fecha.setFocusable(false);
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoFecha = new DatePickerDialog(NewPurchase.this, listenerDeDatePicker, anio, mes, diaDelMes);
                dialogoFecha.show();
            }
        });

        Button contacto = (Button) findViewById(R.id.contacto);
        contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{"panelskill@gmail.com"});
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, ""));

                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(NewPurchase.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        SharedPreferences mPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String idHogar = mPrefs.getString("idHogar", "");
        String grupo = mPrefs.getString("grupo", "");
        String municipio = mPrefs.getString("municipio", "");
        String estado = mPrefs.getString("estado", "");
        String ciudad = mPrefs.getString("ciudad", "");

        TextView idHogarTxt = (TextView) findViewById(R.id.idHogar);
        idHogarTxt.setText(idHogar);
        TextView grupoTxt = (TextView) findViewById(R.id.grupo);
        grupoTxt.setText(grupo);
        TextView municipioTxt = (TextView) findViewById(R.id.municipio);
        municipioTxt.setText(municipio);
        TextView estadoTxt = (TextView) findViewById(R.id.estado);
        estadoTxt.setText(estado);
        TextView ciudadTxt = (TextView) findViewById(R.id.ciudad);
        ciudadTxt.setText(ciudad);

        nombre = (AppCompatEditText) findViewById(R.id.nombre);
        fecha = (AppCompatEditText) findViewById(R.id.fecha);
        tipoNegocio = (SearchableSpinner) findViewById(R.id.tipoNegocio);
        municipios = (SearchableSpinner) findViewById(R.id.municipioCompra);
        estados = (SearchableSpinner) findViewById(R.id.estadoCompra);
        barrio = (AppCompatEditText) findViewById(R.id.barrioCompra);

        setPlaceholders();

        ClassNegocio hintNegocio = new ClassNegocio();
        hintNegocio.setIdNegocio(0);
        hintNegocio.setNombre("Tipo de Negocio");
        items.add(hintNegocio);

        ImageView logout = (ImageView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(NewPurchase.this)
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

        Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            private String dayOfTheWeek;
            private String year;
            private String weekYear;

            @Override
            public void onClick(View v) {

                if (!(fecha.getText().toString().isEmpty()) && (codNegocio != null) && (codMunicipio != null) && (codEstado != null) && !(nombre.getText().toString().isEmpty()) && !(barrio.getText().toString().isEmpty())) {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("fecha", fecha.getText().toString());

                    String dtStart = fecha.getText().toString();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = format.parse(dtStart);
                        Calendar cl = Calendar.getInstance();
                        cl.setTime(date);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("u");
                        dayOfTheWeek = dateFormat.format(date);
                        SimpleDateFormat dateFormat2 = new SimpleDateFormat("w");
                        weekYear = String.valueOf(Integer.parseInt(dateFormat2.format(date)) + 948);
                        year = (String) DateFormat.format("yyyy", date); // 2013

                        data.put("week", weekYear);
                        data.put("year", year);
                        data.put("day", dayOfTheWeek);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //data.put("lugar", lugar.getText().toString().toUpperCase());
                    data.put("nombre", nombre.getText().toString().toUpperCase());
                    data.put("codNegocio", codNegocio);
                    data.put("tipoNegocio", tipoNegocioName);
                    data.put("codMunicipio", codMunicipio);
                    data.put("municipioCompra", municipioSt);
                    data.put("codEstado", codEstado);
                    data.put("estadoCompra", estadoSt);
                    data.put("barrioCompra", barrio.getText().toString().toUpperCase());

                    SharedPreferences mPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();

                    String idHogar = mPrefs.getString("idHogar", "");
                    String grupo = mPrefs.getString("grupo", "");
                    String municipio = mPrefs.getString("municipio", "");
                    String estado = mPrefs.getString("estado", "");
                    String ciudad = mPrefs.getString("ciudad", "");

                    data.put("idHogar", idHogar);
                    data.put("grupo", grupo);
                    data.put("municipio", municipio);
                    data.put("estado", estado);
                    data.put("ciudad", ciudad);

                    compraData = data;

                    if (editando == 1) {
                        ArrayList<HashMap<String, Object>> comprasEdited = new ArrayList<>();

                        for (int i = 0; i < compras.size(); i++) {
                            HashMap<String, Object> purchase = (HashMap<String, Object>) compras.get(i);
                            Log.d("comprasEdited", String.valueOf(purchase));
                            purchase.put("fecha", dtStart);

                            purchase.put("week", weekYear);
                            purchase.put("year", year);
                            purchase.put("day", dayOfTheWeek);

                            purchase.put("lugar", lugar.getText().toString());
                            purchase.put("nombre", nombre.getText().toString());
                            purchase.put("codNegocio", codNegocio);
                            purchase.put("tipoNegocio", tipoNegocioName);

                            purchase.put("codMunicipio", codMunicipio);
                            purchase.put("municipioCompra", municipioSt);
                            purchase.put("codEstado", codEstado);
                            purchase.put("estadoCompra", estadoSt);
                            purchase.put("barrioCompra", barrio.getText().toString().toUpperCase());

                            comprasEdited.add(purchase);
                        }
                        Log.d("comprasEdited", String.valueOf(comprasEdited));
                        compras = comprasEdited;

                        Intent intent = new Intent(NewPurchase.this, FinishPurchase.class);
                        Bundle args = new Bundle();
                        args.putSerializable("compras", (Serializable) compras);
                        args.putString("compra", new Gson().toJson(compraData));
                        intent.putExtra("BUNDLE", args);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(NewPurchase.this, NewProduct.class);
                        Bundle args = new Bundle();
                        args.putSerializable("compras", (Serializable) compras);
                        args.putString("compra", new Gson().toJson(data));
                        intent.putExtra("BUNDLE", args);
                        startActivity(intent);
                    }
                } else {
                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(NewPurchase.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setTitle("Atención")
                            .setMessage("Por favor ingrese todos los campos.")
                            .addButton("OK", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                dialog.dismiss();
                            });
                    builder.show();
                }

            }
        });


    }

    private void setPlaceholders() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, new String[]{"Municipios"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        municipios.setAdapter(adapter);

    }

    private void getData(final NewProduct.VolleyCallBack callBack, String url, HashMap<String, String> params) {
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

                if (volleyError instanceof TimeoutError) {
                    // Log.d("VOLLey error", volleyError.getMessage());
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


    private DatePickerDialog.OnDateSetListener listenerDeDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int anio, int mes, int diaDelMes) {
            // Esto se llama cuando seleccionan una fecha. Nos pasa la vista, pero más importante, nos pasa:
            // El año, el mes y el día del mes. Es lo que necesitamos para saber la fecha completa
            String fechaSt = String.format(Locale.getDefault(), "%02d-%02d-%02d", anio, mes + 1, diaDelMes);

            fecha.setText(fechaSt);
        }
    };

    private HashMap<String, Object> setHint(String cod, String name) {
        HashMap<String, Object> hintItem = new HashMap<String, Object>();
        hintItem.put(cod, 0);
        hintItem.put(name, name);
        return hintItem;
    }

    private void getEstados() {
        getData(new NewProduct.VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                    items.add(setHint("codEstado", "estado"));
                    spinnerArray[0] = "Estado";

                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject resultObj = resultado.getJSONObject(i);
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        items.add(item);
                        spinnerArray[i + 1] = (resultObj.get("estado").toString());
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    estados.setAdapter(adapter);
                    estados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                if (((TextView) parent.getChildAt(0)) != null) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                }

                                codEstado = items.get(position).get("codEstado").toString();
                                estadoSt = items.get(position).get("estado").toString().toUpperCase();

                                getMunicipios(codEstado);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    if (editando == 1) {
                        String codEst = compraData.get("codEstado").toString();
                        codEstado = codEst;

                        estadoSt = compraData.get("estadoCompra").toString().toUpperCase();
                        int spinnerPosition = adapter.getPosition(estadoSt);
                        estados.setSelection(spinnerPosition);
                    }
                }
            }
        }, "http://skill-ca.com/api/estados.php", new HashMap<>());
    }

    private void getMunicipios(String estado) {
        HashMap<String, String> params = new HashMap<>();
        params.put("codEstado", estado);

        getData(new NewProduct.VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    String[] spinnerArray = new String[resultado.length() + 1];
                    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                    items.add(setHint("codMunicipio", "municipio"));
                    spinnerArray[0] = "Municipio";

                    for (int i = 0; i < resultado.length(); i++) {

                        JSONObject resultObj = resultado.getJSONObject(i);
                        Log.d("TAG", String.valueOf(resultObj.get("municipio")));
                        HashMap<String, Object> item = new Gson().fromJson(String.valueOf(resultObj), HashMap.class);
                        items.add(item);
                        spinnerArray[i + 1] = (resultObj.get("municipio").toString().toUpperCase());
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    municipios.setAdapter(adapter);
                    municipios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                if (((TextView) parent.getChildAt(0)) != null) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                }

                                codMunicipio = items.get(position).get("codMunicipio").toString();
                                municipioSt = items.get(position).get("municipio").toString().toUpperCase();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    if (editando == 1) {
                        String codMuni = compraData.get("codMunicipio").toString();
                        codMunicipio = codMuni;

                        municipioSt = compraData.get("municipioCompra").toString().toUpperCase();
                        int spinnerPosition = adapter.getPosition(municipioSt);
                        municipios.setSelection(spinnerPosition);
                    }
                }
            }
        }, "http://skill-ca.com/api/municipios.php", params);
    }


    private void getTipoNegocios() {
        getData(new NewProduct.VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject json) throws JSONException {
                if (json.getInt("code") == 1) {
                    JSONArray resultado = json.getJSONArray("result");

                    ClassNegocio neg = new ClassNegocio();
                    for (int i = 0; i < resultado.length(); i++) {
                        JSONObject tipoNeg = resultado.getJSONObject(i);
                        ClassNegocio tipoNegocio = new ClassNegocio();
                        tipoNegocio.setIdNegocio(Integer.parseInt(tipoNeg.get("codNegocio").toString()));
                        tipoNegocio.setNombre(tipoNeg.get("tipoNegocio").toString());
                        items.add(tipoNegocio);

                        if (editando == 1) {
                            tipoNegocioName = compraData.get("tipoNegocio").toString();
                            if (tipoNegocioName.equals(tipoNeg.get("tipoNegocio").toString())) {
                                neg = tipoNegocio;
                            }
                        }
                    }

                    ArrayAdapter<ClassNegocio> adapter =
                            new ArrayAdapter<ClassNegocio>(getApplicationContext(), R.layout.spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    tipoNegocio.setAdapter(adapter);
                    tipoNegocio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                codNegocio = ((ClassNegocio) items.get(position)).getIdNegocio();
                                tipoNegocioName = ((ClassNegocio) items.get(position)).getNombre();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    if (editando == 1) {
                        String codNeg = compraData.get("codNegocio").toString();
                        codNegocio = Math.toIntExact(Math.round(Double.parseDouble(String.valueOf(codNeg))));

                        tipoNegocioName = compraData.get("tipoNegocio").toString();
                        int spinnerPosition = adapter.getPosition(neg);
                        tipoNegocio.setSelection(spinnerPosition);
                    }
                }
            }
        }, "http://skill-ca.com/api/negocio.php", new HashMap<>());
    }

    private void logout() {

        SharedPreferences mPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.remove("idHogar").apply();
        prefsEditor.remove("grupo").apply();
        prefsEditor.remove("estado").apply();
        prefsEditor.remove("municipio").apply();
        prefsEditor.remove("ciudad").apply();

        Intent intent = new Intent(NewPurchase.this, SignInActivity.class);
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

    public void composeEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, "info@skill-ca.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Contacto desde Android app");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


}
