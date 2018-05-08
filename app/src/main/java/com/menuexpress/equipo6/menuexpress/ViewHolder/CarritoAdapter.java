package com.menuexpress.equipo6.menuexpress.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.menuexpress.equipo6.menuexpress.Carrito;
import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Database.Database;
import com.menuexpress.equipo6.menuexpress.Model.Pedido;
import com.menuexpress.equipo6.menuexpress.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoViewHolder> {
    private List<Pedido> listData = new ArrayList<>();
    private Carrito carrito;

    public CarritoAdapter(List<Pedido> listData, Carrito carrito) {
        this.listData = listData;
        this.carrito = carrito;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(carrito);
        View itemView = inflater.inflate(R.layout.carrito_layout, parent, false);

        return new CarritoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CarritoViewHolder holder, final int position) {

        Picasso.with(carrito.getBaseContext())
                .load(listData.get(position).getImagen())
                .resize(70, 70)
                .centerCrop()
                .into(holder.img_carrito);

        holder.btn_cant_carro.setNumber(listData.get(position).getCantidad());
        holder.btn_cant_carro.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {

            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Pedido pedido = listData.get(position);
                pedido.setCantidad(String.valueOf(newValue));
                new Database(carrito).actualizarCarrito(pedido);

                //Calcular el precio total
                int total = 0;
                List<Pedido> pedidos = new Database(carrito).getCarrito(Common.currentUser.getEmail());
                for (Pedido item : pedidos)
                    total += (Integer.parseInt(item.getPrecio())) * (Integer.parseInt(item.getCantidad()));

                Locale locale = new Locale("es", "MX");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                carrito.txtTotalPrecio.setText(fmt.format(total));
                int subtotal = (Integer.parseInt(listData.get(position).getPrecio())) * (Integer.parseInt(listData.get(position).getCantidad()));
                holder.txt_carrito_precio.setText(fmt.format(subtotal));
            }
        });
        Locale locale = new Locale("es", "MX");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int subtotal = (Integer.parseInt(listData.get(position).getPrecio())) * (Integer.parseInt(listData.get(position).getCantidad()));
        holder.txt_carrito_precio.setText(fmt.format(subtotal));
        holder.txt_carrito_nombre.setText(listData.get(position).getNombreComida());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public Pedido getItem(int position) {
        return listData.get(position);
    }

    public void removeItem(int position) {
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Pedido item, int position) {
        listData.add(position, item);
        notifyItemInserted(position);
    }
}
