package com.menuexpress.equipo6.menuexpress;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
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

    private Query pedidobyUser;
    private Query pedidobyEstado;

    //Variables Admin
    private MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_estado);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPedido);
        toolbar.setTitle("Pedidos");
        setSupportActionBar(toolbar);

        //Iniciarlizar firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        solicitudes = firebaseDatabase.getReference("solicitud");

        recyclerView = (RecyclerView) findViewById(R.id.listaPedidos);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // if (getIntent() == null) {
        cargarPedidos(Common.currentUser.getEmail(), "0");
        // } else {
        //     if (!Boolean.parseBoolean(Common.currentUser.getIsAdmin()))
        //cargarPedidos(getIntent().getStringExtra("email"));
        // }
    }

    private void cargarPedidos(String email, String estado) {
        FirebaseRecyclerOptions<Solicitar> options;
        //String estado = Common.currentResquest.getEstado();
        //Admin
        if (Boolean.parseBoolean(Common.currentUser.getIsAdmin())) {
            pedidobyEstado = solicitudes.orderByChild("estado").equalTo(estado);

            options = new FirebaseRecyclerOptions.Builder<Solicitar>()
                    .setQuery(pedidobyEstado, Solicitar.class)
                    .build();
        } else {
            //Query pedidobyUser = solicitudes.orderByChild("email").equalTo(email);
            pedidobyUser = solicitudes.orderByChild("estado_email").equalTo(estado + "_" + email);

            //Query pedidobyUser = solicitudes.orderByChild("fecha");
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
                holder.txtPedidoFecha.setText(String.format("Fecha : %s", model.getFecha()));
                holder.txtPedidoHora.setText(String.format("Recoger a las  : %s", model.getHora()));

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
    public boolean onContextItemSelected(final MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            mostrarDialogoActualizar(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {

            new AlertDialog.Builder(PedidoEstado.this)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Está segura que desea eliminar este producto?")
                    .setIcon(R.drawable.ic_delete_white_24dp)
                    .setPositiveButton("CONFIRMAR",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    eliminarPedido(adapter.getRef(item.getOrder()).getKey());
                                }
                            })
                    .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    }).show();
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
                item.setEstado_email(String.valueOf(spinner.getSelectedIndex()) + "_" + item.getEmail());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.pedido, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.colocado) {
            cargarPedidos(Common.currentUser.getEmail(), "0");
        } else if (item.getItemId() == R.id.en_proceso) {
            cargarPedidos(Common.currentUser.getEmail(), "1");
        } else if (item.getItemId() == R.id.completado) {
            cargarPedidos(Common.currentUser.getEmail(), "2");
        }

        return super.onOptionsItemSelected(item);
    }

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
