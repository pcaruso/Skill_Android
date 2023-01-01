package com.carusoft.skill;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private static onClickListner onclicklistner;
    FinishPurchase activity;
    //vars
    private ArrayList<HashMap<String, Object>> dataGlobal = new ArrayList<>();

    private Context mContext;

    public ProductsAdapter(Context context, ArrayList<HashMap<String, Object>> data, FinishPurchase actividad) {
        dataGlobal = data;
        mContext = context;
        activity = actividad;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        HashMap<String, Object> data = dataGlobal.get(position);

        holder.categoria.setText(data.get("categoria").toString());
        holder.presentacion.setText(data.get("presentacion").toString());
        holder.marca.setText(data.get("marca").toString());

        String weight = "";
        if ((data.get("peso") != null) && (data.get("medida") != null) ){
            weight = data.get("peso") + " " + data.get("medida");
        }
        holder.peso.setText(weight);

        if (data.get("moneda") != null){
            Integer moneda = Integer.parseInt(String.valueOf(data.get("moneda")));
            if (moneda == 0){
                holder.gasto.setText(data.get("gasto").toString() + " BOLIVARES");
            }else if (moneda == 0){
                holder.gasto.setText(data.get("gasto").toString() + " DOLARES");
            }else if (moneda == 0){
                holder.gasto.setText(data.get("gasto").toString() + " PSO COL");
            }else{
                if (data.get("otraMoneda") != null) {
                    holder.gasto.setText(data.get("gasto").toString() + " " + data.get("otraMoneda").toString());
                }else{
                    holder.gasto.setText(data.get("gasto").toString());
                }
            }
        }else{
            holder.gasto.setText(data.get("gasto").toString() + " BOLIVARES");
        }

        holder.cantidad.setText(data.get("cantidad").toString());
        holder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("EDITAR_PROD", "EDITAR_PROD");
                activity.editProduct(holder.getAbsoluteAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataGlobal.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {


        TextView categoria;
        TextView presentacion;
        TextView marca;
        TextView peso;
        TextView gasto;
        TextView cantidad;
        ImageView editar;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            categoria = itemView.findViewById(R.id.categoria);
            presentacion = itemView.findViewById(R.id.presentacion);
            marca = itemView.findViewById(R.id.marca);
            peso = itemView.findViewById(R.id.peso);
            gasto = itemView.findViewById(R.id.gasto);
            cantidad = itemView.findViewById(R.id.cantidad);
            editar = itemView.findViewById(R.id.editar);

        }

        @Override
        public void onClick(View v) {
            onclicklistner.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(onClickListner onclicklistner) {
        ProductsAdapter.onclicklistner = onclicklistner;
    }

    public interface onClickListner {
        void onItemClick(int position, View v);

    }



}
