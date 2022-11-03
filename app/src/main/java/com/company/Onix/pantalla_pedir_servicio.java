package com.company.Onix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class
pantalla_pedir_servicio extends AppCompatActivity {

    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    private LinearLayout mBtn_siguiente;
    private CircleImageView mBtn_atras;
    private TextInputEditText mNuevo_destino;
    private  double mExtra_lat_origen;
    private double mExta_lng_origen;
    private String mExtra_origen;
    private String mCiudad;
    private TextView mOrigen;
    private TextInputEditText mPrecio_cliente;
    private String mPrecio;
    private TextInputEditText mCliente_comentario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_pedir_servicio);

        mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
        String ciudad=mPref.getString("mi_ciudad","");
        String telefono_bd=mPref.getString("telefono","");
        String nombre_bd=mPref.getString("nombre","");
        mCiudad=ciudad;

        if(telefono_bd.equals("")|| telefono_bd==null){
            Intent intent=new Intent(pantalla_pedir_servicio.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setAction(Intent.ACTION_RUN);
            startActivity(intent);
        }else {
            mNuevo_destino=findViewById(R.id.destino_nuevo);
            mPrecio_cliente=findViewById(R.id.precio_servicio_cliente);
            mCliente_comentario=findViewById(R.id.cliente_comentario);
            mExtra_origen=getIntent().getStringExtra("origin");
            mExtra_lat_origen = getIntent().getDoubleExtra("origin_lat", 0);
            mExta_lng_origen = getIntent().getDoubleExtra("origin_lng", 0);
        }

        mBtn_atras=findViewById(R.id.btn_atras_destino);
        mBtn_atras.setOnClickListener(v -> finish());

        mBtn_siguiente=findViewById(R.id.btn_siguiente_modo);
        mBtn_siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destino=mNuevo_destino.getText().toString();
                mPrecio=mPrecio_cliente.getText().toString();
                String comentario=mCliente_comentario.getText().toString();

                if(destino.equals("")){
                    mNuevo_destino.requestFocus();
                    Toast.makeText(pantalla_pedir_servicio.this, "Digite su destino", Toast.LENGTH_SHORT).show();
                }else {
                    if(mPrecio.equals("")){
                        mPrecio_cliente.requestFocus();
                        Toast.makeText(pantalla_pedir_servicio.this, "Por favor ofrezca un precio para este servicio", Toast.LENGTH_SHORT).show();
                    }else {
                        int precio_numero=Integer.parseInt(mPrecio);
                        if(precio_numero<4000){
                            Toast.makeText(pantalla_pedir_servicio.this, "El servicio minimo debe de ser de 4.000 mil pesos", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(pantalla_pedir_servicio.this,pantalla_pedir_servicio_uno.class);
                            intent.putExtra("origin_lat",mExtra_lat_origen);
                            intent.putExtra("origin_lng",mExta_lng_origen);
                            intent.putExtra("destination_lat",mExtra_lat_origen);
                            intent.putExtra("destination_lng",mExta_lng_origen);
                            intent.putExtra("origin",mExtra_origen);
                            intent.putExtra("precio",mPrecio);
                            intent.putExtra("nota", comentario);
                            intent.putExtra("destino",mNuevo_destino.getText().toString());
                            startActivity(intent);
                        }

                    }

                }


            }
        });
    }
}