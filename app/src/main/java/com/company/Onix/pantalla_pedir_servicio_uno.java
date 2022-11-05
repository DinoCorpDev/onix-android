package com.company.Onix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.company.Onix.services.servicio_pantallas;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.PrimitiveIterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class pantalla_pedir_servicio_uno extends AppCompatActivity {
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    private String mCiudad;
    private CircleImageView mBtn_atras;
    private LinearLayout mBtn_solicitar_servicio;
    private LinearLayout mBtn_solicitar_carro;

    private  double mExtra_lat_origen;
    private double mExta_lng_origen;
    private String mExtra_origen;
    private String mExtra_destino;
    private String mExtra_nota;
    private String mExtra_precio;

    private DatabaseReference mData_usuario_detalle;
    private int bono_numero=0;
    private int descuento=0;
    private TextView mTarifa_usuario;
    private  String mPrecio_total;
    private DatabaseReference mData_postular;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_pedir_servicio_uno);

        mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
        String ciudad=mPref.getString("mi_ciudad","");
        String telefono_bd=mPref.getString("telefono","");
        String nombre_bd=mPref.getString("nombre","");
        mCiudad=ciudad;

        if(telefono_bd.equals("")|| telefono_bd==null){

            Intent intent=new Intent(pantalla_pedir_servicio_uno.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setAction(Intent.ACTION_RUN);

            startActivity(intent);

        }else {
            mTarifa_usuario=findViewById(R.id.tarifa_usuario);

            mData_usuario_detalle=FirebaseDatabase.getInstance().getReference().child("registros").child("usuarios").child(telefono_bd);


            mBtn_solicitar_servicio=findViewById(R.id.btn_solicitar_servicio_moto);

            mExtra_origen=getIntent().getStringExtra("origin");
            mExtra_lat_origen = getIntent().getDoubleExtra("origin_lat", 0);
            mExta_lng_origen = getIntent().getDoubleExtra("origin_lng", 0);
            mExtra_destino=    getIntent().getStringExtra("destino");
            mExtra_precio= getIntent().getStringExtra("precio");
            mExtra_nota= getIntent().getStringExtra("nota");

            mTarifa_usuario.setText(mExtra_precio+" COP");
            mPrecio_total=String.valueOf(mExtra_precio);

        }


        mBtn_solicitar_servicio.setOnClickListener(v -> {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
            String datetime = dateformat.format(c.getTime());

            mData_postular=FirebaseDatabase.getInstance().getReference().child(ciudad).child("postulaciones").child(telefono_bd);
            mData_postular.removeValue();
            escucuchar_alertas();
            //solicitar_el_servicio
            DatabaseReference servicio = FirebaseDatabase.getInstance().getReference().child(mCiudad).child("servicios").child(telefono_bd);
            HashMap<String, Object> registro = new HashMap<>();
            registro.put("nota", mExtra_nota);
            registro.put("destino", mExtra_destino);
            registro.put("modo", "moto");
            registro.put("minutos", "0");
            registro.put("kilometros", "0");
            registro.put("descuento", 0);
            registro.put("telefono_usuario", telefono_bd);
            registro.put("telefono_conductor", "");
            registro.put("estado", "esperando");
            registro.put("nombre", nombre_bd);
            registro.put("direccion", mExtra_origen);
            registro.put("lat", mExtra_lat_origen);
            registro.put("lng", mExta_lng_origen);
            registro.put("lat_destino", 0);
            registro.put("lng_destino", 0);
            registro.put("precio", mPrecio_total);
            registro.put("create_date", datetime);
            servicio.setValue(registro);
            Intent intent = new Intent(pantalla_pedir_servicio_uno.this, pantalla_esperando.class);
            startActivity(intent);
        });















        mBtn_atras=findViewById(R.id.btn_atras_modo);
        mBtn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void escucuchar_alertas(){
        Intent serviceIntent= new Intent(this, servicio_pantallas.class);
        ContextCompat.startForegroundService(pantalla_pedir_servicio_uno.this,serviceIntent);

    }
}