package com.company.Onix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.company.Onix.providers.GoogleApiProvider;
import com.company.Onix.services.servicio_pantallas;
import com.company.Onix.utils.CarMoveAnim;
import com.company.Onix.utils.DecodePoints;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Callback;

public class pantalla_servicio extends AppCompatActivity implements OnMapReadyCallback {
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    private String mi_telefono;
    private String mi_nombre;
    private DatabaseReference mData;
    private DatabaseReference mData_tabla;

    private DatabaseReference mData_posicion_conductor;
    private ValueEventListener mListener;

    private LatLng mDriverLatLng;
    private LatLng mCliente_posicion;
    private LatLng mDestinoLatLng;

    private Marker mMarkerDriver;
    private boolean mIsFristTime = true;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    LatLng mStartLatLng;
    LatLng mEndLatLng;


    //datos conductor

    private TextView mNombre_conductor;
    private TextView mVehiculo_conductor;
    private TextView mPlaca_conductor;
    private TextView mColor_conductor;
    private CircleImageView mFoto_conductor;
    private String mFoto_ruta;
    private DatabaseReference mData_conductor;
    private Button mBtn_cancelar_servicio;

    //datos ruta
    private PolylineOptions mPolylineOptions;
    private List<LatLng> mPolylineList;
    private GoogleApiProvider mGoogleApiProvider;

    private Boolean mMostrar_ruta = true;

    private TextView mMi_estado_servicio;

    //consultar estados en tiempo real

    private DatabaseReference mData_estados;
    private ValueEventListener mListener_estado;

    private String mtelefono_conductor;
    private String mEstado;
    private String mprecio;

    private CircleImageView mBtn_llamar_dos;
    private static final int REQUEST_PERMISSION_CALL = 100;

    private CircleImageView mbtn_whatsapp;
    private TextToSpeech cosa;

    private DatabaseReference mData_consulta_dos;
    private String micono;

