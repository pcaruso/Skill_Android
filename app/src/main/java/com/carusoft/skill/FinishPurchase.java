package com.carusoft.skill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FinishPurchase extends AppCompatActivity {

    private ArrayList<HashMap<String, Object>> compras;
    private HashMap<String, Object> compraData;
    private ProductsAdapter adapter;
    private LottieAnimationView loader;
    private RelativeLayout overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_purchase);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        compras = (ArrayList<HashMap<String, Object>>) args.getSerializable("compras");

        Log.d("compras", String.valueOf(new Gson().toJson(compras)));

        String compra = args.getString("compra");
        compraData = new Gson().fromJson(compra, new TypeToken<HashMap<String, Object>>() {}.getType());

        setHeaderInfo(compraData);

        setupProducts();

        Button finish = (Button) findViewById(R.id.finCompra);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoader();
                String packagesUrl = "http://skill-ca.com/api/salvar.php";
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.getCache().clear();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, packagesUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject json = null;
                        stopLoader();
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(FinishPurchase.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                .setTitle("Gracias!")
                                .setMessage("La compra se ha registrado correctamente.")
                                .addButton("OK", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                    dialog.dismiss();
                                    finish();
                                    Intent intent = new Intent(FinishPurchase.this, NewPurchase.class);
                                    startActivity(intent);

                                });
                        builder.show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Log.d("VOLLey error", volleyError.getMessage());
                        if (volleyError instanceof TimeoutError) {
                            stopLoader();
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<>();
                        String arrayList = new Gson().toJson(compras);
                        params.put("compras", String.valueOf(arrayList));
                        Log.d("FINAL_COMPRAS", String.valueOf(compras));
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

        });

        Button nuevoProd = (Button) findViewById(R.id.nuevoProd);
        nuevoProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinishPurchase.this, NewProduct.class);
                Bundle args = new Bundle();
                args.putSerializable("compras", (Serializable) compras);
                args.putString("compra", new Gson().toJson(compraData));
                intent.putExtra("BUNDLE", args);

                startActivity(intent);


            }
        });

        Button editarCompra = (Button) findViewById(R.id.editarCompra);
        editarCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FinishPurchase.this, NewPurchase.class);
                Bundle args = new Bundle();
                args.putSerializable("compras", (Serializable) compras);
                args.putString("compra", new Gson().toJson(compraData));
                args.putString("editando", "1");
                intent.putExtra("BUNDLE", args);
                startActivity(intent);


            }
        });



        ImageView logout = (ImageView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(FinishPurchase.this)
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

    private void setupProducts(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lista);
        recyclerView.setLayoutManager(layoutManager);
        Log.d("COMPRAS", String.valueOf(compras));
        adapter = new ProductsAdapter(getApplicationContext(), compras,this);
        adapter.setOnItemClickListener(new ProductsAdapter.onClickListner() {
            @Override
            public void onItemClick(int position, View v) {

            }
        });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void editProduct(Integer prodIndex){
        Log.d("PROD_INDEX", String.valueOf(prodIndex));

        Intent intent = new Intent(FinishPurchase.this, NewProduct.class);
        Bundle args = new Bundle();
        args.putSerializable("compras", (Serializable) compras);
        args.putString("compra", new Gson().toJson(compras.get(prodIndex)));
        args.putInt("prodIndex", prodIndex);
        args.putInt("editando", 1);
        intent.putExtra("BUNDLE", args);

        startActivity(intent);

    }

    private void logout(){

        SharedPreferences mPrefs = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.remove("idHogar").apply();
        prefsEditor.remove("grupo").apply();
        prefsEditor.remove("estado").apply();
        prefsEditor.remove("municipio").apply();
        prefsEditor.remove("ciudad").apply();

        finish();
        Intent intent = new Intent(FinishPurchase.this, SignInActivity.class);
        startActivity(intent);
    }

    private void startLoader() {
        loader = findViewById(R.id.animation_view);
        overlay = findViewById(R.id.overlay);
        overlay.setVisibility(View.VISIBLE);
        loader.setVisibility(View.VISIBLE);
        loader.setRenderMode(RenderMode.HARDWARE);
    }

    private void stopLoader(){
        loader.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
    }


}