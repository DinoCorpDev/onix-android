package com.company.Onix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.company.Onix.providers.GoogleApiProvider;
import com.company.Onix.services.servicio_pantallas;
import com.company.Onix.utils.DecodePoints;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class pantalla_detalle extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    private String mTelefono_conductor;

    private GoogleApiProvider mGoogleApiProvider;
    private PolylineOptions mPolylineOptions;
    private List<LatLng> mPolylineList;

    private Double lat_conductor;
    private Double lng_conductor;
    private LatLng mOriginLatLng;
    private  LatLng mPosicion_conductor;


    private String Extra_id;
    private String mExtraLat;
    private String mExtraLng;
    private TextView merror;
    private LatLngBounds latLngBounds;

    //mostrar_envios
    private String Extra_nombre;
    private String  Extra_direccion;
    private String Extra_km;


    private  TextView mostrar_km;
    private  TextView mostrar_nombre;
    private  TextView mostrar_direccion;

    //referencia base de datos

    private ValueEventListener mListener;
    private DatabaseReference mDAta;
    private DatabaseReference mData_comprobar;
    private DatabaseReference mDatabase_tres;

    //traer_los_extra
    private   double mExtra_lat_origen;
    private double mExta_lng_origen;
    private double mExtra_lat_destino;
    private double mExtra_lng_destino;

    private String mExtra_origen;
    private String mExtra_destino;

    private TextView mprueba;

    private TextView mOrigen_detalle;
    private TextView mDestino_detalle;

    //base de datos de tarifa

    private DatabaseReference mBase_servidor;

    private TextView mKm;
    private TextView mPrecio;
    private CircleImageView mBtn_atras_detalle;

    private Button mBtn_solicitar_servicio;
    private ProgressDialog progressDialog;
    private String mCiudad;
    private String mKilometros;
    private String mMinutos;

    private String mPrecio_moto_total;
    private String mPrecio_privado_total;
    private String mPrecio_carro_total;


    /// nuevas variables
    private DatabaseReference mData_usuario_detalle;
    private DatabaseReference mData_postular;

    private TextView mMi_bono;

    private int bono_numero=0;
    private int descuento=0;


    private  TextView mPrecio_servicio_privado;
    private  Button mBtn_solicitar_servicio_privado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_detalle);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Calculando tarifa por favor espere");
        progressDialog.show();
        // seguridad del conductor variables locales
        mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
        String ciudad=mPref.getString("mi_ciudad","");
        String telefono_bd=mPref.getString("telefono","");
        String nombre_bd=mPref.getString("nombre","");
        mCiudad=ciudad;
        mGoogleApiProvider = new GoogleApiProvider(pantalla_detalle.this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        mBase_servidor=FirebaseDatabase.getInstance().getReference().child("a_servidor");

        mMi_bono=findViewById(R.id.mi_bono);

        if(telefono_bd.equals("")|| telefono_bd==null){
            progressDialog.dismiss();
            Intent intent=new Intent(pantalla_detalle.this, pantalla_principal.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setAction(Intent.ACTION_RUN);

            startActivity(intent);

        }else {
            mTelefono_conductor=telefono_bd;


            mData_usuario_detalle=FirebaseDatabase.getInstance().getReference().child("a_servidor");
            mData_usuario_detalle.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.hasChild("a_bono")){
                            String bono=snapshot.child("a_bono").getValue().toString();
                            bono_numero=Integer.parseInt(bono);
                            mMi_bono.setText(bono);
                            if(bono_numero>=0){
                                descuento=0;
                            }


                        }else {
                            //no hay

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });









            mBtn_atras_detalle=findViewById(R.id.boton_atras_detalle);
            mOrigen_detalle=findViewById(R.id.origen_detalle);
            mDestino_detalle=findViewById(R.id.destino_detalle);
            mKm=findViewById(R.id.minutos_distancia);
            mPrecio=findViewById(R.id.precio);
            mExtra_origen=getIntent().getStringExtra("origin");
            mExtra_destino=getIntent().getStringExtra("destination");
            mExtra_lat_origen = getIntent().getDoubleExtra("origin_lat", 0);
            mExta_lng_origen = getIntent().getDoubleExtra("origin_lng", 0);
            mExtra_lat_destino = getIntent().getDoubleExtra("destination_lat", 0);
            mExtra_lng_destino = getIntent().getDoubleExtra("destination_lng", 0);



            mOriginLatLng=new LatLng(mExtra_lat_origen,mExta_lng_origen);
            mPosicion_conductor=new LatLng(mExtra_lat_destino,mExtra_lng_destino);


            Location locationA = new Location("punto A");

            locationA.setLatitude(mOriginLatLng.latitude);
            locationA.setLongitude(mOriginLatLng.longitude);

            Location locationB = new Location("punto B");

            locationB.setLatitude(mPosicion_conductor.latitude);
            locationB.setLongitude(mPosicion_conductor.longitude);

            float distance = locationA.distanceTo(locationB);

            float operacion=distance/1000;





            mOrigen_detalle.setText(mExtra_origen);
            mDestino_detalle.setText(mExtra_destino);
            mBtn_solicitar_servicio=findViewById(R.id.btn_solicitar_servicio);






            mBtn_solicitar_servicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mExtra_destino != null && telefono_bd != null && nombre_bd != null && mExtra_origen != null && mExtra_lat_origen != 0 && mExta_lng_origen != 0 && mExtra_lat_destino != 0 && mExtra_lng_destino != 0 && mPrecio != null) {

                        mData_postular = FirebaseDatabase.getInstance().getReference().child(ciudad).child("postulaciones").child(telefono_bd);
                        mData_postular.removeValue();

                        escucuchar_alertas();
                        //solicitar_el_servicio
                        DatabaseReference servicio = FirebaseDatabase.getInstance().getReference().child(mCiudad).child("servicios").child(telefono_bd);
                        HashMap<String, Object> registro = new HashMap<>();
                        registro.put("nota", "necesito una moto");
                        registro.put("destino", mExtra_destino);
                        registro.put("modo", "moto");
                        registro.put("minutos", mMinutos);
                        registro.put("kilometros", mKilometros);
                        registro.put("descuento", 0);
                        registro.put("telefono_usuario", telefono_bd);
                        registro.put("telefono_conductor", "");
                        registro.put("estado", "esperando");
                        registro.put("nombre", nombre_bd);
                        registro.put("direccion", mExtra_origen);
                        registro.put("lat", mExtra_lat_origen);
                        registro.put("lng", mExta_lng_origen);
                        registro.put("lat_destino", mExtra_lat_destino);
                        registro.put("lng_destino", mExtra_lng_destino);
                        registro.put("precio", mPrecio_moto_total);
                        servicio.setValue(registro);

                        Intent intent = new Intent(pantalla_detalle.this, pantalla_esperando.class);

                        startActivity(intent);


                    }else {
                        Toast.makeText(pantalla_detalle.this, "No se cargo bien la información", Toast.LENGTH_SHORT).show();
                    }
                }
            });



            mBtn_atras_detalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });



        }









    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }




    private void drawRoute() {
        if (mOriginLatLng != null && mPosicion_conductor != null) {

            mMap.addMarker(new MarkerOptions().position(mPosicion_conductor).title("Tu destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_final)));
            mMap.addMarker(new MarkerOptions().position(mOriginLatLng).title("Tu origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_ubicar_dos)));


            mGoogleApiProvider.getDirections(mOriginLatLng, mPosicion_conductor).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        JSONObject route = jsonArray.getJSONObject(0);
                        JSONObject polylines = route.getJSONObject("overview_polyline");
                        String points = polylines.getString("points");
                        mPolylineList = DecodePoints.decodePoly(points);
                        mPolylineOptions = new PolylineOptions();

                        mPolylineOptions.color(Color.DKGRAY);
                        mPolylineOptions.width(12f);
                        mPolylineOptions.geodesic(true);
                        mPolylineOptions.startCap(new SquareCap());
                        mPolylineOptions.jointType(JointType.ROUND);
                        mPolylineOptions.addAll(mPolylineList);
                        mMap.addPolyline(mPolylineOptions);

                        JSONArray legs = route.getJSONArray("legs");
                        JSONObject leg = legs.getJSONObject(0);
                        JSONObject distance = leg.getJSONObject("distance");
                        JSONObject duration = leg.getJSONObject("duration");

                        String distanceText = distance.getString("text");
                        String durationText = duration.getString("text");


                        String[] distanceAndKm = distanceText.split(" ");
                        double distanceValue = Double.parseDouble(distanceAndKm[0]);

                        String[] durationAndMins = durationText.split(" ");
                        double durationValue = Double.parseDouble(durationAndMins[0]);

                        mKilometros=distanceText;
                        mMinutos=durationText;

                        mKm.setText(durationText + " " + distanceText);
                        calculatePrice(distanceValue, durationValue);




                        if (mOriginLatLng.latitude > mPosicion_conductor.latitude && mOriginLatLng.longitude > mPosicion_conductor.longitude) {
                            latLngBounds = new LatLngBounds(mPosicion_conductor, mOriginLatLng);
                        } else if (mOriginLatLng.longitude > mPosicion_conductor.longitude) {

                            LatLng cosa = new LatLng(mExtra_lat_origen, mExtra_lng_destino);
                            LatLng cosa_dos = new LatLng(mExtra_lat_origen, mExta_lng_origen);
                            latLngBounds = new LatLngBounds(cosa, cosa_dos);

                        } else if (mOriginLatLng.latitude > mPosicion_conductor.latitude) {

                            LatLng cosa = new LatLng(mExtra_lat_destino, mExta_lng_origen);
                            LatLng cosa_dos = new LatLng(mExtra_lat_origen, mExtra_lng_destino);
                            latLngBounds = new LatLngBounds(cosa, cosa_dos);

                        } else {
                            latLngBounds = new LatLngBounds(mOriginLatLng, mPosicion_conductor);
                        }


                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 250));


                    } catch (Exception e) {
                        Log.d("Error", "Error encontrado" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }else{
            Toast.makeText(this, "problema con las varibles de ruta iniciar", Toast.LENGTH_SHORT).show();
        }

    }

    private void calculatePrice(double distanceValue, double durationValue) {
        mBase_servidor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    double precio_base = snapshot.child("a_precio_base").getValue().hashCode();
                    double precio_km = snapshot.child("a_precio_km").getValue().hashCode();
                    double precio_minutos = snapshot.child("a_precio_minuto").getValue().hashCode();
                    double precio_minimo = snapshot.child("a_precio_minimo").getValue().hashCode();

                    double precio_km_carro=snapshot.child("a_precio_km_carro").getValue().hashCode();
                    double precio_base_carro=snapshot.child("a_precio_base_carro").getValue().hashCode();
                    double precio_minimo_carro=snapshot.child("a_precio_minimo_carro").getValue().hashCode();


                    double operacion_suma_km=precio_km*distanceValue;
                    double operacion_suna_minutos=precio_minutos*durationValue;
                    double operacion_suma_total=operacion_suma_km+operacion_suna_minutos+precio_base;


                    double operacion_suma_km_carro=precio_km_carro*distanceValue;
                    double precio_carro=operacion_suma_km_carro+precio_base_carro;

                    if(distanceValue<=3 ){
                        operacion_suma_total=precio_minimo;
                        precio_carro=precio_minimo_carro;
                    }
                    if(operacion_suma_total<=precio_minimo){
                        operacion_suma_total=precio_minimo;

                    }

                    if(precio_carro<=4800){
                        precio_carro=precio_minimo_carro;
                    }


                    int precio_moto=(int)operacion_suma_total;
                    int precio_descuento=precio_moto-descuento;





                    int convertir_precio_carro=(int)precio_carro;



                    mPrecio_moto_total=String.valueOf(precio_moto);
                    mPrecio_carro_total=String.valueOf(convertir_precio_carro);




                    mPrecio.setText(String.valueOf(precio_moto)+" COP");


                    progressDialog.dismiss();
                    mBtn_solicitar_servicio.setEnabled(true);


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
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        drawRoute();


    }
    private void escucuchar_alertas(){
        Intent serviceIntent= new Intent(this, servicio_pantallas.class);
        ContextCompat.startForegroundService(pantalla_detalle.this,serviceIntent);

    }
    private void parar_alertas(){

        Intent serviceIntent= new Intent(this, servicio_pantallas.class);
        stopService(serviceIntent);
    }

}