    private DatabaseReference mDatabase_cuatro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_servicio);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        mGoogleApiProvider = new GoogleApiProvider(pantalla_servicio.this);

        mPref = getApplicationContext().getSharedPreferences("sessiones", MODE_PRIVATE);
        String telefono_bd = mPref.getString("telefono", "");
        String nombre = mPref.getString("nombre", "");
        String ciudad = mPref.getString("mi_ciudad", "");
        if (!telefono_bd.equals("")) {
            mi_telefono = telefono_bd;
            mi_nombre = nombre;
            mData = FirebaseDatabase.getInstance().getReference().child(ciudad).child("servicios").child(telefono_bd);
            mData_tabla = FirebaseDatabase.getInstance().getReference().child(ciudad).child("postulaciones").child(telefono_bd).child("tabla_aceptados");
            mData_estados = FirebaseDatabase.getInstance().getReference().child(ciudad).child("servicios").child(telefono_bd);
            mData_consulta_dos = FirebaseDatabase.getInstance().getReference().child(ciudad).child("servicios").child(telefono_bd);
            consulta_base();
        }


        mbtn_whatsapp = findViewById(R.id.btn_whatsap);
        mbtn_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mtelefono_conductor != null) {
                    abrir_whatsapp(mtelefono_conductor);
                } else {
                    Toast.makeText(pantalla_servicio.this, "No se cargo el numero presiona de nuevo", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mMi_estado_servicio = findViewById(R.id.mi_estado_servicio);
        mNombre_conductor = findViewById(R.id.nombre_conductor);
        mVehiculo_conductor = findViewById(R.id.vehiculo);
        mPlaca_conductor = findViewById(R.id.placa_conductor);
        mBtn_llamar_dos = findViewById(R.id.btn_llamar);
        mFoto_conductor = findViewById(R.id.imagen_conductor_servicio);
        mBtn_cancelar_servicio = findViewById(R.id.btn_cancelar_servicio);
        mBtn_cancelar_servicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEstado != null) {
                    if (mEstado.equals("El conductor cancelo el servicio")) {
                        parar_alertas();
                        mData_estados.removeEventListener(mListener_estado);
                        mData_tabla.removeValue();
                        mData.removeValue();
                        volver();

                    } else {
                        if (mEstado.equals("confirmado") || mEstado.equals("aceptado")) {
                            alerta_cancelacion();

                        } else {
                            Toast.makeText(pantalla_servicio.this, "El servicio esta en curso no puedes cancelarlo", Toast.LENGTH_LONG).show();
                        }

                    }
                }
            }
        });
        mBtn_llamar_dos.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                llamar_cliente(mtelefono_conductor);

            } else {// api mayor a nivel 23
                if (ContextCompat.checkSelfPermission(pantalla_servicio.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                    if (mtelefono_conductor.equals("")) {
                        Toast.makeText(pantalla_servicio.this, "No se cargo el nùmero para llamar", Toast.LENGTH_SHORT).show();

                    } else {
                        llamar_cliente(mtelefono_conductor);
                    }

                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(pantalla_servicio.this, Manifest.permission.CALL_PHONE)) {
                        //retorna true si rechazo


                    } else {
                        Toast.makeText(pantalla_servicio.this, "Solicitando permiso de llamada", Toast.LENGTH_SHORT).show();
                        if (mtelefono_conductor.equals("")) {
                            llamar_cliente(mtelefono_conductor);
                        }


                    }
                    ActivityCompat.requestPermissions(pantalla_servicio.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
                }
            }


        });


    }

    private void abrir_whatsapp(String mtelefono_conductor) {
        if (mtelefono_conductor != null) {
            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=+57" + mtelefono_conductor + "&text=hola");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void llamar_cliente(String numero_de_telefono) {
        if (ContextCompat.checkSelfPermission(pantalla_servicio.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + numero_de_telefono));
            if (numero_de_telefono != "") {
                startActivity(intent);
            }

        }
    }

    public void alerta_cancelacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(pantalla_servicio.this);
        builder.setMessage("¿Quieres cancelar el servicio?").setTitle("CANCELAR SERVICIO")
                .setPositiveButton("CANCELAR SERVICIO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        parar_alertas();

                        mData_estados.removeEventListener(mListener_estado);
                        mData.removeValue();
                        mData_tabla.removeValue();
                        volver();


                    }


                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                }).create().show();
    }

    private void consulta_estado() {
        mListener_estado = mData_estados.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String estado = snapshot.child("estado").getValue().toString();
                    String precio = snapshot.child("precio").getValue().toString();
                    String descuento = snapshot.child("descuento").getValue().toString();
                    int precio_numero = Integer.parseInt(precio);
                    int decuento_numero = Integer.parseInt(descuento);
                    int precio_pagar = precio_numero - decuento_numero;

                    mprecio = String.valueOf(precio_pagar);
                    mEstado = estado;
                    mMi_estado_servicio.setText(estado);

                    if (estado.equals("confirmado")) {
                        cosa = new TextToSpeech(getBaseContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status != TextToSpeech.ERROR) {
                                    cosa.setLanguage(Locale.getDefault());

                                    String miTexto = mi_nombre + " el conductor ya se encuentra en tu domicilio";
                                    cosa.speak(miTexto, cosa.QUEUE_FLUSH, null, null);
                                }


                            }
                        });

                    }
                    if (estado.equals("viaje iniciado")) {
                        cosa = new TextToSpeech(getBaseContext(), status -> {
                            if (status != TextToSpeech.ERROR) {
                                cosa.setLanguage(Locale.getDefault());

                                String miTexto = mi_nombre + " el conductor inicio el servicio";
                                cosa.speak(miTexto, cosa.QUEUE_FLUSH, null, null);
                            }


                        });

                    }
                    if (estado.equals("cobrando")) {
                        cosa = new TextToSpeech(getBaseContext(), status -> {
                            if (status != TextToSpeech.ERROR) {
                                cosa.setLanguage(Locale.getDefault());
                                String miTexto = mi_nombre + " Gracias por usar el servicio de onix";
                                cosa.speak(miTexto, cosa.QUEUE_FLUSH, null, null);
                                parar_alertas();
                                mData_estados.removeEventListener(mListener_estado);
                                mData_posicion_conductor.removeEventListener(mListener);
                                mPref = getApplicationContext().getSharedPreferences("sessiones", MODE_PRIVATE);
                                mEditor = mPref.edit();
                                mEditor.putString("pantalla", "plataforma");
                                mEditor.apply();
                                Intent intent = new Intent(pantalla_servicio.this, pantalla_cobro.class);
                                intent.putExtra("id_publi", mtelefono_conductor);
                                intent.putExtra("precio", mprecio);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.setAction(Intent.ACTION_RUN);
                                startActivity(intent);
                            }
                        });
                    }

                    if (estado.equals("El conductor cancelo el servicio")) {
                        cosa = new TextToSpeech(getBaseContext(), status -> {
                            if (status != TextToSpeech.ERROR) {
                                cosa.setLanguage(Locale.getDefault());
                                if (mNombre_conductor.getText().toString().equals("cargando...")) {
                                    String miTexto = "El conductor  CANCELO EL SERVICIO," + mi_nombre + " ¿si quieres te podemos pedir otro vehículo?";
                                    cosa.speak(miTexto, cosa.QUEUE_FLUSH, null, null);
                                } else {
                                    String miTexto = "El conductor " + mNombre_conductor.getText().toString() + " CANCELO EL SERVICIO," + mi_nombre + " ¿si quieres te podemos pedir otro vehículo?";
                                    cosa.speak(miTexto, cosa.QUEUE_FLUSH, null, null);
                                }
                                alerta_conductor_cancela();
                            }
                        });
                    }
                } else {
                    mData_tabla.removeValue();
                    mData_estados.removeEventListener(mListener_estado);
                    parar_alertas();
                    volver();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void alerta_conductor_cancela() {

        AlertDialog.Builder builder = new AlertDialog.Builder(pantalla_servicio.this);
        builder.setMessage("¿Quieres buscar otro conductor?").setTitle("SERVICIO CANCELADO POR EL CONDUCTOR")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Toast.makeText(pantalla_servicio.this, "ok buscaremos otro", Toast.LENGTH_SHORT).show();

                        mData_estados.removeEventListener(mListener_estado);
                        parar_alertas();
                        escucuchar_alertas();
                        HashMap<String, Object> registro = new HashMap<>();

                        if (micono != null) {
                            if (micono.equals("")) {
                                registro.put("estado", "vip");
                            }
                            if (micono.equals("moto")) {
                                registro.put("estado", "vip");
                            }
                            if (micono.equals("carro")) {
                                registro.put("estado", "carro");
                            }
                        } else {
                            registro.put("estado", "vip");
                        }

                        registro.put("telefono_conductor", "");

                        mData.updateChildren(registro);

                        mData_tabla.removeValue();


                        if (mData_posicion_conductor != null) {
                            mData_posicion_conductor.removeEventListener(mListener);
                        }
                        Intent intent = new Intent(pantalla_servicio.this, pantalla_esperando.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setAction(Intent.ACTION_RUN);
                        startActivity(intent);


                    }


                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        parar_alertas();
                        mData_estados.removeEventListener(mListener_estado);
                        mData.removeValue();
                        volver();

                    }
                }).create().show();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        mData_consulta_dos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("modo")) {
                        micono = snapshot.child("modo").getValue().toString();
                    } else {
                        micono = "";
                    }

                    String estado = snapshot.child("estado").getValue().toString();

                    String precio = snapshot.child("precio").getValue().toString();
                    String descuento = snapshot.child("descuento").getValue().toString();
                    int precio_numero = Integer.parseInt(precio);
                    int decuento_numero = Integer.parseInt(descuento);
                    int precio_pagar = precio_numero - decuento_numero;

                    mprecio = String.valueOf(precio_pagar);

                    mEstado = estado;
                    if (estado.equals("El conductor cancelo el servicio")) {
                        alerta_conductor_cancela();
                    }


                    if (estado.equals("cobrando")) {
                        parar_alertas();
                        mData_estados.removeEventListener(mListener_estado);
                        mData_posicion_conductor.removeEventListener(mListener);
                        mPref = getApplicationContext().getSharedPreferences("sessiones", MODE_PRIVATE);
                        mEditor = mPref.edit();
                        mEditor.putString("pantalla", "plataforma");
                        mEditor.apply();
                        Intent intent = new Intent(pantalla_servicio.this, pantalla_cobro.class);
                        intent.putExtra("id_publi", mtelefono_conductor);
                        intent.putExtra("precio", mprecio);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setAction(Intent.ACTION_RUN);
                        startActivity(intent);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void consulta_base() {
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    consulta_estado();
                    if (snapshot.hasChild("modo")) {
                        micono = snapshot.child("modo").getValue().toString();
                    } else {
                        micono = "";
                    }

                    String estado = snapshot.child("estado").getValue().toString();
                    mMi_estado_servicio.setText(estado);
                    String telefono_conductor = snapshot.child("telefono_conductor").getValue().toString();
                    mtelefono_conductor = telefono_conductor;
                    mDatabase_cuatro = FirebaseDatabase.getInstance().getReference().child("registros").child("usuarios").child(mi_telefono).child("historial_de_viaje").child(mtelefono_conductor);


                    double lat_cliente = Double.parseDouble(snapshot.child("lat").getValue().toString());
                    double lng_cliente = Double.parseDouble(snapshot.child("lng").getValue().toString());
                    double destino_lat = Double.parseDouble(snapshot.child("lat_destino").getValue().toString());
                    double destino_lng = Double.parseDouble(snapshot.child("lng_destino").getValue().toString());
                    mCliente_posicion = new LatLng(lat_cliente, lng_cliente);
                    mDestinoLatLng = new LatLng(destino_lat, destino_lng);
                    if (mDestinoLatLng != null && mCliente_posicion != null) {
                        Ruta_destino();
                    }

                    if (!telefono_conductor.equals("")) {
                        mData_conductor = FirebaseDatabase.getInstance().getReference().child("registros").child("conductores").child(telefono_conductor);
                        datos_conductor();

                        mData_posicion_conductor = FirebaseDatabase.getInstance().getReference().child("conectados").child(telefono_conductor);
                        mListener = mData_posicion_conductor.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    double lat = Double.parseDouble(snapshot.child("lat").getValue().toString());
                                    double lng = Double.parseDouble(snapshot.child("lng").getValue().toString());
                                    mDriverLatLng = new LatLng(lat, lng);


                                    if (mMostrar_ruta == true) {
                                        if (mDriverLatLng != null && mCliente_posicion != null) {
                                            drawRoute();

                                            mMostrar_ruta = false;
                                        }


                                    }


                                    if (mIsFristTime) {

                                        if (micono.equals("")) {
                                            mMarkerDriver = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(lat, lng))
                                                    .title("Tu Conductor")
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.motorbike)));
                                            mIsFristTime = false;
                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                                    new CameraPosition.Builder()
                                                            .target(mDriverLatLng).zoom(14f)
                                                            .build()
                                            ));

                                        }

                                        if ((micono.equals("moto"))) {
                                            mMarkerDriver = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(lat, lng))
                                                    .title("Tu Conductor")
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.motorbike)));
                                            mIsFristTime = false;
                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                                    new CameraPosition.Builder()
                                                            .target(mDriverLatLng).zoom(14f)
                                                            .build()
                                            ));

                                        }
                                        if (micono.equals("carro")) {
                                            mMarkerDriver = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(lat, lng))
                                                    .title("Tu Conductor")
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_carro)));
                                            mIsFristTime = false;
                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                                    new CameraPosition.Builder()
                                                            .target(mDriverLatLng).zoom(14f)
                                                            .build()
                                            ));

                                        }


                                    }

                                    if (mStartLatLng != null) {
                                        mEndLatLng = mStartLatLng;
                                    }

                                    mStartLatLng = new LatLng(lat, lng);

                                    if (mEndLatLng != null) {
                                        CarMoveAnim.carAnim(mMarkerDriver, mEndLatLng, mStartLatLng);

                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                                new CameraPosition.Builder()
                                                        .target(mDriverLatLng).zoom(14f)
                                                        .build()
                                        ));

                                    }


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                } else {
                    parar_alertas();
                    volver();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void Ruta_destino() {
        if (mCliente_posicion != null && mDestinoLatLng != null) {
            // mMap.addMarker(new MarkerOptions().position(mDestinoLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.bandera)));
        }
    }

    private void volver() {
        if (mData_posicion_conductor != null) {
            mData_posicion_conductor.removeEventListener(mListener);
        }
        parar_alertas();
        mPref = getApplicationContext().getSharedPreferences("sessiones", MODE_PRIVATE);
        mEditor = mPref.edit();
        mEditor.putString("pantalla", "plataforma");
        mEditor.apply();
        //el servicio se elimino de la base de datos
        Intent intent = new Intent(pantalla_servicio.this, plataforma.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Intent.ACTION_RUN);
        startActivity(intent);
    }

    private void drawRoute() {
        if (mCliente_posicion != null && mDriverLatLng != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(mCliente_posicion)
                    .title("Usuario")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_pin)));
        }
    }

    private void datos_conductor() {
        mData_conductor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nombre_conductor = snapshot.child("nombre").getValue().toString();
                    String vehiculo = snapshot.child("vehiculo").getValue().toString();
                    String placa = snapshot.child("placa").getValue().toString();
                    String foto = snapshot.child("foto").getValue().toString();
                    String foto_ruta = snapshot.child("foto").getValue().toString();
                    mFoto_ruta = foto_ruta;

                    Picasso.get().load(foto).into(mFoto_conductor);

                    mNombre_conductor.setText(nombre_conductor);
                    mVehiculo_conductor.setText(vehiculo);
                    mPlaca_conductor.setText("Placa: " + placa);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);


    }

    private void escucuchar_alertas() {
        Intent serviceIntent = new Intent(this, servicio_pantallas.class);
        ContextCompat.startForegroundService(pantalla_servicio.this, serviceIntent);
    }

    private void parar_alertas() {

        Intent serviceIntent = new Intent(this, servicio_pantallas.class);
        stopService(serviceIntent);
    }

}