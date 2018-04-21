package com.menuexpress.equipo6.menuexpress.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.menuexpress.equipo6.menuexpress.Interface.ItemClickListener;
import com.menuexpress.equipo6.menuexpress.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuNombre;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);

        txtMenuNombre = (TextView) itemView.findViewById(R.id.menu_nombre);
        imageView = (ImageView) itemView.findViewById(R.id.menu_imagen);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
