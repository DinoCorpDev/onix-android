package com.company.Onix.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.company.Onix.Adapter.ServiceAdapter;
import com.company.Onix.MainActivity;
import com.company.Onix.Modelos.Service;
import com.company.Onix.R;
import com.company.Onix.pantalla_servicio;
import com.company.Onix.utils.recycler.RecyclerItemClickListener;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ServicesActiveActivity extends AppCompatActivity {

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private ServiceAdapter mAdaptar_tabla_normal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_active);
        mPref = getApplicationContext().getSharedPreferences("sessiones", MODE_PRIVATE);
        mEditor = mPref.edit();
        readServices();
    }

    private void readServices() {
        String ciudad = mPref.getString("mi_ciudad", "");
        String telefono_bd = mPref.getString("telefono", "");
        ArrayList<Service> localDataSet = new ArrayList();
        RecyclerView mTabla_dos = findViewById(R.id.tabla_normal);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(ServicesActiveActivity.this);
        mTabla_dos.setLayoutManager(linearLayoutManager1);
        mTabla_dos.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mTabla_dos, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        mEditor.putString("telefono_s", localDataSet.get(position).getKey());
                        mEditor.apply();
                        Intent intent = new Intent(ServicesActiveActivity.this, pantalla_servicio.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        if (telefono_bd != null) {
            Query query = FirebaseDatabase.getInstance().getReference().child(ciudad).child("servicios");
            query.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                            Service service = new Service();
                            service.setKey(datasnapshot.getKey());
                            service.setDireccion(datasnapshot.child("direccion").getValue(String.class));
                            service.setDestino(datasnapshot.child("destino").getValue(String.class));
                            service.setKilometros(datasnapshot.child("kilometros").getValue(String.class));
                            service.setPrecio(datasnapshot.child("precio").getValue(String.class));
                            service.setStatus(datasnapshot.child("estado").getValue(String.class));
                            localDataSet.add(service);
                        }
                        mAdaptar_tabla_normal = new ServiceAdapter();
                        mAdaptar_tabla_normal.CustomAdapter(localDataSet);
                        mTabla_dos.setAdapter(mAdaptar_tabla_normal);
                        mAdaptar_tabla_normal.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }

    }

}