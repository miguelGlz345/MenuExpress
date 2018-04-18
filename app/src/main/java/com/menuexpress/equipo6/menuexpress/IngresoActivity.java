package com.menuexpress.equipo6.menuexpress;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IngresoActivity extends AppCompatActivity {

    TextView edtEmail, edtContraseña;
    Button bIngreso;
    com.rey.material.widget.CheckBox cbMostrar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference tabla_usuario;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);

        edtEmail = (TextView) findViewById(R.id.edtEmail);
        edtContraseña = (TextView) findViewById(R.id.edtContraseña);
        bIngreso = (Button) findViewById(R.id.btnIngresar);
        cbMostrar = (com.rey.material.widget.CheckBox) findViewById(R.id.cbShow);

        //Inicializar Firebase
        //Inicializar Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        tabla_usuario = firebaseDatabase.getReference("usuario");

        cbMostrar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!cbMostrar.isChecked()) {
                    //mostrar password
                    edtContraseña.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    //ocultar password
                    edtContraseña.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        bIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                firebaseAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtContraseña.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(IngresoActivity.this, "Inicio de sesion correcto", Toast.LENGTH_SHORT).show();
                            irMain();
                        } else {
                            Toast.makeText(IngresoActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                /*tabla_usuario.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Verificar si el usuario existe
                        if (dataSnapshot.child(edtEmail.getText().toString()).exists()) {
                            //Obtener la informacion del usuario
                            Usuario usuario = dataSnapshot.child(edtEmail.getText().toString()).getValue(Usuario.class);
                            if (usuario.getContraseña().equals(edtContraseña.getText().toString())) {
                                Toast.makeText(IngresoActivity.this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();
                                irMain();
                            } else {
                                Toast.makeText(IngresoActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(IngresoActivity.this, "Usuario no existe", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
            }
        });
    }

    public void irMain() {
        Intent intent = new Intent(IngresoActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            irMain();
        }
    }
}
