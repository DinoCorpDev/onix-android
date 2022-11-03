package com.company.Onix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class pantalla_cobro extends AppCompatActivity {
    private TextView mNombre_conductor;
    private Button mBtn_finalizar;

    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;

    private String id_publi;
    private String precio;
    private TextInputEditText mComentario;
    private TextView mPrecio;

    //base de datos de usuario para almacenar el comentario
    private DatabaseReference mData;
    private DatabaseReference mData_servicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_cobro);
        mPrecio=findViewById(R.id.precio_servicio);
        id_publi=getIntent().getStringExtra("id_publi");
        precio=getIntent().getStringExtra("precio");
        mPrecio.setText(precio+" COP");

        mComentario=findViewById(R.id.comentario);
        mNombre_conductor=findViewById(R.id.nombre_conductor_cobro);
        mBtn_finalizar=findViewById(R.id.btn_finalizar);

        mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
        String nombre_bd=mPref.getString("nombre","");

        String telefono_bd=mPref.getString("telefono","");
        if(!nombre_bd.equals("")&& !telefono_bd.equals("")){

            mNombre_conductor.setText(nombre_bd);

        }
        mBtn_finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Guardar_comentario= mComentario.getText().toString();
                if(id_publi!=null){
                    mData= FirebaseDatabase.getInstance().getReference().child("registros").child("conductores").child(id_publi).child("historial_comentarios").child(telefono_bd);
                    mData_servicio = FirebaseDatabase.getInstance().getReference().child("servicios").child(telefono_bd);
                    mData_servicio.removeValue();
                    if(!Guardar_comentario.equals("")){
                        HashMap<String,Object> registro= new HashMap<>();
                        registro.put("mensaje","enviado");
                        registro.put("nota",Guardar_comentario);
                        //acutalizamos el registro con update children
                        mData.setValue(registro);

                    }

                }



                Intent intent=new Intent(pantalla_cobro.this, plataforma.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setAction(Intent.ACTION_RUN);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}