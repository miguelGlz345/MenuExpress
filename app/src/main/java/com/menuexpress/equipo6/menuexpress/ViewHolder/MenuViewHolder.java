package com.menuexpress.equipo6.menuexpress.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Interface.ItemClickListener;
import com.menuexpress.equipo6.menuexpress.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView txtMenuNombre;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MenuViewHolder(View itemView) {
        super(itemView);

        txtMenuNombre = (TextView) itemView.findViewById(R.id.menu_nombre);
        imageView = (ImageView) itemView.findViewById(R.id.menu_imagen);

        itemView.setOnClickListener(this);

        //Admin
        if (Boolean.parseBoolean(Common.currentUser.getIsAdmin())) {
            itemView.setOnCreateContextMenuListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    //Admin
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Seleccionar Opción");
        menu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        menu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}
