package com.menuexpress.equipo6.menuexpress;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Model.Usuario;

import io.paperdb.Paper;

public class IngresoActivity extends AppCompatActivity {

    private TextView edtEmail, edtContraseña;
    private Button bIngreso;
    private com.rey.material.widget.CheckBox cbMostrar;
    private com.rey.material.widget.CheckBox cbRecordar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference tabla_usuario;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);

        edtEmail = (TextView) findViewById(R.id.edtEmail);
        edtContraseña = (TextView) findViewById(R.id.edtContraseña);
        bIngreso = (Button) findViewById(R.id.btnIngresarI);
        cbMostrar = (com.rey.material.widget.CheckBox) findViewById(R.id.cbShow);
        cbRecordar = (com.rey.material.widget.CheckBox) findViewById(R.id.cbRecuerdame);

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
                //if (Common.currentUser.getAdmin() == true){
                ingresoUsuario();
                // }
            }
        });
    }

    private void ingresoUsuario() {
        String email = edtEmail.getText().toString();
        String pass = edtContraseña.getText().toString();

        if (Common.isConnectedToInternet(getBaseContext())) {

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            tabla_usuario.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String user_id = firebaseAuth.getCurrentUser().getUid();

                                    //guardar email y contraseña
                                    if (cbRecordar.isChecked()) {
                                        Paper.book().write(Common.EMAIL_KEY, user_id);
                                        Paper.book().write(Common.PASS_KEY, user_id);
                                    }

                                    //Verificar si el usuario existe
                                    if (dataSnapshot.child(user_id).exists()) {
                                        //Obtener la informacion del usuario
                                        Usuario usuario = dataSnapshot.child(user_id).getValue(Usuario.class);

                                        //Se guarda el usuario actual
                                        Common.currentUser = usuario;
                                        Intent intent = new Intent(IngresoActivity.this, Inicio.class);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(IngresoActivity.this, "Inicio de sesion correcto", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(IngresoActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            Toast.makeText(IngresoActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(IngresoActivity.this, "Faltan datos que llenar", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(IngresoActivity.this, "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void irAInicio() {
        Intent intent = new Intent(IngresoActivity.this, Inicio.class);
        startActivity(intent);
        //finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        //if (currentUser != null) {
        //  irAInicio();
        //}
    }
}
