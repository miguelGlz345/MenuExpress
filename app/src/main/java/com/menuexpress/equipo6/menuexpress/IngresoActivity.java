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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.menuexpress.equipo6.menuexpress.Model.Usuario;

public class IngresoActivity extends AppCompatActivity {

    TextView edtUsuario, edtContraseña;
    Button bIngreso;
    CheckBox cbMostrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);

        edtUsuario = (TextView) findViewById(R.id.edtEmail);
        edtContraseña = (TextView) findViewById(R.id.edtContraseña);
        bIngreso = (Button) findViewById(R.id.btnIngresar);
        cbMostrar = (CheckBox) findViewById(R.id.cbShow);

        //Inicializar Firebase
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference tabla_usuario = firebaseDatabase.getReference("usuario");

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
                tabla_usuario.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Verificar si el usuario existe
                        if (dataSnapshot.child(edtUsuario.getText().toString()).exists()) {
                            //Obtener la informacion del usuario
                            Usuario usuario = dataSnapshot.child(edtUsuario.getText().toString()).getValue(Usuario.class);
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
                });
            }
        });

    }

    public void irMain() {
        Intent intent = new Intent(IngresoActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
