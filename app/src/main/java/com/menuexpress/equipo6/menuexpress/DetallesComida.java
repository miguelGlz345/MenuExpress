package com.menuexpress.equipo6.menuexpress;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Database.Database;
import com.menuexpress.equipo6.menuexpress.Model.Comida;
import com.menuexpress.equipo6.menuexpress.Model.Pedido;
import com.squareup.picasso.Picasso;

public class DetallesComida extends AppCompatActivity {

    private TextView nombre_comida, precio_comida, descrip_comida;
    private ImageView imagen_comida;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton btnCarro;
    private ElegantNumberButton btnNumero;

    private String comidaId = "";

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference comida;

    private Comida comidaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_comida);

        //Iniciarlizar firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        comida = firebaseDatabase.getReference("comida");

        btnNumero = (ElegantNumberButton) findViewById(R.id.btn_cant);
        btnCarro = (FloatingActionButton) findViewById(R.id.btn_carro);

        btnCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).agregarCarrito(new Pedido(
                        comidaId,
                        comidaActual.getNombre(),
                        btnNumero.getNumber(),
                        comidaActual.getPrecio(),
                        comidaActual.getDescuento()

                ));
                Toast.makeText(DetallesComida.this, "Agregado al carrito", Toast.LENGTH_SHORT).show();
            }
        });

        nombre_comida = (TextView) findViewById(R.id.nombre_comida);
        precio_comida = (TextView) findViewById(R.id.comida_precio);
        descrip_comida = (TextView) findViewById(R.id.comida_descrip);
        imagen_comida = (ImageView) findViewById(R.id.img_comida);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandeAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Obtener el id del intent
        if (getIntent() != null)
            comidaId = getIntent().getStringExtra("comidaId");
        if (!comidaId.isEmpty()) {
            if (Common.isConnectedToIntenet(getBaseContext())) {
                getDetalleComida(comidaId);
            } else {
                Toast.makeText(DetallesComida.this, "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    private void getDetalleComida(String comidaId) {
        comida.child(comidaId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comidaActual = dataSnapshot.getValue(Comida.class);

                //colocar imagen
                Picasso.with(getBaseContext()).load(comidaActual.getImagen()).into(imagen_comida);

                collapsingToolbarLayout.setTitle(comidaActual.getNombre());
                precio_comida.setText(comidaActual.getPrecio());
                nombre_comida.setText(comidaActual.getNombre());
                descrip_comida.setText(comidaActual.getDescripcion());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
