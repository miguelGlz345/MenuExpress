package com.menuexpress.equipo6.menuexpress.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.menuexpress.equipo6.menuexpress.Interface.ItemClickListener;
import com.menuexpress.equipo6.menuexpress.R;

public class PedidoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtPedidoId, txtPedidoEstado, txtPedidoUsuario;

    private ItemClickListener itemClickListener;

    public PedidoViewHolder(View itemView) {
        super(itemView);

        txtPedidoId = (TextView) itemView.findViewById(R.id.pedido_id);
        txtPedidoUsuario = (TextView) itemView.findViewById(R.id.pedido_usuario);
        txtPedidoEstado = (TextView) itemView.findViewById(R.id.pedido_estado);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
