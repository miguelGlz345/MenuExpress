package com.menuexpress.equipo6.menuexpress;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Database.Database;
import com.menuexpress.equipo6.menuexpress.Helper.RecyclerItemTouchHelper;
import com.menuexpress.equipo6.menuexpress.Interface.RecyclerItemTouchHelperListener;
import com.menuexpress.equipo6.menuexpress.Model.Pedido;
import com.menuexpress.equipo6.menuexpress.Model.Solicitar;
import com.menuexpress.equipo6.menuexpress.ViewHolder.CarritoAdapter;
import com.menuexpress.equipo6.menuexpress.ViewHolder.CarritoViewHolder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import info.hoang8f.widget.FButton;

public class Carrito extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    private List<Pedido> carrito = new ArrayList<>();
    private CarritoAdapter adapter;
    public TextView txtTotalPrecio;
    private FButton btnRealizarPedido;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference solicitud;

    private RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        //Iniciarlizar firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        solicitud = firebaseDatabase.getReference("solicitud");

        recyclerView = (RecyclerView) findViewById(R.id.listaCarrito);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Swipe para borrar
        ItemTouchHelper.SimpleCallback itemTouchHelpersimpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelpersimpleCallback).attachToRecyclerView(recyclerView);

        txtTotalPrecio = (TextView) findViewById(R.id.txtTotal);
        btnRealizarPedido = (FButton) findViewById(R.id.btnRealizarPedido);

        btnRealizarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carrito.size() > 0)
                    showAlertDialog();
                else
                    Toast.makeText(Carrito.this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
            }
        });
        cargarListaComida();
    }

    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Carrito.this);
        alertDialog.setTitle("CONFIRMAR PEDIDO");
        alertDialog.setMessage("HORA A RECOGER");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        //Time picker
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.time_picker, null, false);
        final TimePicker myTimePicker = (TimePicker) view.findViewById(R.id.myTimePicker);

        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        final String f = String.valueOf(System.currentTimeMillis());

        //Si es si, se envia el pedido
        alertDialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                myTimePicker.setIs24HourView(true);
                String currentHourText = myTimePicker.getCurrentHour().toString();
                String currentMinuteText = myTimePicker.getCurrentMinute().toString();
                //Toast.makeText(Carrito.this, currentHourText + ":" + currentMinuteText, Toast.LENGTH_SHORT).show();;

                Solicitar solicitar = new Solicitar(
                        Common.currentUser.getNombre(),
                        Common.currentUser.getEmail(),
                        txtTotalPrecio.getText().toString(),
                        carrito,
                        "0",
                        Common.getFecha(Long.parseLong(f)),
                        currentHourText + ":" + currentMinuteText,
                        "0" + "_" + Common.currentUser.getEmail()
                );

                String folio = UUID.randomUUID().toString().toUpperCase().substring(0, 6);

                //Eviar a firebase
                //String num_pedido = String.valueOf(System.currentTimeMillis());
                solicitud.child(folio).setValue(solicitar);
                new Database(getBaseContext()).limpiarCarrito(Common.currentUser.getEmail());
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
        carrito = new Database(this).getCarrito(Common.currentUser.getEmail());
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
        //if (item.getTitle().equals(Common.DELETE)) {
        //  deleteCarrito(item.getOrder());
        //}
        return true;
    }

    private void deleteCarrito(int position) {
        carrito.remove(position);
        new Database(this).limpiarCarrito(Common.currentUser.getEmail());
        for (Pedido item : carrito)
            new Database(this).agregarACarrito(item);
        cargarListaComida();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CarritoViewHolder) {
            String nombre = ((CarritoAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getNombreComida();
            final Pedido deleteItem = ((CarritoAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removerDelCarrito(deleteItem.getIdComida(), Common.currentUser.getEmail());

            //Calcular el precio total
            int total = 0;
            List<Pedido> pedidos = new Database(getBaseContext()).getCarrito(Common.currentUser.getEmail());
            for (Pedido item : pedidos)
                total += (Integer.parseInt(item.getPrecio())) * (Integer.parseInt(item.getCantidad()));
            Locale locale = new Locale("es", "MX");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            txtTotalPrecio.setText(fmt.format(total));

            Snackbar snackBar = Snackbar.make(rootLayout, nombre + " removido del carrito", Snackbar.LENGTH_LONG);
            snackBar.setAction("Deshacer", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem, deleteIndex);
                    new Database(getBaseContext()).agregarACarrito(deleteItem);

                    //Calcular el precio total
                    int total = 0;
                    List<Pedido> pedidos = new Database(getBaseContext()).getCarrito(Common.currentUser.getEmail());
                    for (Pedido item : pedidos)
                        total += (Integer.parseInt(item.getPrecio())) * (Integer.parseInt(item.getCantidad()));
                    Locale locale = new Locale("es", "MX");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                    txtTotalPrecio.setText(fmt.format(total));
                }
            });
            snackBar.setActionTextColor(Color.YELLOW);
            snackBar.show();
        }
    }
}
