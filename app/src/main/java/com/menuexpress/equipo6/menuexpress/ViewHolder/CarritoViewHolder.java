package com.menuexpress.equipo6.menuexpress.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.menuexpress.equipo6.menuexpress.Interface.ItemClickListener;
import com.menuexpress.equipo6.menuexpress.R;

public class CarritoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txt_carrito_nombre, txt_carrito_precio;
    public ElegantNumberButton btn_cant_carro;
    public ImageView img_carrito;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    private ItemClickListener itemClickListener;

    public CarritoViewHolder(View itemView) {
        super(itemView);
        txt_carrito_nombre = (TextView) itemView.findViewById(R.id.carrito_item_nombre);
        txt_carrito_precio = (TextView) itemView.findViewById(R.id.carrito_item_precio);
        btn_cant_carro = (ElegantNumberButton) itemView.findViewById(R.id.btn_cant_carro);
        img_carrito = (ImageView) itemView.findViewById(R.id.img_carrito);
        view_background = (RelativeLayout) itemView.findViewById(R.id.view_bg);
        view_foreground = (LinearLayout) itemView.findViewById(R.id.view_fg);

    }

    public void setTxt_carrito_precio(TextView txt_carrito_precio) {
        this.txt_carrito_precio = txt_carrito_precio;
    }

    public void setTxt_carrito_nombre(TextView txt_carrito_nombre) {
        this.txt_carrito_nombre = txt_carrito_nombre;
    }

    @Override
    public void onClick(View v) {

    }

}

