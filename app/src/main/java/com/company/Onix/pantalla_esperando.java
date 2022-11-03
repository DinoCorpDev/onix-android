package com.company.Onix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.company.Onix.Adapter.mi_adapter_tres;
import com.company.Onix.Modelos.mi_servicio_tres;
import com.company.Onix.services.servicio_pantallas;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;

public class pantalla_esperando extends AppCompatActivity {
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    private String mi_telefono;
    private String mi_nombre;
    private DatabaseReference mData;
    private ValueEventListener mListener;
    private Button mCancelar_espera;
    // lista

    private RecyclerView mRecyclerView;
    private mi_adapter_tres mAdapter;

    private LinearLayout mVentana_encima;

    private TextView mValor_total;
    private DatabaseReference mData_numero;
    private DatabaseReference mData_postular;
    private Boolean Tutorial=false;

    Handler handler = new Handler();
    int contador=0;
    private String mEstado;
    private TextToSpeech cosa;
    private String mTelefono_conductor="";

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("NOSE", "Temporizador");
            handler.postDelayed(runnable, 1000);
            contador++;
            if(contador>15){
                if(mEstado!=null){
                    if(mEstado.equals("vip")){
                        HashMap<String, Object> registro = new HashMap<>();
                        registro.put("estado", "esperando");
                        mData.updateChildren(registro);

                        handler.removeCallbacks(runnable);
                    }
                }



            }

        }



    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_esperando);
        mCancelar_espera=findViewById(R.id.btn_cancelar_espera);
        escucuchar_alertas();

        mPref = getApplicationContext().getSharedPreferences("sessiones", MODE_PRIVATE);
        String telefono_bd = mPref.getString("telefono", "");
        String nombre = mPref.getString("nombre", "");
        String ciudad=mPref.getString("mi_ciudad", "");
        if (!telefono_bd.equals("")) {

            mi_telefono = telefono_bd;
            mi_nombre = nombre;
            mData = FirebaseDatabase.getInstance().getReference().child(ciudad).child("servicios").child(telefono_bd);
            mData_postular = FirebaseDatabase.getInstance().getReference().child(ciudad).child("postulaciones").child(telefono_bd);

            mValor_total=findViewById(R.id.valore_total);
            mData_numero = FirebaseDatabase.getInstance().getReference().child(ciudad).child("servicios").child(telefono_bd);
            mData_numero.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){

                        String precio=snapshot.child("precio").getValue().toString();
                        String descuento=snapshot.child("descuento").getValue().toString();
                        mEstado=snapshot.child("estado").getValue().toString();

                        if(mEstado.equals("vip")){
                            runnable.run();
                        }

                        int descuento_numero=Integer.parseInt(descuento);
                        int precio_numero=Integer.parseInt(precio);
                        int total=precio_numero;
                        mValor_total.setText(String.valueOf(total)+" COP");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            consulta_base();

            mVentana_encima=findViewById(R.id.ventan_encima);

            mRecyclerView = findViewById(R.id.contenedor_historial);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(linearLayoutManager);

            Query query = FirebaseDatabase.getInstance().getReference().child(ciudad).child("postulaciones").child(telefono_bd)
                    .child("tabla_aceptados")
                    .orderByChild("km_reales")
                    .startAt(0.0);


            ;
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){

                        mVentana_encima.setVisibility(View.INVISIBLE);

                        cosa=new TextToSpeech(getBaseContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if(status!=TextToSpeech.ERROR){

                                    cosa.setLanguage(Locale.getDefault());
                                    String miTexto=mi_nombre+" En esta lista apareceran los conductores que aceptaron tu servicio, por favor selecciona al más cercano o al que mejor te parezca";
                                    if(Tutorial==false){
                                        cosa.speak(miTexto,cosa.QUEUE_FLUSH,null,null);
                                        Tutorial=true;

                                    }


                                }


                            }
                        });
                    }else {
                        mVentana_encima.setVisibility(View.VISIBLE);






                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            FirebaseRecyclerOptions<mi_servicio_tres> options=new FirebaseRecyclerOptions.Builder<mi_servicio_tres>()
                    .setQuery(query, mi_servicio_tres.class)
                    .build();

            mAdapter=new mi_adapter_tres(options, pantalla_esperando.this);

            mRecyclerView.setAdapter(mAdapter);

            mAdapter.startListening();
















        }
        mCancelar_espera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(handler!=null){
                    handler.removeCallbacks(runnable);

                }
                if(mData_postular!=null){
                    mData_postular.removeValue();
                }
                if(mData!=null){
                    parar_alertas();
                    mData.removeValue();
                    mData.removeEventListener(mListener);
                    mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
                    mEditor=mPref.edit();
                    mEditor.putString("pantalla","plataforma");
                    mEditor.apply();
                    mData.removeEventListener(mListener);
                    finish();
                }

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        consulta_base();
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mTelefono_conductor.equals("")){
            if(handler!=null){
                handler.removeCallbacks(runnable);

            }
            if(mData_postular!=null){
                mData_postular.removeValue();
            }

        }


    }

    @Override
    public void onBackPressed() {

    }

    private void consulta_base(){
        mListener=mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String telefono_conductor=snapshot.child("telefono_conductor").getValue().toString();
                    mTelefono_conductor=telefono_conductor;

                    if(!telefono_conductor.equals("")){
                        if(handler!=null){
                            handler.removeCallbacks(runnable);

                        }
                        mData.removeEventListener(mListener);
                        mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
                        mEditor=mPref.edit();
                        mEditor.putString("pantalla","servicio");
                        mEditor.apply();
                        mData.removeEventListener(mListener);
                        Intent intent=new Intent(pantalla_esperando.this, pantalla_servicio.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setAction(Intent.ACTION_RUN);

                        startActivity(intent);

                    }
                }else {
                    if(handler!=null){
                        handler.removeCallbacks(runnable);

                    }
                    mData.removeEventListener(mListener);
                    mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
                    mEditor=mPref.edit();
                    mEditor.putString("pantalla","plataforma");
                    mEditor.apply();
                    //el servicio se elimino de la base de datos
                    Intent intent=new Intent(pantalla_esperando.this, plataforma.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setAction(Intent.ACTION_RUN);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void escucuchar_alertas(){
        Intent serviceIntent= new Intent(this, servicio_pantallas.class);
        ContextCompat.startForegroundService(pantalla_esperando.this,serviceIntent);

    }
    private void parar_alertas(){

        Intent serviceIntent= new Intent(this, servicio_pantallas.class);
        stopService(serviceIntent);
    }

}