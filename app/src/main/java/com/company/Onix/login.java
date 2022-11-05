package com.company.Onix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
    private Button mBtn_entrar;
    private TextInputEditText mTelefono_loguin;
    private TextInputEditText mContra_loguin;

    private ProgressDialog progressDialog;

    private Query mquery;
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase_conectados;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Comprobando ingreso a tu cuenta, por favor espere");

        mTelefono_loguin = findViewById(R.id.telefono_login);
        mContra_loguin = findViewById(R.id.contra_login);

        //tomar variables guardadas
        mPref = getApplicationContext().getSharedPreferences("sessiones", MODE_PRIVATE);
        String telefono_bd = mPref.getString("telefono", "");
        String contra_bd = mPref.getString("contra", "");
        if (!telefono_bd.equals("") && !contra_bd.equals("")) {
            mTelefono_loguin.setText(telefono_bd);
            mContra_loguin.setText(contra_bd);
        }

        mBtn_entrar = findViewById(R.id.btn_ingresar);
        mBtn_entrar.setOnClickListener(v -> {

            String telefono = mTelefono_loguin.getText().toString();
            String contra = mContra_loguin.getText().toString();

            if (telefono.equals("")) {
                mTelefono_loguin.requestFocus();
                Toast.makeText(login.this, "Digite su telefono", Toast.LENGTH_SHORT).show();
            } else {
                if (contra.equals("")) {
                    mContra_loguin.requestFocus();
                    Toast.makeText(login.this, "Digite su contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    mquery = FirebaseDatabase.getInstance().getReference().child("registros")
                            .child("usuarios")
                            .orderByChild("cuenta_login")
                            .equalTo(telefono + contra);

                    mquery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                mDatabase = FirebaseDatabase.getInstance().getReference().child("registros").child("usuarios").child(telefono);
                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {

                                            String nombre = snapshot.child("nombre").getValue().toString();
                                            String foto = snapshot.child("foto").getValue().toString();
                                            mPref = getApplicationContext().getSharedPreferences("sessiones", MODE_PRIVATE);
                                            mEditor = mPref.edit();
                                            mEditor.putString("foto", foto);
                                            mEditor.putString("nombre", nombre);
                                            mEditor.putString("telefono", telefono);
                                            mEditor.putString("contra", contra);
                                            mEditor.putString("pantalla", "plataforma");
                                            mEditor.apply();
                                            progressDialog.dismiss();
                                            Toast.makeText(login.this, "Ingresando a la plataforma", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(login.this, plataforma.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.setAction(Intent.ACTION_RUN);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(login.this, "Contaseña o telafono incorrecto", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
}