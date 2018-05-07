package com.menuexpress.equipo6.menuexpress.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.menuexpress.equipo6.menuexpress.Carrito;
import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Database.Database;
import com.menuexpress.equipo6.menuexpress.Interface.ItemClickListener;
import com.menuexpress.equipo6.menuexpress.Model.Pedido;
import com.menuexpress.equipo6.menuexpress.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CarritoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView txt_carrito_nombre, txt_carrito_precio;
    public ElegantNumberButton btn_cant_carro;
    public ImageView img_carrito;

    private ItemClickListener itemClickListener;

    public CarritoViewHolder(View itemView) {
        super(itemView);
        txt_carrito_nombre = (TextView) itemView.findViewById(R.id.carrito_item_nombre);
        txt_carrito_precio = (TextView) itemView.findViewById(R.id.carrito_item_precio);
        btn_cant_carro = (ElegantNumberButton) itemView.findViewById(R.id.btn_cant_carro);
        img_carrito = (ImageView) itemView.findViewById(R.id.img_carrito);

        itemView.setOnCreateContextMenuListener(this);
    }

    public void setTxt_carrito_nombre(TextView txt_carrito_nombre) {
        this.txt_carrito_nombre = txt_carrito_nombre;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Seleccionar Opcion");
        menu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}

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
                new Database(carrito).actualizarDatabase(pedido);

                //Calcular el precio total
                int total = 0;
                List<Pedido> pedidos = new Database(carrito).getCarrito();
                for (Pedido item : pedidos)
                    total += (Integer.parseInt(pedido.getPrecio())) * (Integer.parseInt(item.getCantidad()));
                Locale locale = new Locale("es", "MX");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                carrito.txtTotalPrecio.setText(fmt.format(total));

                int subtotal = (Integer.parseInt(listData.get(position).getPrecio())) * (Integer.parseInt(pedidos.get(position).getCantidad()));
                holder.txt_carrito_precio.setText(fmt.format(subtotal));

            }
        });

        holder.txt_carrito_nombre.setText(listData.get(position).getNombreComida());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
