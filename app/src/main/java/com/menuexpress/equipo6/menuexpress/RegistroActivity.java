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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Model.Usuario;

public class RegistroActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    Button btnRegistrar;
    EditText edtNombre, edtA_pat, edtA_mat, edtTelefono, edtDir, edtCorreo, edtPassword, edtConfPass;

    long maxIdUser;
    String Uid;
    String checkUser = "0";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference tabla_usuario;
    private FirebaseAuth firebaseAuth;
    com.rey.material.widget.CheckBox cbMostrar, cbMostrar2;
    private DatabaseReference campo_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        edtNombre = (EditText) findViewById(R.id.edtNom);
        //edtA_pat = (EditText) findViewById(R.id.edtA_pat);
        //edtA_mat = (EditText) findViewById(R.id.edtA_mat);
        //edtTelefono = (EditText) findViewById(R.id.edtTelefono);
        //edtDir = (EditText) findViewById(R.id.edtDireccion);
        edtCorreo = (EditText) findViewById(R.id.edtCorreo);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfPass = (EditText) findViewById(R.id.edtConfPass);
        btnRegistrar = (Button) findViewById(R.id.btnRegistroR);
        cbMostrar = (com.rey.material.widget.CheckBox) findViewById(R.id.cbMostrar);
        cbMostrar2 = (com.rey.material.widget.CheckBox) findViewById(R.id.cbMostrar2);

        //Inicializar Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        tabla_usuario = firebaseDatabase.getReference("usuario");

        cbMostrar2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!cbMostrar2.isChecked()) {
                    //mostrar password
                    edtConfPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    //ocultar password
                    edtConfPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        cbMostrar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!cbMostrar.isChecked()) {
                    //mostrar password
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    //ocultar password
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resgistrarUsuario();
            }
        });

    }

    private void resgistrarUsuario() {
        String nombre = edtNombre.getText().toString();
        String email = edtCorreo.getText().toString();
        String pass = edtPassword.getText().toString();
        String confpass = edtConfPass.getText().toString();
        if (Common.isConnectedToInternet(getBaseContext())) {

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(confpass)) {

                if (pass.equals(confpass)) {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                tabla_usuario.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        //Registrar usuario
                                        String user_id = firebaseAuth.getCurrentUser().getUid();
                                        DatabaseReference currentUserDB = tabla_usuario.child(user_id);
                                        Usuario usuario = new Usuario(
                                                edtNombre.getText().toString(),
                                                //edtA_pat.getText().toString(),
                                                //edtA_mat.getText().toString(),
                                                //edtTelefono.getText().toString(),
                                                //edtDir.getText().toString(),
                                                edtCorreo.getText().toString());
                                        currentUserDB.setValue(usuario);

                                        irIngreso();
                                        //irMain();
                                        Toast.makeText(RegistroActivity.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else {//task
                                Toast.makeText(RegistroActivity.this, "Usuario ya existe", Toast.LENGTH_SHORT).show();
                            }
                        } //OnComplete
                    });
                } else { //equals
                    Toast.makeText(RegistroActivity.this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show();
                }
            } else { //!Empty
                Toast.makeText(RegistroActivity.this, "Faltan campos que llenar", Toast.LENGTH_SHORT).show();
            }

        } else {//Conectado a internet
            Toast.makeText(RegistroActivity.this, "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void irMain() {
        Intent intent = new Intent(RegistroActivity.this, Inicio.class);
        startActivity(intent);
    }

    public void irIngreso() {
        Intent intent = new Intent(RegistroActivity.this, IngresoActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        //if (currentUser != null) {
        //  irMain();
        //}
    }
}
