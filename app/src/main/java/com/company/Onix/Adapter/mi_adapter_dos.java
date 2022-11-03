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

import com.company.Onix.Modelos.mi_servicio_dos;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class mi_adapter_dos extends FirebaseRecyclerAdapter<mi_servicio_dos, mi_adapter_dos.ViewHolder> {
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



    public mi_adapter_dos(FirebaseRecyclerOptions<mi_servicio_dos> options, Context context){
        super(options);

        mContext=context;




    }



    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull mi_servicio_dos dato) {
        final String id= getRef(position).getKey();

        holder.nombre.setText(dato.getNombre_cliente());
        holder.mPago_historial.setText("Pago: "+dato.getPago());
        holder.mDireccion_historial.setText(dato.getDireccion());
        holder.mDestino_historial.setText(dato.getDestino());

        mDatabase_cliente= FirebaseDatabase.getInstance().getReference().child("registros").child("conductores").child(id);
        mDatabase_cliente.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String image =snapshot.child("foto").getValue().toString();

                    Picasso.get().load(image).into(holder.mfoto_cliente);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(mContext, "presiono", Toast.LENGTH_SHORT).show();

            }
        });


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_cuatro,parent,false);
        return new ViewHolder(view);


    }

    public class ViewHolder extends RecyclerView.ViewHolder{



        private TextView nombre;
        private CircleImageView mfoto_cliente;
        private TextView mNumero_carreras;
        private TextView mPago_historial;
        private TextView mDireccion_historial;
        private TextView mDestino_historial;



        private View mView;

        public ViewHolder(View view){
            super(view);
            mView=view;
            nombre=view.findViewById(R.id.nombre_cliente_historial);
            mfoto_cliente=view.findViewById(R.id.foto_cliente_historial);
            mPago_historial=view.findViewById(R.id.pago_cliente_historial);
            mDireccion_historial=view.findViewById(R.id.direccion_cliente_historial);
            mDestino_historial=view.findViewById(R.id.destino_cliente_historial);






        }
    }
}



