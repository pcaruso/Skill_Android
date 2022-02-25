package com.carusoft.skill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_purchase);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
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

        setupProducts();
        Button finish = (Button) findViewById(R.id.finCompra);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String packagesUrl = "http://skill-ca.com/api/salvar.php";
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.getCache().clear();
                //startLoader();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, packagesUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE_SALVAR", response);
                        JSONObject json = null;

                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(FinishPurchase.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                .setTitle("Gracias!")
                                .setMessage("La compra se ha registrado correctamente.")
                                .addButton("OK", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {
                                    dialog.dismiss();
                                });
                        builder.show();
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
                        String arrayList = new Gson().toJson(compras);
                        params.put("compras", String.valueOf(arrayList));
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
                args.putString("compra", new Gson().toJson(compra));
                intent.putExtra("BUNDLE", args);

                startActivity(intent);
            }
        });

    }

    private void setupProducts(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lista);
        recyclerView.setLayoutManager(layoutManager);
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
}