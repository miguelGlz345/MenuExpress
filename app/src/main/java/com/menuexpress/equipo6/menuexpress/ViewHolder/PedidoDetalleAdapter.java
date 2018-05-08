package com.menuexpress.equipo6.menuexpress.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.menuexpress.equipo6.menuexpress.Model.Pedido;
import com.menuexpress.equipo6.menuexpress.R;

import java.util.List;

class PedidoDetalleViewHolder extends RecyclerView.ViewHolder {

    public TextView nombre, cantidad, precio;

    public PedidoDetalleViewHolder(View itemView) {
        super(itemView);

        nombre = (TextView) itemView.findViewById(R.id.det_nom_comida);
        cantidad = (TextView) itemView.findViewById(R.id.det_cant_comida);
        precio = (TextView) itemView.findViewById(R.id.det_precio_comida);
    }
}

public class PedidoDetalleAdapter extends RecyclerView.Adapter<PedidoDetalleViewHolder> {
    List<Pedido> misPedidos;

    public PedidoDetalleAdapter(List<Pedido> misPedidos) {
        this.misPedidos = misPedidos;
    }

    @NonNull
    @Override
    public PedidoDetalleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pedido_detalle_layout, parent, false);
        return new PedidoDetalleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoDetalleViewHolder holder, int position) {
        Pedido pedido = misPedidos.get(position);
        holder.nombre.setText(String.format("Nombre : %s", pedido.getNombreComida()));
        holder.cantidad.setText(String.format("Cantidad : %s", pedido.getCantidad()));
        holder.precio.setText(String.format("Precio : %s", pedido.getPrecio()));
    }

    @Override
    public int getItemCount() {
        return misPedidos.size();
    }
}
