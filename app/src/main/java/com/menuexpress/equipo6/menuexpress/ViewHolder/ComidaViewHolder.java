package com.menuexpress.equipo6.menuexpress.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.menuexpress.equipo6.menuexpress.Interface.ItemClickListener;
import com.menuexpress.equipo6.menuexpress.R;

public class ComidaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView nombre_comida;
    public ImageView imagen_comida;

    private ItemClickListener itemClickListener;

    public ComidaViewHolder(View itemView) {
        super(itemView);

        nombre_comida = (TextView) itemView.findViewById(R.id.comida_nombre);
        imagen_comida = (ImageView) itemView.findViewById(R.id.comida_imagen);

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
