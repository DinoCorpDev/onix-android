package com.company.Onix.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.company.Onix.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class servicio_pantallas extends Service {
    private DatabaseReference mData;
    public final String CHANNEL_ID = "com.example.bryft";
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    private String mi_telefono;
    private String mi_nombre;
    private TextToSpeech cosa;
    private ValueEventListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();

        mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
        String telefono_bd=mPref.getString("telefono_s","");
        String nombre=mPref.getString("nombre","");
        String ciudad=mPref.getString("mi_ciudad", "");

        /*if(!telefono_bd.equals("")){
            mi_telefono=telefono_bd;
            mi_nombre=nombre;
            mData=FirebaseDatabase.getInstance().getReference().child(ciudad).child("servicios").child(telefono_bd);
           mListener=mData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String estado=snapshot.child("estado").getValue().toString();

                        if(estado.equals("aceptado")){
                            cosa=new TextToSpeech(getBaseContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if(status!=TextToSpeech.ERROR){

                                        cosa.setLanguage(Locale.getDefault());
                                        String miTexto=mi_nombre+" un conductor de onix viene en camino";

                                        cosa.speak(miTexto,cosa.QUEUE_FLUSH,null,null);

                                        mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
                                        mEditor=mPref.edit();
                                        mEditor.putString("pantalla","servicio");
                                        mEditor.apply();
                                        mData.removeEventListener(mListener);






                                    }


                                }
                            });

                        }

                        if(estado.equals("confirmado")){
                            cosa=new TextToSpeech(getBaseContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if(status!=TextToSpeech.ERROR){

                                        cosa.setLanguage(Locale.getDefault());
                                        String miTexto=mi_nombre+" El conductor ya se encuentra en tu posicion";

                                        cosa.speak(miTexto,cosa.QUEUE_FLUSH,null,null);


                                    }


                                }
                            });

                        }

                        if(estado.equals("viaje iniciado")){
                            cosa=new TextToSpeech(getBaseContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if(status!=TextToSpeech.ERROR){

                                        cosa.setLanguage(Locale.getDefault());
                                        String miTexto=mi_nombre+" El conductor inicio el viaje";

                                        cosa.speak(miTexto,cosa.QUEUE_FLUSH,null,null);


                                    }


                                }
                            });

                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }*/


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.onix_icono2)
                .setContentTitle("ONIX")
                .setContentText("Facil y rapido")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            startMyForegroundService();

        }
        else{
            startForeground(50,notification);
        }





        return START_STICKY;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyForegroundService(){
        String channelName="My Foreground Service";
        NotificationChannel channel=new NotificationChannel(CHANNEL_ID,channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this,CHANNEL_ID);
        Notification notification = builder
                .setOngoing(true)
                .setSmallIcon(R.drawable.onix_icono2)
                .setContentTitle("ONIX")
                .setContentText("Facil y rapido")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(50,notification);


    }
}

