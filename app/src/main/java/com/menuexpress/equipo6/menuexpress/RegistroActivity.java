package com.menuexpress.equipo6.menuexpress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

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
        btnRegistrar = (Button) findViewById(R.id.btnIngresar);
        cbMostrar = (CheckBox) findViewById(R.id.cbMostrar);

        //Inicializar Firebase
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference tabla_usuario = firebaseDatabase.getReference("usuario");

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
                irMain();
                tabla_usuario.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Verificar si el usuario existe
                        if (dataSnapshot.child(edtTelefono.getText().toString()).exists()) {
                            Toast.makeText(RegistroActivity.this, "Usuario ya existe", Toast.LENGTH_SHORT).show();
                        } else {
                            Usuario usuario = new Usuario(
                                    edtNombre.getText().toString(),
                                    edtA_pat.getText().toString(),
                                    edtA_mat.getText().toString(),
                                    edtTelefono.getText().toString(),
                                    edtDir.getText().toString(),
                                    edtCorreo.getText().toString(),
                                    edtPassword.getText().toString());
                            tabla_usuario.child(edtTelefono.getText().toString()).setValue(usuario);
                            Toast.makeText(RegistroActivity.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    public void irMain() {
        Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
