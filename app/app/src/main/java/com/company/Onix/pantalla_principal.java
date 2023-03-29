package com.company.Onix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class pantalla_principal extends AppCompatActivity {
    private Button mBtn_crear_cuenta;
    private Button mBtn_loguear_cuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);

        mBtn_crear_cuenta=findViewById(R.id.btn_crea_cuenta);
        mBtn_loguear_cuenta=findViewById(R.id.btn_loguear_cuenta);

        mBtn_crear_cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(pantalla_principal.this,pantalla_registro.class);
                startActivity(intent);
            }
        });

        mBtn_loguear_cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(pantalla_principal.this,login.class);
                startActivity(intent);

            }
        });

    }
}