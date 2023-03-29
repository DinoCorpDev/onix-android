package com.company.Onix.services;

import androidx.annotation.NonNull;


import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {
    private static final int NOTIFICACION_CODE = 100;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

}
