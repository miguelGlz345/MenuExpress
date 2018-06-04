package com.menuexpress.equipo6.menuexpress;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.menuexpress.equipo6.menuexpress.Interface.ItemClickListener;
import com.menuexpress.equipo6.menuexpress.Model.Comida;
import com.menuexpress.equipo6.menuexpress.ViewHolder.ComidaViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private FirebaseRecyclerAdapter<Comida, ComidaViewHolder> searchAdapter;
    private List<String> listaSugerencia = new ArrayList<>();
    private MaterialSearchBar materialSearchBar;

    private FirebaseRecyclerAdapter<Comida, ComidaViewHolder> adapter;
    private RecyclerView recycler_comida;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference comida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Iniciarlizar firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        comida = firebaseDatabase.getReference("comida");

        //cargar comida
        recycler_comida = (RecyclerView) findViewById(R.id.reclycler_search);
        recycler_comida.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_comida.setLayoutManager(layoutManager);

        //Search
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.search_All);
        materialSearchBar.setHint("Busca tu comida");
        cargarSugerencia();
        materialSearchBar.setLastSuggestions(listaSugerencia);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> sugerencia = new ArrayList<>();
                for (String busqueda : listaSugerencia) {
                    if (busqueda.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        sugerencia.add(busqueda);
                }
                materialSearchBar.setLastSuggestions(sugerencia);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //Cuando la barra de busqueda se cierra se restaura el adapter
                if (!enabled)
                    recycler_comida.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //Cuando se finaliza muestra el resultado
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        cargarTodaComida();
    }

    private void cargarTodaComida() {

        Query listaComidaCategoriaId = comida;

        FirebaseRecyclerOptions<Comida> options = new FirebaseRecyclerOptions.Builder<Comida>()
                .setQuery(listaComidaCategoriaId, Comida.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Comida, ComidaViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ComidaViewHolder holder, int position, @NonNull final Comida model) {
                holder.nombre_comida.setText(model.getNombre());
                Picasso.with(SearchActivity.this)
                        .load(model.getImagen())
                        .into(holder.imagen_comida);

                // final Comida local = model;

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(ListaComida.this, "" + local.getNombre(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SearchActivity.this, DetallesComida.class);
                        intent.putExtra("comidaId", adapter.getRef(position).getKey());
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @NonNull
            @Override
            public ComidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comida_item, parent, false);
                return new ComidaViewHolder(itemView);
            }
        };
        adapter.startListening();
        //adapter.notifyDataSetChanged(); //Actualiza los datos si cambian
        recycler_comida.setAdapter(adapter);

    }

    private void cargarSugerencia() {
        comida.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot posSnapshot : dataSnapshot.getChildren()) {
                    Comida item = posSnapshot.getValue(Comida.class);
                    listaSugerencia.add(item.getNombre()); //agrega el nombre a la lista de sugerencia
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startSearch(CharSequence text) {

        Query searchByName = comida.orderByChild("nombre").equalTo(text.toString());

        FirebaseRecyclerOptions<Comida> options = new FirebaseRecyclerOptions.Builder<Comida>()
                .setQuery(searchByName, Comida.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Comida, ComidaViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ComidaViewHolder holder, int position, @NonNull Comida model) {
                holder.nombre_comida.setText(model.getNombre());
                Picasso.with(getBaseContext()).load(model.getImagen())
                        .into(holder.imagen_comida);

                final Comida local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(SearchActivity.this, DetallesComida.class);
                        intent.putExtra("comidaId", searchAdapter.getRef(position).getKey());
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @NonNull
            @Override
            public ComidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comida_item, parent, false);
                return new ComidaViewHolder(itemView);
            }
        };
        searchAdapter.startListening();

        searchAdapter.notifyDataSetChanged(); //Actualiza los datos si cambian
        recycler_comida.setAdapter(searchAdapter);
    }

    @Override
    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        if (searchAdapter != null)
            searchAdapter.stopListening();
        super.onStop();
    }
}
