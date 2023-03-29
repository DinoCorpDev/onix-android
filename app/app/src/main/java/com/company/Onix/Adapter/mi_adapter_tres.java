package com.company.Onix.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.Onix.Modelos.mi_servicio_tres;
import com.company.Onix.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class mi_adapter_tres extends FirebaseRecyclerAdapter<mi_servicio_tres, mi_adapter_tres.ViewHolder> {
    private static final Object MODE_PRIVATE = true;
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    private  String mi_telefono;
    DatabaseReference mDatabase_dos;
    DatabaseReference mDatabase_cliente;
    private String mLatConductor;
    private String mLngConducor;

    private Context mContext;
    // nuevas cosas

    private LatLng mOringinLatLng;
    private LatLng mDriverFoundLatLng;

    private DatabaseReference mData_seleccion;
    private DatabaseReference mData_conductor;



    public mi_adapter_tres(FirebaseRecyclerOptions<mi_servicio_tres> options, Context context){
        super(options);

        mContext=context;

        mPref = context.getSharedPreferences("sessiones", Context.MODE_PRIVATE);
        String telefono_bd = mPref.getString("telefono", "");
        String nombre = mPref.getString("nombre", "");
        String ciudad=mPref.getString("mi_ciudad", "");
        mData_seleccion=FirebaseDatabase.getInstance().getReference().child(ciudad).child("servicios").child(telefono_bd);

    }



    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull mi_servicio_tres dato) {
        final String id= getRef(position).getKey();

        holder.nombre.setText(dato.getNombre());

        String tiempo=dato.getMinutos();
        String distancia=dato.getKm();
        holder.mTiempo_y_distancia.setText(distancia);
        holder.mTarifa_conductor.setText(String.valueOf(dato.getTarifa_conductor())+" COP");

        String image =dato.getFoto();

        Picasso.get().load(image).into(holder.mfoto_cliente);

        mData_conductor=FirebaseDatabase.getInstance().getReference().child("registros").child("conductores").child(id);
        mData_conductor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String numero_viajes=snapshot.child("viajes_vip").getValue().toString();
                    holder.mNumero_viajes.setText(String.valueOf(numero_viajes));


                    String cancelaciones=snapshot.child("servicios_cancelados").getValue().toString();
                    int numero_cacelaciones=Integer.parseInt(cancelaciones);
                    int numero_viajes_conversion=Integer.parseInt(numero_viajes);
                    if(numero_cacelaciones>numero_viajes_conversion){
                            holder.mExperiencia.setText("Novato");
                    }else {
                        holder.mExperiencia.setText("Normal");
                    }

                    if(numero_viajes_conversion>30){
                        holder.mExperiencia.setText("Recomendado");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pago_conductor=String.valueOf(dato.getTarifa_conductor());

                Toast.makeText(mContext, "conductor seleccionado", Toast.LENGTH_SHORT).show();
                HashMap<String,Object> registro= new HashMap<>();
                registro.put("telefono_conductor",id);
                registro.put("estado","aceptado");
                registro.put("precio",pago_conductor);
                mData_seleccion.updateChildren(registro);

            }
        });


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_tres,parent,false);
        return new ViewHolder(view);


    }

    public class ViewHolder extends RecyclerView.ViewHolder{



        private TextView nombre;
        private CircleImageView mfoto_cliente;
        private TextView mTiempo_y_distancia;
        private TextView mTarifa_conductor;
        private TextView mExperiencia;
        private TextView mNumero_viajes;




        private View mView;

        public ViewHolder(View view){
            super(view);
            mView=view;
            mTarifa_conductor=view.findViewById(R.id.tarifa_conductor);
            nombre=view.findViewById(R.id.nombre_conductor_seleccion);
            mfoto_cliente=view.findViewById(R.id.foto_cliente_historial);
            mTiempo_y_distancia=view.findViewById(R.id.tiempo_y_distancia);
            mNumero_viajes=view.findViewById(R.id.numero_viajes);
            mExperiencia=view.findViewById(R.id.experiencia);






        }
    }
}




