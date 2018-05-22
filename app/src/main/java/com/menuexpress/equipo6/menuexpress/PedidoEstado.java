package com.menuexpress.equipo6.menuexpress;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Interface.ItemClickListener;
import com.menuexpress.equipo6.menuexpress.Model.MyResponse;
import com.menuexpress.equipo6.menuexpress.Model.Notificacion;
import com.menuexpress.equipo6.menuexpress.Model.Sender;
import com.menuexpress.equipo6.menuexpress.Model.Solicitar;
import com.menuexpress.equipo6.menuexpress.Model.Token;
import com.menuexpress.equipo6.menuexpress.Remote.APIService;
import com.menuexpress.equipo6.menuexpress.ViewHolder.PedidoViewHolder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoEstado extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Solicitar, PedidoViewHolder> adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference solicitudes;

    //Variables Admin
    private MaterialSpinner spinner;
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_estado);

        //Iniciar service
        mService = Common.getFCMService();

        //Iniciarlizar firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        solicitudes = firebaseDatabase.getReference("solicitud");

        recyclerView = (RecyclerView) findViewById(R.id.listaPedidos);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // if (getIntent() == null) {
        cargarPedidos(Common.currentUser.getEmail());
        // } else {
        //     if (!Boolean.parseBoolean(Common.currentUser.getIsAdmin()))
        //cargarPedidos(getIntent().getStringExtra("email"));
        // }
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
                holder.txtPedidoFecha.setText(String.format("Fecha : %s", model.getFecha()));

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

                enviarEstadoPedidoAUsuario(localKey, item);

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

    //Notificaciones
    private void enviarEstadoPedidoAUsuario(final String key, final Solicitar item) {
        DatabaseReference tokens = firebaseDatabase.getReference("tokens");
        tokens.orderByKey().equalTo(Common.currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Token token = postSnapShot.getValue(Token.class);

                    Notificacion notificacion = new Notificacion("MenuExpress", "Tu pedido " + key + " fue actualizado");
                    Sender content = new Sender(token.getToken(), notificacion);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.body().success == 1) {
                                        Toast.makeText(PedidoEstado.this, "El pedido fue actualizado", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(PedidoEstado.this, "El pedido fue actualizado pero falló enviar la notificación", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
