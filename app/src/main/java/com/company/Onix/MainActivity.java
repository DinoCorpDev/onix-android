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
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    private Button mBtn_ingresar;
    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            startLocation();
        } else {
            Toast.makeText(this, "Por varo verifica tu conexion a internet", Toast.LENGTH_LONG).show();
        }

        mBtn_ingresar = findViewById(R.id.btn_ingresar);
        mBtn_ingresar.setOnClickListener(v -> iniciar_sistema());

    }


    private void showAlertDialogoNOGPS(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicación de alta precisión para continuar")
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
                iniciar_sistema();
            }
            else{
                checkLocationPermissions();
            }
        }else {
            if (gpsActived()) {
                iniciar_sistema();
            }
            else {
                showAlertDialogoNOGPS();
            }
        }
    }

    int indexServices=0;
    private void readServices() {

        String ciudad = mPref.getString("mi_ciudad", "");
        String telefono_bd = mPref.getString("telefono", "");
        String pantalla=mPref.getString("pantalla","");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(ciudad)
                .child("servicios");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        if (key.contains(telefono_bd)) {
                            indexServices += 1;
                        }
                    }
                }
                if(!pantalla.equals("")){

                    if(pantalla.equals("plataforma")){
                        Intent intent=new Intent(MainActivity.this, plataforma.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setAction(Intent.ACTION_RUN);
                        startActivity(intent);
                    }

                    if (pantalla.equals("servicio") && indexServices == 1){
                        Intent intent=new Intent(MainActivity.this, pantalla_servicio.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setAction(Intent.ACTION_RUN);
                        startActivity(intent);
                    }else if(indexServices > 1){
                        Intent intent=new Intent(MainActivity.this, plataforma.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setAction(Intent.ACTION_RUN);
                        startActivity(intent);
                    }

                    if (pantalla.equals("registro")){
                        Intent intent=new Intent(MainActivity.this, pantalla_registro.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setAction(Intent.ACTION_RUN);
                        startActivity(intent);
                    }
                }else {
                    Intent intent =new Intent(MainActivity.this,verificacion.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void iniciar_sistema() {
        mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
        readServices();
    }

    private void checkLocationPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this).setTitle("Por favor proporciona los permisos de localizacion")
                        .setMessage("Esta aplicacion requiere de los permisos de ubicaciòn para poder utillizarse")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            }
            else {

                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciar_sistema();

            }
        }

    }





}
