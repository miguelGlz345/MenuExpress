package com.menuexpress.equipo6.menuexpress;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Database.Database;
import com.menuexpress.equipo6.menuexpress.Model.Pedido;
import com.menuexpress.equipo6.menuexpress.Model.Solicitar;
import com.menuexpress.equipo6.menuexpress.ViewHolder.CarritoAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Carrito extends AppCompatActivity {

    private List<Pedido> carrito = new ArrayList<>();
    private CarritoAdapter adapter;
    public TextView txtTotalPrecio;
    private FButton btnRealizarPedido;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference resquest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        //Iniciarlizar firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        resquest = firebaseDatabase.getReference("solicitud");

        recyclerView = (RecyclerView) findViewById(R.id.listaCarrito);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrecio = (TextView) findViewById(R.id.txtTotal);
        btnRealizarPedido = (FButton) findViewById(R.id.btnRealizarPedido);

        btnRealizarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carrito.size() > 0)
                    showAlertDialog();
                else
                    Toast.makeText(Carrito.this, "El carrito está vacio", Toast.LENGTH_SHORT).show();
            }
        });
        cargarListaComida();
    }

    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Carrito.this);
        alertDialog.setTitle("CONFIRMACION");
        alertDialog.setMessage("CONFIRMAR PEDIDO");

        //final TextView edtDireccion = new EditText(Carrito.this);
        //edtDireccion.setText("Confirmar pedido");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        //edtDireccion.setLayoutParams(lp);
        //alertDialog.setView(edtDireccion);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Si es si, se envia el pedido
        alertDialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Solicitar solicitar = new Solicitar(
                        Common.currentUser.getNombre(),
                        Common.currentUser.getEmail(),
                        txtTotalPrecio.getText().toString(),
                        carrito,
                        "0"
                );

                //Eviar a firebase
                resquest.child(String.valueOf(System.currentTimeMillis())).setValue(solicitar);
                new Database(getBaseContext()).elmininarCarrito();
                Toast.makeText(Carrito.this, "Gracias, orden recibida", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void cargarListaComida() {
        carrito = new Database(this).getCarrito();
        adapter = new CarritoAdapter(carrito, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calcular el precio total
        int total = 0;
        for (Pedido pedido : carrito)
            total += (Integer.parseInt(pedido.getPrecio())) * (Integer.parseInt(pedido.getCantidad()));
        Locale locale = new Locale("es", "MX");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrecio.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)) {
            deleteCarrito(item.getOrder());
        }
        return true;
    }

    private void deleteCarrito(int position) {
        carrito.remove(position);
        new Database(this).elmininarCarrito();
        for (Pedido item : carrito)
            new Database(this).agregarCarrito(item);
        cargarListaComida();
    }
}
