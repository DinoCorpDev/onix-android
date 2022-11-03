package com.company.Onix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.hdodenhof.circleimageview.CircleImageView;

public class pantalla_terminos extends AppCompatActivity {
    private CircleImageView mAtras;
    private Button mRegresar_dos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_terminos);
        mAtras=findViewById(R.id.btn_regresar_flecha);
        mRegresar_dos=findViewById(R.id.btn_regresar_terminos_dos);
        mRegresar_dos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}