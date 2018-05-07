package com.menuexpress.equipo6.menuexpress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.ViewHolder.PedidoDetalleAdapter;

public class DetallesPedido extends AppCompatActivity {

    TextView det_pedido_id, det_pedido_usuario, det_pedido_total;
    String pedido_id = "";
    RecyclerView det_listaComida;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_pedido);

        det_pedido_id = (TextView) findViewById(R.id.det_pedido_id);
        det_pedido_usuario = (TextView) findViewById(R.id.det_pedido_usuario);
        det_pedido_total = (TextView) findViewById(R.id.det_pedido_total);

        det_listaComida = (RecyclerView) findViewById(R.id.det_listaComida);
        det_listaComida.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        det_listaComida.setLayoutManager(layoutManager);

        if (getIntent() != null)
            pedido_id = getIntent().getStringExtra("pedidoId");

        det_pedido_id.setText(pedido_id);
        det_pedido_usuario.setText(Common.currentResquest.getNombre());
        det_pedido_total.setText(Common.currentResquest.getTotal());

        PedidoDetalleAdapter adapter = new PedidoDetalleAdapter(Common.currentResquest.getComidas());

        adapter.notifyDataSetChanged();
        det_listaComida.setAdapter(adapter);
    }
}
