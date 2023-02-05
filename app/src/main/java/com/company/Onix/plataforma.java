package com.company.Onix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.company.Onix.Modelos.DriverLocation;
import com.company.Onix.providers.GeofireProvider;
import com.company.Onix.utils.CarMoveAnim;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class plataforma extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationRequest mLocationRequest;
    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;
    private FusedLocationProviderClient mFusedLocation;
    private LatLng mCurrentLatLng;
    private GoogleMap.OnCameraIdleListener mCameraListener;
    private String mOrigen;
    private LatLng mOrigenLatLng;
    private boolean mOriginSelect=true;
    private  String mFijar_camara="no";
    private String mCiudad_origen;
    private  TextView mDireccion;
    private LatLng mPosicionOriginal;
    private boolean mIsFristTime = true;
    private LinearLayout mMenu;

    private Button mBtn_solicitar;


    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;





    private TextInputEditText mNota;

    //place
    private AutocompleteSupportFragment mAutocomplete;
    private AutocompleteSupportFragment mAutocomplete_destino;

    private Marker mMarker;
    private PlacesClient mPlaces;



    private String mi_telefono;
    private String mi_nombre;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;

    //nuevas variebles
    private  ImageView micono_central;
    private ImageView micono_central_dos;

    private LinearLayout mVentana_origen;
    private LinearLayout mVentana_destino;

    private LatLng mDestinationLatLng;
    private String mDestination;

    private Button mSolicitar_detalle;

    //prueba carga de vehiculos
    private GeofireProvider mGeofireProvider;
    private List<Marker> mDriversMarkers = new ArrayList<>();
    private ArrayList<DriverLocation> mDriversLocation=new ArrayList<>();

    private DatabaseReference mData_servidor;

    private DatabaseReference mData_servicio;
    private DatabaseReference mData_postular;


    //nuevas variables
    private DatabaseReference mData_usuario;


    //variable api
    private String mApi;
    private String mActivar_buscador="no";

    private TextView mDinero_bono;

    private LinearLayout mBtn_ubicar;
    private LinearLayout mBtn_regalo_nuevo;

    //variable bono
    private String mActivar_bono="";

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (mIsFristTime) {
                        getActiveDrivers();
                        mIsFristTime = false;
                        limitSearch();
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .zoom(18f)
                                        .build()

                        ));


                    }



                }
            }
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plataforma);
        permiso_super_poner();
        mGeofireProvider = new GeofireProvider("active_drivers");

        micono_central = findViewById(R.id.icono_central);
        micono_central_dos = findViewById(R.id.icono_central_dos);
        mVentana_origen = findViewById(R.id.cuadro_origen);
        mVentana_destino = findViewById(R.id.cuadro_destino);
        mSolicitar_detalle = findViewById(R.id.btn_solicar_detalle);
        mDinero_bono=findViewById(R.id.dinero_bono);
        mBtn_ubicar=findViewById(R.id.btn_ubicar);
        mBtn_regalo_nuevo=findViewById(R.id.btn_regalo_nuevo);
        mPref = getApplicationContext().getSharedPreferences("sessiones", MODE_PRIVATE);
        String telefono_bd = mPref.getString("telefono", "");
        String nombre = mPref.getString("nombre", "");
        String ciudad=mPref.getString("mi_ciudad","");
        if(ciudad.equals("")){
        Intent intent=new Intent(plataforma.this,pantalla_ciudad.class);
        startActivity(intent);
        }

        mData_servidor = FirebaseDatabase.getInstance().getReference().child("a_servidor");
        mData_servidor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String version_apk = "20";
                    String version_conductor = snapshot.child("version_cliente").getValue().toString();
                    String link_apk = snapshot.child("playstore_usuario").getValue().toString();
                    String mi_api=snapshot.child("a_api").getValue().toString();
                    String activar_buscador=snapshot.child("activar_buscador").getValue().toString();
                    mActivar_buscador=activar_buscador;
                    String activar_bono=snapshot.child("activar_nuevo_bono").getValue().toString();
                    mActivar_bono=activar_bono;
                    mApi=mi_api;

                    instanceAutocompleteOrigin();

                    if(mActivar_bono.equals("si")){
                        mBtn_regalo_nuevo.setVisibility(View.VISIBLE);
                    }
                    else{
                        mBtn_regalo_nuevo.setVisibility(View.INVISIBLE);
                    }

                    if (!version_conductor.equals(version_apk)) {
                        Toast.makeText(plataforma.this, "Actualizar sistema", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(plataforma.this, pantalla_actualizacion.class);
                        intent.putExtra("link_apk", link_apk);
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

        if (!telefono_bd.equals("")) {

            mi_telefono = telefono_bd;
            mi_nombre = nombre;
            mData_servicio=FirebaseDatabase.getInstance().getReference().child(ciudad).child("servicios").child(telefono_bd);
            mData_servicio.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String estado=snapshot.child("estado").getValue().toString();
                        if(estado.equals("vip")){
                            Intent intent=new Intent(plataforma.this, pantalla_esperando.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setAction(Intent.ACTION_RUN);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            mData_usuario=FirebaseDatabase.getInstance().getReference().child("registros").child("usuarios").child(telefono_bd);
            mData_usuario.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String verificacion=snapshot.child("verificacion").getValue().toString();

                        if(snapshot.hasChild("ciudad")){

                        }else {
                            Intent intent=new Intent(plataforma.this,pantalla_ciudad.class);
                            startActivity(intent);
                        }

                        if(snapshot.hasChild("nombre")){

                        }else {
                            mData_usuario.removeValue();
                            mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
                            mEditor=mPref.edit();
                            mEditor.putString("pantalla","");
                            mEditor.apply();
                            Intent intent=new Intent(plataforma.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setAction(Intent.ACTION_RUN);
                            startActivity(intent);
                        }

                        if(verificacion.equals("suspendido")){
                            mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
                            mEditor=mPref.edit();

                            mEditor.putString("pantalla","");

                            mEditor.apply();

                            Toast.makeText(plataforma.this, "cuenta eliminada por el admin", Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(plataforma.this, pantalla_supendido.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setAction(Intent.ACTION_RUN);
                            startActivity(intent);
                        }

                        if(snapshot.hasChild("nuevo_bono")){
                            String mi_bono=snapshot.child("nuevo_bono").getValue().toString();
                            mDinero_bono.setText("$ "+mi_bono);


                            if(mi_bono.length()==1){


                                String pesos=mi_bono.substring(0,1);

                                mDinero_bono.setText("$ "+pesos);
                            }

                            if(mi_bono.length()>1){

                                String pesos=mi_bono.substring(0,2);

                                mDinero_bono.setText("$ "+pesos);
                            }

                            if(mi_bono.length()>2){

                                String pesos=mi_bono.substring(0,3);

                                mDinero_bono.setText("$ "+pesos);
                            }

                            if(mi_bono.length()>3){
                                String miles= mi_bono.substring(0,1);
                                String pesos=mi_bono.substring(1,4);

                                mDinero_bono.setText("$ "+miles+"."+pesos);



                            }
                            if(mi_bono.length()>4){

                                String miles= mi_bono.substring(0,2);
                                String pesos=mi_bono.substring(2,5);

                                mDinero_bono.setText("$ "+miles+"."+pesos);

                            }
                            if(mi_bono.length()>5){
                                String miles= mi_bono.substring(0,3);
                                String pesos=mi_bono.substring(3,6);

                                mDinero_bono.setText("$ "+miles+"."+pesos);

                            }


                            if(mi_bono.length()>6){
                                String millon=mi_bono.substring(0,1);
                                String miles= mi_bono.substring(1,4);
                                String pesos=mi_bono.substring(4,7);

                                mDinero_bono.setText("$ "+millon+"."+miles+"."+pesos);

                            }




                            int mi_bono_numero=Integer.parseInt(mi_bono);

                            if(mActivar_bono.equals("si")){

                                if(mi_bono_numero>=1000){
                                    //  Toast.makeText(plataforma.this, "Recuerda que tienes uno bono disponible de: "+mi_bono_numero+" pesos", Toast.LENGTH_LONG).show();
                                    mBtn_regalo_nuevo.setVisibility(View.VISIBLE);
                                }else {
                                    mBtn_regalo_nuevo.setVisibility(View.INVISIBLE);
                                }
                            }

                        }else {
                            HashMap<String,Object> registro= new HashMap<>();
                            //datos normales
                            registro.put("nuevo_bono",10000);
                            mData_usuario.updateChildren(registro);
                            mDinero_bono.setText("$ 10.000");
                            mBtn_regalo_nuevo.setVisibility(View.VISIBLE);

                        }
                    }else {
                        mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
                        mEditor=mPref.edit();
                        mEditor.putString("pantalla","");
                        mEditor.apply();
                        Intent intent=new Intent(plataforma.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setAction(Intent.ACTION_RUN);
                        startActivity(intent);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });











        }else {
            Intent intent=new Intent(plataforma.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setAction(Intent.ACTION_RUN);

            startActivity(intent);
        }

        mBtn_regalo_nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(plataforma.this,pantalla_regalo.class);
                startActivity(intent);
            }
        });

        mBtn_ubicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMap!=null){
                    if(mCurrentLatLng.latitude!=0&& mCurrentLatLng.longitude!=0){
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(new LatLng(mCurrentLatLng.latitude, mCurrentLatLng.longitude))
                                        .zoom(18f)
                                        .build()

                        ));
                    }
                }
            }
        });





        mBtn_solicitar=findViewById(R.id.btn_solicitar);
        mBtn_solicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
                String nombre=mPref.getString("nombre","");
                String telefono=mPref.getString("telefono","");


                if(nombre.equals("")||telefono.equals("")){
                    Intent intent =new Intent(plataforma.this,pantalla_principal.class);
                    startActivity(intent);

                }else {
                    mOriginSelect=false;
                    micono_central.setVisibility(View.INVISIBLE);
                    micono_central_dos.setVisibility(View.VISIBLE);

                    mVentana_origen.setVisibility(View.INVISIBLE);
                    mVentana_destino.setVisibility(View.VISIBLE);
                    if(mDestinationLatLng!=null){
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(mDestinationLatLng)
                                        .zoom(18f)
                                        .build()

                        ));
                    }

                }


            }
        });



        mMenu=findViewById(R.id.btn_menu);
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(plataforma.this,pantalla_menu.class);
                startActivity(intent);
            }
        });
        mDireccion=findViewById(R.id.mi_direccion);
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        mPlaces = Places.createClient(this);

        instanceAutocompleteOrigin();
        instanceAutocompleteDestino();

        onCamereMove();

        mSolicitar_detalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mOrigenLatLng !=null && mDestinationLatLng!=null){
                    Intent intent = new Intent(plataforma.this, pantalla_detalle.class);
                    intent.putExtra("origin_lat",mOrigenLatLng.latitude);
                    intent.putExtra("origin_lng",mOrigenLatLng.longitude);
                    intent.putExtra("destination_lat",mDestinationLatLng.latitude);
                    intent.putExtra("destination_lng",mDestinationLatLng.longitude);
                    intent.putExtra("origin",mOrigen);
                    intent.putExtra("destination",mDestination);
                    intent.putExtra("ciudad_origen",mCiudad_origen);
                    intent.putExtra("activar_bono",mActivar_bono);
                    startActivity(intent);
                }
            }
        });




    }

    @Override
    public void onBackPressed() {
        mOriginSelect=true;
        micono_central.setVisibility(View.VISIBLE);
        micono_central_dos.setVisibility(View.INVISIBLE);

        mVentana_origen.setVisibility(View.VISIBLE);
        mVentana_destino.setVisibility(View.INVISIBLE);
        if(mOrigenLatLng!=null){
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(mOrigenLatLng)
                            .zoom(18f)
                            .build()

            ));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.setOnCameraIdleListener(mCameraListener);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);

        startLocation();


    }


    private void showAlertDialogoNOGPS(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicación para continuar")
                .setPositiveButton("Configuraciòn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean gpsActived(){
        boolean isActive= false;
        LocationManager locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)){
            isActive= true;

        }
        return isActive;
    }

    private void startLocation(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if(gpsActived()){
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mMap.setMyLocationEnabled(true);
                }
                else{
                    showAlertDialogoNOGPS();
                }
            }
            else{
                checkLocationPermissions();
            }

        }else {
            if (gpsActived()) {
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }
            else {
                showAlertDialogoNOGPS();
            }
        }
    }

    private void checkLocationPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this).setTitle("Por favor proporciona los permisos de localizacion")
                        .setMessage("Esta aplicacion requiere de los permisos de ubicaciòn para poder utillizarse")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(plataforma.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            }
            else {
                ActivityCompat.requestPermissions(plataforma.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

            }
        }
    }



    private void  limitSearch(){
        LatLng northSide= SphericalUtil.computeOffset(mCurrentLatLng,2000,0);
        LatLng southSide= SphericalUtil.computeOffset(mCurrentLatLng,2000,180);
        mAutocomplete.setCountry("CO");
        mAutocomplete.setLocationBias(RectangularBounds.newInstance(southSide,northSide));

        mAutocomplete_destino.setCountry("CO");
        mAutocomplete_destino.setLocationBias(RectangularBounds.newInstance(southSide,northSide));


    }

    private void  instanceAutocompleteDestino(){
        mAutocomplete_destino = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_destino);
        mAutocomplete_destino.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutocomplete_destino.setHint("Lugar De destino");

        mAutocomplete_destino.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {


                mDestination = place.getName();

                mDestinationLatLng = place.getLatLng();
                Log.d("PLACE", "Name" + mDestination);
                Log.d("PLACE", "lat" + mDestinationLatLng.latitude);
                Log.d("PLACE", "lat" + mDestinationLatLng.longitude);
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(mDestinationLatLng)
                                .zoom(18f)
                                .build()

                ));








            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
    }


    private void  instanceAutocompleteOrigin(){
        mAutocomplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placeaAutocompleteOrigin);
        mAutocomplete.setHint("Lugar De Origen");

            if(mActivar_buscador.equals("si")){
                mAutocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));

                mAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(@NonNull Place place) {


                        mOrigen = place.getName();

                        mOrigenLatLng = place.getLatLng();
                        Log.d("PLACE", "Name" + mOrigen);
                        Log.d("PLACE", "lat" + mOrigenLatLng.latitude);
                        Log.d("PLACE", "lat" + mOrigenLatLng.longitude);
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(mOrigenLatLng)
                                        .zoom(18f)
                                        .build()

                        ));



                    }

                    @Override
                    public void onError(@NonNull Status status) {

                    }
                });
            }





    }





    private void onCamereMove(){
        if(mFijar_camara.equals("no")) {

            mCameraListener = new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    try {
                        Geocoder geocoder = new Geocoder(plataforma.this);

                        if (mOriginSelect && mFijar_camara.equals("no")) {
                            mOrigenLatLng = mMap.getCameraPosition().target;
                            List<Address> addressList = geocoder.getFromLocation(mOrigenLatLng.latitude, mOrigenLatLng.longitude, 1);
                            String city = addressList.get(0).getLocality();
                            String country = addressList.get(0).getCountryName();
                            String address = addressList.get(0).getAddressLine(0);

                            String numero_casa = addressList.get(0).getFeatureName();
                            String calle = addressList.get(0).getThoroughfare();



                            mCiudad_origen = city;
                            if (mCiudad_origen == null) {
                                mCiudad_origen = "";
                            }
                            mOrigen = address + " " + city;
                            mAutocomplete.setText(address + " " + city);


                            if (calle != null) {
                                mDireccion.setText(calle + " #" + numero_casa);
                                mAutocomplete.setText(calle + " #" + numero_casa);


                            } else {

                                mDireccion.setText(numero_casa);

                            }


                            if (mPosicionOriginal == null) {
                                mPosicionOriginal = mOrigenLatLng;
                            }


                        }else {
                            mDestinationLatLng = mMap.getCameraPosition().target;
                            List<Address> addressList = geocoder.getFromLocation(mDestinationLatLng.latitude, mDestinationLatLng.longitude, 1);
                            String city = addressList.get(0).getLocality();
                            String country = addressList.get(0).getCountryName();
                            String address = addressList.get(0).getAddressLine(0);
                            // mDestination = address + " " + city;
                            mAutocomplete_destino.setText(address + " " + city);
                            mDestination = address + " " + city;
                            // mCiudad=city;
                            // if(mCiudad==null){
                            //   mCiudad="";
                            //}


                        }


                    } catch (Exception e) {
                        Log.d("Error:", "Mensaje camara error: " + e.getMessage());
                    }
                }
            };

        }

    }



    public void permiso_super_poner() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }


        }
    }

    //traer vehiculos
    private void getActiveDrivers() {
        mGeofireProvider.getActiveDrivers(mCurrentLatLng,10).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //añadir markadores de vehiculos
                for (Marker marker : mDriversMarkers) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            return;
                        }
                    }
                }

                LatLng driverLatLng = new LatLng(location.latitude, location.longitude);
                Marker marker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Conductor Disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.uber_car)));
                marker.setTag(key);
                mDriversMarkers.add(marker);

                DriverLocation driverLocation=new DriverLocation();
                driverLocation.setId(key);
                mDriversLocation.add(driverLocation);


                //  mostrar informacion de conductores al presionar  getDriversInfo();


            }

            @Override
            public void onKeyExited(String key) {
                for (Marker marker : mDriversMarkers) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            marker.remove();
                            mDriversMarkers.remove(marker);
                            mDriversLocation.remove(getPositionDriver(key));
                            return;
                        }
                    }
                }

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //actualizar vehiculos marcadores posicion
                for (Marker marker : mDriversMarkers) {

                    LatLng start= new LatLng(location.latitude,location.longitude);
                    LatLng end=null;

                    int position=getPositionDriver(marker.getTag().toString());

                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            if(mDriversLocation.get(position).getLatLng() !=null){
                                end=mDriversLocation.get(position).getLatLng();
                            }

                            mDriversLocation.get(position).setLatLng(new LatLng(location.latitude,location.longitude));

                            if(end !=null){
                                CarMoveAnim.carAnim(marker,end,start);

                            }

                        }
                    }
                }


            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }
    private int getPositionDriver(String id){
        int position=0;

        for(int i=0; i< mDriversLocation.size();i++){
            if(id.equals(mDriversLocation.get(i).getId())){
                position=i;
                break;
            }
        }

        return position;
    }

}
