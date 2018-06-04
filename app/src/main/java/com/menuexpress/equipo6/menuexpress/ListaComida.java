package com.menuexpress.equipo6.menuexpress;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Interface.ItemClickListener;
import com.menuexpress.equipo6.menuexpress.Model.Comida;
import com.menuexpress.equipo6.menuexpress.ViewHolder.ComidaViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import info.hoang8f.widget.FButton;

public class ListaComida extends AppCompatActivity {

    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Comida, ComidaViewHolder> adapter;
    private FirebaseRecyclerAdapter<Comida, ComidaViewHolder> searchAdapter;
    private List<String> listaSugerencia = new ArrayList<>();
    private MaterialSearchBar materialSearchBar;
    private TextView txtNombre;
    private RecyclerView recycler_comida;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference comida;
    private String categoriaId = "";

    //Variables admin
    private CounterFab counterFabComida;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private MaterialEditText adminEditNombreComida, adminEditDescripComida, adminEditPrecioComida, adminEditDescuentoComida;
    private FButton adminBtnSelectComida, adminBtnUploadComida;
    private Comida nuevaComida;
    private Uri saveUri;
    private RelativeLayout rootLayoutComida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comida);

        //Iniciarlizar firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        comida = firebaseDatabase.getReference("comida");

        //cargar comida
        recycler_comida = (RecyclerView) findViewById(R.id.reclycler_comida);
        recycler_comida.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_comida.setLayoutManager(layoutManager);

        //Variables admin
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        counterFabComida = (CounterFab) findViewById(R.id.counterFabComida);
        rootLayoutComida = (RelativeLayout) findViewById(R.id.rootLayoutComida);

        //Cargar funcionalidades de administrador
        if (Boolean.parseBoolean(Common.currentUser.getIsAdmin())) {
            counterFabComida.setVisibility(View.VISIBLE);
        } else {
            counterFabComida.setVisibility(View.GONE);
        }

        counterFabComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cargar funcionalidades de administrador
                if (Boolean.parseBoolean(Common.currentUser.getIsAdmin())) {
                    mostrarDialogo();
                }
            }
        });

        if (getIntent() != null)
            categoriaId = getIntent().getStringExtra("categoriaId");
        if (!categoriaId.isEmpty() && categoriaId != null) {
            if (Common.isConnectedToInternet(getBaseContext())) {
                cargarListaComida(categoriaId);
            } else {
                Toast.makeText(ListaComida.this, "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        materialSearchBar = (MaterialSearchBar) findViewById(R.id.busqueda);
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

    }

    private void cargarSugerencia() {
        comida.orderByChild("idCat").equalTo(categoriaId)
                .addValueEventListener(new ValueEventListener() {
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

    private void cargarListaComida(String categoriaId) {

        Query listaComidaCategoriaId = comida.orderByChild("idCat").equalTo(categoriaId);

        FirebaseRecyclerOptions<Comida> options = new FirebaseRecyclerOptions.Builder<Comida>()
                .setQuery(listaComidaCategoriaId, Comida.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Comida, ComidaViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ComidaViewHolder holder, int position, @NonNull final Comida model) {
                holder.nombre_comida.setText(model.getNombre());
                Picasso.with(ListaComida.this)
                        .load(model.getImagen())
                        .into(holder.imagen_comida);

                // final Comida local = model;

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(ListaComida.this, "" + local.getNombre(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ListaComida.this, DetallesComida.class);
                        intent.putExtra("comidaId", adapter.getRef(position).getKey());
                        startActivity(intent);
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

        adapter.notifyDataSetChanged(); //Actualiza los datos si cambian
        recycler_comida.setAdapter(adapter);

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

               /* boolean isExists = new Database(getBaseContext()).checarExisteComida(adapter.getRef(position).getKey(), Common.currentUser.getEmail());
                if (!isExists){

                }else{
                    new Database(getBaseContext()).incrementarCarrito(Common.currentUser.getEmail(), adapter.getRef(position).getKey());
                }*/

                final Comida local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(ListaComida.this, DetallesComida.class);
                        intent.putExtra("comidaId", searchAdapter.getRef(position).getKey());
                        startActivity(intent);
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

    //Admin ini ----------------------------------------------------------------------

    private void mostrarDialogo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ListaComida.this);
        alertDialog.setTitle("Agregar nueva comida");
        //alertDialog.setMessage("Llena toda la información");

        LayoutInflater inflater = this.getLayoutInflater();
        View admin_comida_layout = inflater.inflate(R.layout.admin_comida_layout, null);

        adminEditNombreComida = admin_comida_layout.findViewById(R.id.adminEditNombreComida);
        adminEditDescripComida = admin_comida_layout.findViewById(R.id.adminEditDescripComida);
        adminEditPrecioComida = admin_comida_layout.findViewById(R.id.adminEditPrecioComida);
        adminEditDescuentoComida = admin_comida_layout.findViewById(R.id.adminEditDescuentoComida);

        adminBtnSelectComida = admin_comida_layout.findViewById(R.id.adminBtnSelectComida);
        adminBtnUploadComida = admin_comida_layout.findViewById(R.id.adminBtnUploadComida);

        alertDialog.setView(admin_comida_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        adminBtnSelectComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirImagen();
            }
        });

        adminBtnUploadComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subirImagen();
            }
        });

        alertDialog.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (nuevaComida != null) {
                    comida.push().setValue(nuevaComida);
                    Snackbar.make(rootLayoutComida, "Comida: " + nuevaComida.getNombre() + " fue agregada", Snackbar.LENGTH_SHORT).show();
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();
            adminBtnSelectComida.setText("Imagen Selccionada");
        }
    }

    public void elegirImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccioar imagen"), Common.PICK_IMAGE_REQUEST);
    }

    public void subirImagen() {
        if (saveUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Subiendo...");
            progressDialog.show();

            String nomImagen = UUID.randomUUID().toString();
            final StorageReference imagenFolder = storageReference.child("images/" + nomImagen);
            imagenFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(ListaComida.this, "Subido correctamente", Toast.LENGTH_SHORT).show();
                    imagenFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            nuevaComida = new Comida(
                                    adminEditNombreComida.getText().toString(),
                                    uri.toString(),
                                    adminEditDescripComida.getText().toString(),
                                    adminEditPrecioComida.getText().toString(),
                                    categoriaId,
                                    adminEditDescuentoComida.getText().toString());
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ListaComida.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Subido " + progress + "%");
                        }
                    });
        }
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            mostrarDialogoActualizar(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {

            new AlertDialog.Builder(ListaComida.this)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Está segura que desea eliminar este producto?")
                    .setIcon(R.drawable.ic_delete_white_24dp)
                    .setPositiveButton("CONFIRMAR",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    eliminarComida(adapter.getRef(item.getOrder()).getKey());
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

    private void mostrarDialogoActualizar(final String key, final Comida item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ListaComida.this);
        alertDialog.setTitle("Actualizar categoría");

        final LayoutInflater inflater = this.getLayoutInflater();
        View admin_comida_layout = inflater.inflate(R.layout.admin_comida_layout, null);

        adminEditNombreComida = admin_comida_layout.findViewById(R.id.adminEditNombreComida);
        adminEditDescripComida = admin_comida_layout.findViewById(R.id.adminEditDescripComida);
        adminEditPrecioComida = admin_comida_layout.findViewById(R.id.adminEditPrecioComida);
        adminEditDescuentoComida = admin_comida_layout.findViewById(R.id.adminEditDescuentoComida);

        adminBtnSelectComida = admin_comida_layout.findViewById(R.id.adminBtnSelectComida);
        adminBtnUploadComida = admin_comida_layout.findViewById(R.id.adminBtnUploadComida);

        alertDialog.setView(admin_comida_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        adminEditNombreComida.setText(item.getNombre());
        adminEditDescripComida.setText(item.getDescripcion());
        adminEditPrecioComida.setText(item.getPrecio());
        adminEditDescuentoComida.setText(item.getDescuento());

        adminBtnSelectComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirImagen();
            }
        });

        adminBtnUploadComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarImagen(item);
            }
        });

        alertDialog.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setNombre(adminEditNombreComida.getText().toString());
                item.setDescripcion(adminEditDescripComida.getText().toString());
                item.setPrecio(adminEditPrecioComida.getText().toString());
                item.setDescuento(adminEditDescuentoComida.getText().toString());
                comida.child(key).setValue(item);
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

    public void cambiarImagen(final Comida item) {
        if (saveUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Subiendo...");
            progressDialog.show();

            String nomImagen = UUID.randomUUID().toString();
            final StorageReference imagenFolder = storageReference.child("images/" + nomImagen);
            imagenFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(ListaComida.this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
                    imagenFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setImagen(uri.toString());
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ListaComida.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Subiendo " + progress + "%");
                        }
                    });

        }
    }

    private void eliminarComida(String key) {
        comida.child(key).removeValue();
    }

    //Anmin fin ----------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        cargarListaComida(categoriaId);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            //userid = firebaseAuth.getCurrentUser().getUid();
            //usuario = FirebaseDatabase.getInstance().getReference().child("usuario");
            irAWelcome();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        // searchAdapter.stopListening();
    }

    public void irAWelcome() {
        Intent intent = new Intent(ListaComida.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
