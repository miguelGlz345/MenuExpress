package com.menuexpress.equipo6.menuexpress.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Interface.ItemClickListener;
import com.menuexpress.equipo6.menuexpress.R;

public class PedidoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView txtPedidoId, txtPedidoEstado, txtPedidoUsuario, txtPedidoFecha, txtPedidoHora;

    private ItemClickListener itemClickListener;

    public PedidoViewHolder(View itemView) {
        super(itemView);

        txtPedidoId = (TextView) itemView.findViewById(R.id.pedido_id);
        txtPedidoUsuario = (TextView) itemView.findViewById(R.id.pedido_usuario);
        txtPedidoEstado = (TextView) itemView.findViewById(R.id.pedido_estado);
        txtPedidoFecha = (TextView) itemView.findViewById(R.id.pedido_fecha);
        txtPedidoHora = (TextView) itemView.findViewById(R.id.pedido_hora);

        itemView.setOnClickListener(this);

        //Admin
        if (Boolean.parseBoolean(Common.currentUser.getIsAdmin())) {
            itemView.setOnCreateContextMenuListener(this);
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Seleccionar Opción");
        menu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        menu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}
