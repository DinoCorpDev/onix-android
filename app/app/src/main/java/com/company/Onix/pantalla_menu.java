package com.company.Onix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.company.Onix.services.servicio_pantallas;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class pantalla_menu extends AppCompatActivity {
    private CircleImageView mBtn_regrasar;

    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    private Button mBtn_soporte;
    private Button mBtn_historial;
    private Button mBtn_compartir;
    private Button mBtn_pagina;
    private Button mBtn_modo_conductor;
    private Button mBtn_terminos;
    private Button mBtn_cerrar_aplicacion;
    private Button mBtn_cerrar_sesion;
    private String Telefono_soporte;
    private String pagina;
    private String playstore_usuario;
    private String playstore_conductor;

    private TextView mTelefono_menu;

    private CircleImageView mFoto;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_menu);
        mFoto=findViewById(R.id.imagen_conductor);
        mTelefono_menu=findViewById(R.id.telefono_menu);
        mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
        String telefono_bd=mPref.getString("telefono","");
        String foto=mPref.getString("foto","");

        String nombre=mPref.getString("nombre","");

        if(!telefono_bd.equals("")){

            mTelefono_menu.setText(telefono_bd);

            Picasso.get().load(foto).into(mFoto);


        }

        mBtn_terminos=findViewById(R.id.btn_terminos);
        mBtn_regrasar=findViewById(R.id.btn_regresar);
        mBtn_soporte=findViewById(R.id.btn_soporte);
        mBtn_historial=findViewById(R.id.btn_historial_viajes);
        mBtn_compartir=findViewById(R.id.btn_compartir);
        mBtn_modo_conductor=findViewById(R.id.btn_conductor);
        mBtn_pagina=findViewById(R.id.btn_pagina_web);
        mBtn_cerrar_aplicacion=findViewById(R.id.btn_salir);
        mBtn_cerrar_sesion=findViewById(R.id.btn_cerrar_sesion);


        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("a_servidor");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Telefono_soporte=snapshot.child("soporte_usuario").getValue().toString();
                        playstore_usuario=snapshot.child("playstore_usuario").getValue().toString();
                        pagina=snapshot.child("pagina").getValue().toString();
                        playstore_conductor=snapshot.child("playstore_conductor").getValue().toString();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        mBtn_soporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=+" + Telefono_soporte + "&text=Soy%20cliente%20de%20ONIX%20necesito%20soporte%20t%C3%A9cnico%20");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        mBtn_compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mLink=playstore_usuario;

                String mTexto_compartir="Te invito a descargar la nueva aplicacion de transporte en Bogotá y Medellin."+"\n\n"+"Su nombre es: ONIX pide tu servicio de MotoTaxi con tarifa fija de la forma mas facil y rapida con un solo click"+"\n\n"+"Descargala desde el suguiente enlace"+"\n\n"+"LINK DE DESCARGA:"+"\n\n"+mLink;


                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mTexto_compartir);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

            }
        });

        mBtn_pagina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(pantalla_menu.this,pantalla_ciudad.class);
                startActivity(intent);
            }
        });

        mBtn_modo_conductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(playstore_conductor);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        mBtn_cerrar_aplicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parar_alertas();
                finishAffinity();
            }
        });

        mBtn_cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
                mEditor=mPref.edit();
                mEditor.putString("id_publi","");
                mEditor.putString("pantalla","");
                mEditor.apply();
                parar_alertas();
                Intent intent = new Intent(pantalla_menu.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setAction(Intent.ACTION_RUN);
                startActivity(intent);
            }
        });

        mBtn_historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(pantalla_menu.this,pantalla_hostorial.class);
                startActivity(intent);
            }
        });
        mBtn_terminos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(pantalla_menu.this,pantalla_terminos.class);
                startActivity(intent);
            }
        });
        mBtn_regrasar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private void parar_alertas(){

        Intent serviceIntent= new Intent(this, servicio_pantallas.class);
        stopService(serviceIntent);
    }
}