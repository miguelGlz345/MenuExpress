package com.menuexpress.equipo6.menuexpress;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Interface.ItemClickListener;
import com.menuexpress.equipo6.menuexpress.Model.Solicitar;
import com.menuexpress.equipo6.menuexpress.ViewHolder.PedidoViewHolder;

public class PedidoEstado extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Solicitar, PedidoViewHolder> adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference solicitudes;

    //Variables Admin
    private MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_estado);

        //Iniciarlizar firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        solicitudes = firebaseDatabase.getReference("solicitud");

        recyclerView = (RecyclerView) findViewById(R.id.listaPedidos);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //if (getIntent() == null)
        cargarPedidos(Common.currentUser.getEmail());
        //else
        //  cargarPedidos(getIntent().getStringExtra("email"));
    }

    private void cargarPedidos(String email) {
        FirebaseRecyclerOptions<Solicitar> options;
        //Admin
        if (Boolean.parseBoolean(Common.currentUser.getIsAdmin())) {
            options = new FirebaseRecyclerOptions.Builder<Solicitar>()
                    .setQuery(solicitudes, Solicitar.class)
                    .build();
        } else {
            Query pedidobyUser = solicitudes.orderByChild("email").equalTo(email);

            options = new FirebaseRecyclerOptions.Builder<Solicitar>()
                    .setQuery(pedidobyUser, Solicitar.class)
                    .build();
        }

        adapter = new FirebaseRecyclerAdapter<Solicitar, PedidoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PedidoViewHolder holder, int position, @NonNull final Solicitar model) {
                holder.txtPedidoId.setText(String.format("Clave : %s", adapter.getRef(position).getKey()));
                holder.txtPedidoUsuario.setText(String.format("A nombre de : %s", model.getNombre()));
                holder.txtPedidoEstado.setText(String.format("Estado : %s", Common.convertirCodigoEstado(model.getEstado())));
                holder.txtPedidoFecha.setText(String.format("Fecha : %s", Common.getFecha(Long.parseLong(adapter.getRef(position).getKey()))));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(PedidoEstado.this, DetallesPedido.class);
                        Common.currentResquest = model;
                        intent.putExtra("pedidoId", adapter.getRef(position).getKey());
                        startActivity(intent);
                        //finish();
                    }
                });
            }

            @NonNull
            @Override
            public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pedido_layout, parent, false);
                return new PedidoViewHolder(itemView);
            }
        };
        adapter.startListening();

        adapter.notifyDataSetChanged(); //Actualiza los datos si cambian
        recyclerView.setAdapter(adapter);

    }

    //Admin ini ----------------------------------------------------------------------

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            mostrarDialogoActualizar(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {
            eliminarPedido(adapter.getRef(item.getOrder()).getKey());

        }
        return super.onContextItemSelected(item);
    }

    private void mostrarDialogoActualizar(String key, final Solicitar item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PedidoEstado.this);
        alertDialog.setTitle("Actualizar pedido");

        LayoutInflater inflater = this.getLayoutInflater();
        final View admin_pedido_layout = inflater.inflate(R.layout.admin_pedido_layout, null);

        spinner = (MaterialSpinner) admin_pedido_layout.findViewById(R.id.spinner);
        spinner.setItems("Colocado", "En proceso", "Completado");

        alertDialog.setView(admin_pedido_layout);

        final String localKey = key;
        alertDialog.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setEstado(String.valueOf(spinner.getSelectedIndex()));
                solicitudes.child(localKey).setValue(item);
            }
        });

        alertDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void eliminarPedido(String key) {
        solicitudes.child(key).removeValue();
    }

    //Anmin fin ----------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
