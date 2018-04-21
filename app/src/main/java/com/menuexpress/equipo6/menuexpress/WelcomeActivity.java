package com.menuexpress.equipo6.menuexpress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Model.Usuario;

import io.paperdb.Paper;

public class WelcomeActivity extends AppCompatActivity {

    private LinearLayout l1, l2;
    private Animation uptodown, downtoup;
    private Button bRegistro, bIngreso;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        l1 = (LinearLayout) findViewById(R.id.line1);
        l2 = (LinearLayout) findViewById(R.id.line2);
        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        l1.setAnimation(uptodown);
        l2.setAnimation(downtoup);

        //Iniciarlizar firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usuario = firebaseDatabase.getReference("usuario");

        bRegistro = (Button) findViewById(R.id.btnRegistroW);
        bIngreso = (Button) findViewById(R.id.btnIngresarW);

        //Inicializar Paper
        Paper.init(this);

        bRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registro();
            }
        });

        bIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarSesion();
            }
        });
    }

    public void iniciarSesion() {
        Intent intent = new Intent(WelcomeActivity.this, IngresoActivity.class);
        startActivity(intent);
        finish();
    }

    public void Registro() {
        Intent intent = new Intent(WelcomeActivity.this, RegistroActivity.class);
        startActivity(intent);
        finish();
    }

    private void irAInicio() {
        Intent intent = new Intent(WelcomeActivity.this, Inicio.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();

        String email = Paper.book().read(Common.EMAIL_KEY);
        String pass = Paper.book().read(Common.PASS_KEY);

        if (email != null && pass != null) {
            if (!email.isEmpty() && !pass.isEmpty()) {
                usuarioRecordado(email, pass);
                bIngreso.setVisibility(View.GONE);
                bRegistro.setVisibility(View.GONE);
            } else {
                firebaseAuth.signOut();
                bIngreso.setVisibility(View.VISIBLE);
                bRegistro.setVisibility(View.VISIBLE);
            }
        }


    }

    private void usuarioRecordado(String email, String pass) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            usuario.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String user_id = firebaseAuth.getCurrentUser().getUid();

                    //Verificar si el usuario existe
                    if (dataSnapshot.child(user_id).exists()) {
                        //Obtener la informacion del usuario
                        Usuario usuario = dataSnapshot.child(user_id).getValue(Usuario.class);
                        //Se guarda el usuario actual
                        Common.currentUser = usuario;
                        //Toast.makeText(WelcomeActivity.this, "Inicio de sesion correcto", Toast.LENGTH_SHORT).show();
                        irAInicio();
                    } else {
                        Toast.makeText(WelcomeActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


}
