package com.menuexpress.equipo6.menuexpress;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Database.Database;
import com.menuexpress.equipo6.menuexpress.Interface.ItemClickListener;
import com.menuexpress.equipo6.menuexpress.Model.Categoria;
import com.menuexpress.equipo6.menuexpress.Service.ListenPedido;
import com.menuexpress.equipo6.menuexpress.Service.ListenPedidoAdmin;
import com.menuexpress.equipo6.menuexpress.ViewHolder.MenuViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class Inicio extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Categoria, MenuViewHolder> adapter;
    private TextView txtNombreUsuario;
    private RecyclerView recycler_menu;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference category;
    private DatabaseReference usuario;
    private String userid;
    private CounterFab counterFabIni;

    //Variables admin
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private MaterialEditText adminEditNombre;
    private FButton adminBtnSelect, adminBtnUpload;
    private Categoria nuevaCategoria;
    private Uri saveUri;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //Iniciarlizar firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        category = firebaseDatabase.getReference("categoria");

        //Variables admin
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Inicializar Paper
        Paper.init(this);

        counterFabIni = (CounterFab) findViewById(R.id.counterFabIni);
        counterFabIni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Cargar funcionalidades de administrador
                if (Boolean.parseBoolean(Common.currentUser.getIsAdmin())) {
                    mostrarDialogo();
                } else {
                    Intent intent = new Intent(Inicio.this, Carrito.class);
                    startActivity(intent);
                }
            }
        });

        counterFabIni.setCount(new Database(this).getContCarrito(Common.currentUser.getEmail()));

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Colocar el nombre del usuario
        View headerView = navigationView.getHeaderView(0);
        txtNombreUsuario = (TextView) headerView.findViewById(R.id.textNombre);
        txtNombreUsuario.setText(Common.currentUser.getNombre());
        //+ " " + Common.currentUser.getAp_paterno() + " " + Common.currentUser.getAp_materno());

        //cargar menu
        recycler_menu = (RecyclerView) findViewById(R.id.reclycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        //Cargar funcionalidades de administrador
        if (Boolean.parseBoolean(Common.currentUser.getIsAdmin())) {
            counterFabIni.setImageResource(R.drawable.ic__add_white_24dp);
        } else {
            counterFabIni.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
        }

        Intent service = new Intent(Inicio.this, ListenPedido.class);
        startService(service);
        Intent serviceAdmin = new Intent(Inicio.this, ListenPedidoAdmin.class);
        startService(serviceAdmin);

        if (Common.isConnectedToInternet(this)) {
            cargarMenu();
        } else {
            Toast.makeText(Inicio.this, "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void cargarMenu() {
        FirebaseRecyclerOptions<Categoria> options = new FirebaseRecyclerOptions.Builder<Categoria>()
                .setQuery(category, Categoria.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Categoria, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Categoria model) {
                holder.txtMenuNombre.setText(model.getNombre());
                Picasso.with(Inicio.this).load(model.getImagen())
                        .into(holder.imageView);

                final Categoria clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Se obtiene el id de categoría
                        Intent intent = new Intent(Inicio.this, ListaComida.class);
                        intent.putExtra("categoriaId", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(itemView);
            }
        };
        adapter.startListening();

        adapter.notifyDataSetChanged(); //Actualiza los datos si cambian
        recycler_menu.setAdapter(adapter);

    }

    //Admin ini ----------------------------------------------------------------------

    private void mostrarDialogo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Inicio.this);
        alertDialog.setTitle("Agregar nueva categoría");

        LayoutInflater inflater = this.getLayoutInflater();
        View admin_categoria_layout = inflater.inflate(R.layout.admin_categoria_layout, null);

        adminEditNombre = admin_categoria_layout.findViewById(R.id.adminEditNombre);
        adminBtnSelect = admin_categoria_layout.findViewById(R.id.adminBtnSelect);
        adminBtnUpload = admin_categoria_layout.findViewById(R.id.adminBtnUpload);

        alertDialog.setView(admin_categoria_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        adminBtnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirImagen();
            }
        });

        adminBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subirImagen();
            }
        });

        alertDialog.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (nuevaCategoria != null) {
                    category.push().setValue(nuevaCategoria);
                    Snackbar.make(drawer, "Categoría: " + nuevaCategoria.getNombre() + " fue agregada", Snackbar.LENGTH_SHORT).show();
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
            adminBtnSelect.setText("Imagen Selccionada");
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
                    Toast.makeText(Inicio.this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
                    imagenFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            nuevaCategoria = new Categoria(adminEditNombre.getText().toString(), uri.toString());
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Inicio.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            mostrarDialogoActualizar(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {
            eliminarCategoría(adapter.getRef(item.getOrder()).getKey());

        }
        return super.onContextItemSelected(item);
    }

    private void mostrarDialogoActualizar(final String key, final Categoria item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Inicio.this);
        alertDialog.setTitle("Actualizar categoría");

        final LayoutInflater inflater = this.getLayoutInflater();
        View admin_categoria_layout = inflater.inflate(R.layout.admin_categoria_layout, null);

        adminEditNombre = admin_categoria_layout.findViewById(R.id.adminEditNombre);
        adminBtnSelect = admin_categoria_layout.findViewById(R.id.adminBtnSelect);
        adminBtnUpload = admin_categoria_layout.findViewById(R.id.adminBtnUpload);

        alertDialog.setView(admin_categoria_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        adminEditNombre.setText(item.getNombre());

        adminBtnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirImagen();
            }
        });

        adminBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarImagen(item);
            }
        });

        alertDialog.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setNombre(adminEditNombre.getText().toString());
                category.child(key).setValue(item);
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

    public void cambiarImagen(final Categoria item) {
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
                    Toast.makeText(Inicio.this, "Subido correctamente", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Inicio.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void eliminarCategoría(String key) {
        DatabaseReference comidas = firebaseDatabase.getReference("comida");
        Query comidaEnCategoria = comidas.orderByChild("idCat").equalTo(key);
        comidaEnCategoria.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    postSnapShot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        category.child(key).removeValue();
    }

    //Admin fin ----------------------------------------------------------------------

    /*private  void updateToken(String token){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token (token, false);
        tokens.child(Common.currentUser.getEmail().setValue(data));
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.Actualizar) {
            cargarMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_carro) {
            Intent cintent = new Intent(Inicio.this, Carrito.class);
            startActivity(cintent);

        } else if (id == R.id.nav_pedido) {
            Intent pintent = new Intent(Inicio.this, PedidoEstado.class);
            startActivity(pintent);

        } else if (id == R.id.nav_salir) {
            Paper.book().destroy();
            firebaseAuth.signOut();
            irAWelcome();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userid = firebaseAuth.getCurrentUser().getUid();
            usuario = FirebaseDatabase.getInstance().getReference().child("usuario");
            cargarMenu();
        } else {
            irAWelcome();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        counterFabIni.setCount(new Database(this).getContCarrito(Common.currentUser.getEmail()));
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void irAWelcome() {
        Intent intent = new Intent(Inicio.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}
