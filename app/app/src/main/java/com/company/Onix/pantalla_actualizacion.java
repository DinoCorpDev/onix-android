package com.company.Onix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class pantalla_actualizacion extends AppCompatActivity {
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;

    private Button mBtn_acutalizar;

    private String Extra_link_apk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_actualizacion);
        Extra_link_apk=getIntent().getStringExtra("link_apk");
        mBtn_acutalizar=findViewById(R.id.btn_actualizar);

        mBtn_acutalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finishAffinity();
                Uri uri = Uri.parse(Extra_link_apk);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });

    }
}