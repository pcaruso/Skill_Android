package com.carusoft.skill;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;

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
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class NewPurchase extends AppCompatActivity {
    final Calendar calendario = Calendar.getInstance();
    int anio = calendario.get(Calendar.YEAR);
    int mes = calendario.get(Calendar.MONTH);
    int diaDelMes = calendario.get(Calendar.DAY_OF_MONTH);

    ArrayList<ClassNegocio> items = new ArrayList<>();

    private SearchableSpinner tipoNegocio;
    private AppCompatEditText fecha;
    private AppCompatEditText nombre;
    private AppCompatEditText lugar;
    private Integer codNegocio;
    private String tipoNegocioName;

    ArrayList<HashMap<String, Object>> compras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_purchase);
        getSupportActionBar().hide();

        if (compras == null){
            compras = new ArrayList<HashMap<String, Object>>();
        }
        getTipoNegocios();

        lugar = (AppCompatEditText) findViewById(R.id.lugar);
        nombre = (AppCompatEditText) findViewById(R.id.nombre);
        fecha = (AppCompatEditText) findViewById(R.id.fecha);
        fecha.setFocusable(false);
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialogoFecha = new DatePickerDialog(NewPurchase.this, listenerDeDatePicker, anio, mes, diaDelMes);
                dialogoFecha.show();
            }
        });


        tipoNegocio = (SearchableSpinner) findViewById(R.id.spinner);

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
            @Override
            public void onClick(View v) {

                if (!(fecha.getText().equals("")) && (codNegocio != null)  && !(nombre.getText().equals(""))  && !(lugar.getText().equals(""))) {


                    HashMap<String, Object> data = new HashMap<>();
                    data.put("fecha", fecha.getText().toString());
                    data.put("lugar", lugar.getText().toString());
                    data.put("nombre", nombre.getText().toString());
                    data.put("codNegocio", codNegocio);
                    data.put("tipoNegocio", tipoNegocioName);

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

                    compras.add(data);

                    Intent intent = new Intent(NewPurchase.this, NewProduct.class);
                    Bundle args = new Bundle();
                    args.putSerializable("compras", (Serializable) compras);
                    args.putString("compra", new Gson().toJson(data));
                    intent.putExtra("BUNDLE", args);

                    startActivity(intent);

                }else{
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

    private DatePickerDialog.OnDateSetListener listenerDeDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int anio, int mes, int diaDelMes) {
            // Esto se llama cuando seleccionan una fecha. Nos pasa la vista, pero más importante, nos pasa:
            // El año, el mes y el día del mes. Es lo que necesitamos para saber la fecha completa
            String fechaSt = String.format(Locale.getDefault(), "%02d-%02d-%02d", anio, mes+1, diaDelMes);

            fecha.setText(fechaSt);
        }
    };

    private void getTipoNegocios() {
        String packagesUrl = "http://skill-ca.com/api/negocio.php";

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
                        JSONArray tipoNegocios = json.getJSONArray("result");
                        for (int i = 0 ; i < tipoNegocios.length(); i++) {
                            JSONObject tipoNeg = tipoNegocios.getJSONObject(i);
                            ClassNegocio tipoNegocio = new ClassNegocio();
                            tipoNegocio.setIdNegocio(Integer.parseInt(tipoNeg.get("codNegocio").toString()));
                            tipoNegocio.setNombre(tipoNeg.get("tipoNegocio").toString());
                            items.add(tipoNegocio);
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
                                    codNegocio = ((ClassNegocio)items.get(position)).getIdNegocio();
                                    tipoNegocioName = ((ClassNegocio)items.get(position)).getNombre();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
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

    private void logout(){

        SharedPreferences mPrefs = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.remove("idHogar").apply();
        prefsEditor.remove("grupo").apply();
        prefsEditor.remove("estado").apply();
        prefsEditor.remove("municipio").apply();
        prefsEditor.remove("ciudad").apply();

        Intent intent = new Intent(NewPurchase.this, SignInActivity.class);
        startActivity(intent);
    }


}
