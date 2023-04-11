package com.company.Onix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class verificacion extends AppCompatActivity {
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    private Button mBtn_verificar_telefono;
    private Button mBtn_verificar_codigo;


    private TextView mMensaje;

    private  FirebaseAuth mAuth;


    private String mVerificacion;
    private PhoneAuthProvider.ForceResendingToken mToken;

    private TextInputEditText mNumero_telefono;
    private TextInputEditText mNumero_codigo;
    private LinearLayout mVentana_carga;
    private LinearLayout mVentana_celular;
    private LinearLayout mVentana_codigo;

    //base de datos de usuario
    private DatabaseReference mData;
    private ProgressDialog progressDialog;
    private LinearLayout mBtn_soporte_ingreso;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacion);

        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(verificacion.this)
                == ConnectionResult.SUCCESS) {
            // The SafetyNet Attestation API is available.
            Toast.makeText(this, "Ingresa tu teléfono celular", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Por favor actualiza los servicio de google play", Toast.LENGTH_SHORT).show();
            // Prompt user to update Google Play services.
            Intent intent=new Intent(verificacion.this,pantalla_intalar_servicios.class);
            startActivity(intent);
        }
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Comprobando ingreso a tu cuenta, por favor espere");

        mVentana_codigo=findViewById(R.id.ventana_codigo);
        mVentana_carga=findViewById(R.id.ventana_carga);
        mVentana_celular=findViewById(R.id.ventan_celular);
        mNumero_telefono=findViewById(R.id.numero_telefono_texto);
        mNumero_codigo=findViewById(R.id.numero_codigo);
        mBtn_verificar_codigo=findViewById(R.id.btn_verificar_codigo);
        mBtn_soporte_ingreso=findViewById(R.id.btn_soporte_ingreso);
        mBtn_verificar_codigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo=mNumero_codigo.getText().toString();
                if(codigo.equals("")){
                    mNumero_codigo.requestFocus();
                    Toast.makeText(verificacion.this, "Digite su código", Toast.LENGTH_SHORT).show();
                }else {
                    if(codigo.length()<6){
                        Toast.makeText(verificacion.this, "El código debe de ser de 6 digitos", Toast.LENGTH_LONG).show();
                    }else {
                        ocultar_teclado();
                        verificar_codigo(codigo);
                    }


                }
            }
        });

        mBtn_soporte_ingreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=+57" + "3009777955" + "&text=Hola%20soy%20cliente%20de%20ONIX%20tengo%20problemas%20para%20ingresar");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        mBtn_verificar_telefono=findViewById(R.id.btn_verificar_telefono);
        mBtn_verificar_telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String numero=mNumero_telefono.getText().toString();
                if(numero.equals("")){
                    mNumero_telefono.requestFocus();
                    Toast.makeText(verificacion.this, "Por favor digite su número", Toast.LENGTH_SHORT).show();
                }else {
                    if(numero.length()<10){
                        Toast.makeText(verificacion.this, "El número de teléfono debe de tener minimo 10 digitos ejemplo (304 44 44 549)", Toast.LENGTH_LONG).show();
                    }else {
                        mVentana_carga.setVisibility(View.VISIBLE);
                        mVentana_celular.setVisibility(View.INVISIBLE);
                        mVentana_codigo.setVisibility(View.INVISIBLE);
                        enviar_numero(numero);
                        Toast.makeText(verificacion.this, "Comprobando número", Toast.LENGTH_SHORT).show();

                    }

                }



            }
        });








    }

    private void enviar_numero(String numero){
        ocultar_teclado();
        mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true);

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                //Log.d(TAG, "onVerificationCompleted:" + credential);


                signInWithPhoneAuthCredential(credential);


                Toast.makeText(verificacion.this, "ya esta certificado", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.



                String mensaje="Problemas con el ingreso";
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    mensaje="Error de autentificacion verifique bien el número";


                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    mensaje="Tus intentos ya superaron el limite, por favor espera 5 minutos para volverlo a intentar";

                }
                progressDialog.dismiss();
                mVentana_celular.setVisibility(View.VISIBLE);
                mVentana_carga.setVisibility(View.INVISIBLE);
                mVentana_codigo.setVisibility(View.INVISIBLE);
                Toast.makeText(verificacion.this,mensaje , Toast.LENGTH_LONG).show();
                mBtn_soporte_ingreso.setVisibility(View.VISIBLE);




            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                mVerificacion = verificationId;
                mToken = token;
                Toast.makeText(verificacion.this, "Código enviado por sms al número de telefono", Toast.LENGTH_LONG).show();
                mVentana_celular.setVisibility(View.INVISIBLE);
                mVentana_carga.setVisibility(View.INVISIBLE);
                mVentana_codigo.setVisibility(View.VISIBLE);
            }
        };



        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+57"+numero)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);



    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Toast.makeText(verificacion.this, "Ingresando a onix", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            // Update UI

                            mData= FirebaseDatabase.getInstance().getReference().child("registros").child("usuarios").child(mNumero_telefono.getText().toString());
                            mData.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        String nombre=snapshot.child("nombre").getValue().toString();

                                        String foto=snapshot.child("foto").getValue().toString();


                                        //colocando nueva variable para identificar el cliente nuevo
                                        HashMap<String,Object> registro= new HashMap<>();
                                        //datos normales
                                        registro.put("actualizacion","si");
                                        mData.updateChildren(registro);

                                        mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
                                        mEditor=mPref.edit();

                                        mEditor.putString("foto",foto);
                                        mEditor.putString("nombre",nombre);

                                        mEditor.putString("telefono",mNumero_telefono.getText().toString());

                                        mEditor.putString("pantalla","plataforma");

                                        mEditor.apply();
                                        progressDialog.dismiss();
                                        //Toast.makeText(verificacion.this, "Ingresando a la plataforma", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(verificacion.this, plataforma.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.setAction(Intent.ACTION_RUN);
                                        startActivity(intent);



                                    }else {

                                        mPref=getApplicationContext().getSharedPreferences("sessiones",MODE_PRIVATE);
                                        mEditor=mPref.edit();
                                        mEditor.putString("telefono",mNumero_telefono.getText().toString());
                                        mEditor.putString("pantalla","registro");
                                        mEditor.apply();
                                        progressDialog.dismiss();
                                        Toast.makeText(verificacion.this, "Por favor registre su cuenta", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(verificacion.this, pantalla_registro.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.setAction(Intent.ACTION_RUN);
                                        startActivity(intent);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(verificacion.this, "El código no es valido", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


    private void  verificar_codigo(String codigo){
        progressDialog.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificacion, codigo);
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    public void onBackPressed() {

    }


    public void ocultar_teclado() {

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mNumero_telefono.getWindowToken(), 0);
    }

}



