package com.menuexpress.equipo6.menuexpress;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.menuexpress.equipo6.menuexpress.Model.Usuario;

public class RegistroActivity extends AppCompatActivity {

    EditText edtNombre, edtA_pat, edtA_mat, edtTelefono, edtDir, edtCorreo, edtPassword;
    Button btnRegistrar;
    CheckBox cbMostrar;

    long maxIdUser;
    String Uid;
    String checkUser = "0";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference tabla_usuario;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        edtNombre = (EditText) findViewById(R.id.edtNom);
        edtA_pat = (EditText) findViewById(R.id.edtA_pat);
        edtA_mat = (EditText) findViewById(R.id.edtA_mat);
        edtTelefono = (EditText) findViewById(R.id.edtTelefono);
        edtDir = (EditText) findViewById(R.id.edtDireccion);
        edtCorreo = (EditText) findViewById(R.id.edtCorreo);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnRegistrar = (Button) findViewById(R.id.btnRegistroR);
        cbMostrar = (CheckBox) findViewById(R.id.cbMostrar);

        //Inicializar Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        tabla_usuario = firebaseDatabase.getReference("usuario");

       /* tabla_usuario.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    while (dataSnapshot.child(Uid).getValue(Queue.class).equals("usuario")) {
                        checkUser = "1";
                        break;
                    }
                } catch (Exception e) {
                    checkUser = "0";
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

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

                firebaseAuth.createUserWithEmailAndPassword(edtCorreo.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            maxId();
                            String user_id = firebaseAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDB = tabla_usuario.child(user_id);
                            Usuario usuario = new Usuario(
                                    maxIdUser,
                                    edtNombre.getText().toString(),
                                    edtA_pat.getText().toString(),
                                    edtA_mat.getText().toString(),
                                    edtTelefono.getText().toString(),
                                    edtDir.getText().toString(),
                                    edtCorreo.getText().toString());
                            currentUserDB.child(user_id).setValue(usuario);
                            Toast.makeText(RegistroActivity.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(RegistroActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });

                /*tabla_usuario.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Verificar si el usuario existe
                        if (dataSnapshot.child(edtTelefono.getText().toString()).exists()) {
                            Toast.makeText(RegistroActivity.this, "Usuario ya existe", Toast.LENGTH_SHORT).show();
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
            }
        });

    }


    public void maxId() {
        tabla_usuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                maxIdUser = dataSnapshot.getChildrenCount() + 1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void irMain() {
        Intent intent = new Intent(RegistroActivity.this, Inicio.class);
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
