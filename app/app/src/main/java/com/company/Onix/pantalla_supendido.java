package com.company.Onix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class pantalla_supendido extends AppCompatActivity {
    private Button mBtn_salir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_supendido);
        mBtn_salir=findViewById(R.id.btn_salir_supension);
        mBtn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}