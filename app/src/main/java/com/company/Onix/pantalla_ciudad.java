package com.company.Onix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class pantalla_ciudad extends AppCompatActivity {
    private Spinner mSpinner_ciudad;
    private Button mBtn_aceptar_ciudad;
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_ciudad);
        mSpinner_ciudad=findViewById(R.id.selecion_ciudad);
        mBtn_aceptar_ciudad=findViewById(R.id.btn_aceptar_ciudad);
        ArrayAdapter<CharSequence> adapter_cuatro= ArrayAdapter.createFromResource(this,R.array.opciones_ciudad, android.R.layout.simple_spinner_item);
        adapter_cuatro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner_ciudad.setAdapter(adapter_cuatro);


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

                    finish();

                }

            }
        });

    }

    @Override
    public void onBackPressed() {

    }
}