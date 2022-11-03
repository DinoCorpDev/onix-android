package com.company.Onix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.company.Onix.Adapter.mi_adapter_dos;
import com.company.Onix.Modelos.mi_servicio_dos;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class pantalla_hostorial extends AppCompatActivity {
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;
    private mi_adapter_dos mAdapter;
    private boolean mServicios_en_lista=true;
    private LinearLayout mVentana_progreso;
    private String codigo;
    private String mTelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_hostorial);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
        String nombre_bd=mPref.getString("nombre","");

        String telefono_bd=mPref.getString("telefono","");
        if(!nombre_bd.equals("")&& !telefono_bd.equals("")) {
            mTelefono=telefono_bd;
        }




        mVentana_progreso=findViewById(R.id.ventana_progreso);


        mRecyclerView = findViewById(R.id.contenedor_historial);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        Query query = FirebaseDatabase.getInstance().getReference().child("registros").child("usuarios").child(mTelefono)
                .child("historial_de_viaje")
                .orderByChild("estado_servicio")
                .equalTo("Cobrado");


        ;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    mVentana_progreso.setVisibility(View.INVISIBLE);

                }else {
                    mVentana_progreso.setVisibility(View.VISIBLE);
                    mServicios_en_lista=false;



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseRecyclerOptions<mi_servicio_dos> options=new FirebaseRecyclerOptions.Builder<mi_servicio_dos>()
                .setQuery(query, mi_servicio_dos.class)
                .build();

        mAdapter=new mi_adapter_dos(options, pantalla_hostorial.this);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.startListening();





    }



    @Override
    protected void onStart() {
        super.onStart();

    }







    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
}