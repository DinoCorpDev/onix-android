package com.company.Onix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class pantalla_intalar_servicios extends AppCompatActivity {
    private LinearLayout mBtn_acutalizar_servicios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_intalar_servicios);

        mBtn_acutalizar_servicios=findViewById(R.id.btn_actualizar_servicios);
        mBtn_acutalizar_servicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.ims");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                finish();
            }
        });

    }
}