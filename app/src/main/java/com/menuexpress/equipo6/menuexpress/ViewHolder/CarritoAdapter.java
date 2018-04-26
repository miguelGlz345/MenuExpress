package com.menuexpress.equipo6.menuexpress.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.menuexpress.equipo6.menuexpress.Interface.ItemClickListener;
import com.menuexpress.equipo6.menuexpress.Model.Pedido;
import com.menuexpress.equipo6.menuexpress.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CarritoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txt_carrito_nombre, txt_carrito_precio;
    public ImageView img_carrito_cant;

    private ItemClickListener itemClickListener;

    public CarritoViewHolder(View itemView) {
        super(itemView);
        txt_carrito_nombre = (TextView) itemView.findViewById(R.id.carrito_item_nombre);
        txt_carrito_precio = (TextView) itemView.findViewById(R.id.carrito_item_precio);
        img_carrito_cant = (ImageView) itemView.findViewById(R.id.carrito_item_cant);
    }

    public void setTxt_carrito_nombre(TextView txt_carrito_nombre) {
        this.txt_carrito_nombre = txt_carrito_nombre;
    }

    @Override
    public void onClick(View v) {

    }
}

public class CarritoAdapter extends RecyclerView.Adapter<CarritoViewHolder> {
    private List<Pedido> listData = new ArrayList<>();
    private Context context;

    public CarritoAdapter(List<Pedido> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.carrito_layout, parent, false);
        return new CarritoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        TextDrawable drawable = TextDrawable.builder()
                .buildRound("" + listData.get(position).getCantidad(), Color.RED);
        holder.img_carrito_cant.setImageDrawable(drawable);

        Locale locale = new Locale("es", "MX");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int precio = (Integer.parseInt(listData.get(position).getPrecio())) * (Integer.parseInt(listData.get(position).getCantidad()));
        holder.txt_carrito_precio.setText(fmt.format(precio));
        holder.txt_carrito_nombre.setText(listData.get(position).getNombreComida());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
