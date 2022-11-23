package com.company.Onix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class pantalla_ciudad extends AppCompatActivity {
    private Spinner mSpinner_ciudad;
    private Button mBtn_aceptar_ciudad;
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    private DatabaseReference mData_usuario;
    private String mTelefono;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_ciudad);
        mSpinner_ciudad=findViewById(R.id.selecion_ciudad);
        mBtn_aceptar_ciudad=findViewById(R.id.btn_aceptar_ciudad);
        ArrayAdapter<CharSequence> adapter_cuatro= ArrayAdapter.createFromResource(this,R.array.opciones_ciudad, android.R.layout.simple_spinner_item);
        adapter_cuatro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner_ciudad.setAdapter(adapter_cuatro);
        mPref = getApplication().getSharedPreferences("sessiones", Context.MODE_PRIVATE);

        String telefono_bd = mPref.getString("telefono", "");
        if(!telefono_bd.equals("")){
            mTelefono = telefono_bd;

            mData_usuario = FirebaseDatabase.getInstance().getReference().child("registros").child("usuarios").child(telefono_bd);

        }

        mBtn_aceptar_ciudad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ciudad= mSpinner_ciudad.getSelectedItem().toString();
                if (ciudad.equals("Seleccione una ciudad")){
                    Toast.makeText(pantalla_ciudad.this, "Debes seleccionar la ciudad donde te encuentras", Toast.LENGTH_SHORT).show();
                }else {

                    mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
                    mEditor=mPref.edit();

                    mEditor.putString("mi_ciudad",ciudad);
                    mEditor.putString("pantalla","plataforma");
                    mEditor.apply();

                    HashMap<String,Object> registro= new HashMap<>();
                    registro.put("ciudad",ciudad);
                    mData_usuario.updateChildren(registro);

                    finish();

                }

            }
        });

    }

    @Override
    public void onBackPressed() {

    }
}