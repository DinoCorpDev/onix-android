package com.company.Onix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.company.Onix.providers.ImagesProvider;
import com.company.Onix.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class pantalla_registro extends AppCompatActivity {

    private TextInputEditText mNombre;
    private TextInputEditText mTelefono;
    private TextInputEditText mReferido;
    private Button mBtn_siguiente_registro;


    //listener

    private Query mquery;

    //progresarbar
    private ProgressDialog progressDialog;

    //guardar id en telefono
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;

    //subir imagenes
    private CircleImageView mBtn_seleccionar_imagen;

    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private int upload_count = 0;
    private static final int GALLERY_REQUEST = 1;
    private Uri ImageUri;
    private File mImageFile;

    private ImagesProvider mImageProvider;

    //variable de voz texto
    private TextToSpeech cosa;
    private String mTeleno_certificado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_registro);
        mPref = getApplicationContext().getSharedPreferences("sessiones", MODE_PRIVATE);
        String telefono_bd = mPref.getString("telefono", "");
        if (telefono_bd.equals("")) {
            mPref = getApplicationContext().getSharedPreferences("sessiones", MODE_PRIVATE);
            mEditor = mPref.edit();

            mEditor.putString("pantalla", "");

            mEditor.apply();
            progressDialog.dismiss();
            Toast.makeText(pantalla_registro.this, "Por favor cree una cuenta", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(pantalla_registro.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setAction(Intent.ACTION_RUN);
            startActivity(intent);
        } else {
            mTeleno_certificado = telefono_bd;
        }


        mImageProvider = new ImagesProvider("imagenes_perfil_usuarios");


        mBtn_seleccionar_imagen = findViewById(R.id.btn_imagen);
        mBtn_seleccionar_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imagenes = new Intent(Intent.ACTION_GET_CONTENT);
                imagenes.setType("image/*");
                startActivityForResult(imagenes, GALLERY_REQUEST);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Comprobando el registro de tu cuenta por favor espere");


        mNombre = findViewById(R.id.nombre);
        mTelefono = findViewById(R.id.telefono);
        mTelefono.setText(mTeleno_certificado);
        mReferido = findViewById(R.id.referido);


        mBtn_siguiente_registro = findViewById(R.id.btn_siguiente_registrar);
        mBtn_siguiente_registro.setOnClickListener(v -> {
            String nombre = mNombre.getText().toString();
            String telefono = mTelefono.getText().toString();
            String referido = mReferido.getText().toString();
            if (nombre.equals("")) {
                mNombre.requestFocus();
                Toast.makeText(pantalla_registro.this, "por favor coloque un nombre", Toast.LENGTH_SHORT).show();
            } else {
                if (mImageFile == null) {
                    enviar_nota_audio();
                    Toast.makeText(pantalla_registro.this, "por favor suba una imagen para su perfil de usuario", Toast.LENGTH_LONG).show();
                    Intent imagenes = new Intent(Intent.ACTION_GET_CONTENT);
                    imagenes.setType("image/*");
                    startActivityForResult(imagenes, GALLERY_REQUEST);

                } else {
                    progressDialog.show();
                    mBtn_siguiente_registro.setEnabled(false);
                    subir_imagen();
                }
            }
        });


    }

    public void enviar_nota_audio() {
        cosa = new TextToSpeech(getBaseContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    cosa.setLanguage(Locale.getDefault());
                    String miTexto = "Por favor suba una foto para su perfil de usuario. Gracias";
                    cosa.speak(miTexto, cosa.QUEUE_FLUSH, null, null);
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            try {
                mImageFile = FileUtil.from(this, data.getData());
                mBtn_seleccionar_imagen.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("Error", "Mensaje: " + e.getMessage());
            }
        }
    }

    private void subir_imagen() {
        mImageProvider.saveImage(pantalla_registro.this, mImageFile, mTelefono.getText().toString()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {
                            String miFoto = uri.toString();

                            //enviar_registro a la base de datos del conductor nuevo
                            String newRef = "ONIX";
                            if(!mReferido.getText().toString().isEmpty()){
                                newRef = mReferido.getText().toString();
                            }

                            DatabaseReference servicio = FirebaseDatabase.getInstance().getReference().child("registros").child("usuarios").child(mTelefono.getText().toString());
                            HashMap<String, Object> registro = new HashMap<>();
                            //datos normales
                            registro.put("foto", miFoto);
                            registro.put("nombre", mNombre.getText().toString());
                            registro.put("telefono", mTeleno_certificado);
                            registro.put("id_referido", newRef);

                            //datos requeridos por el sistema
                            registro.put("servicio", 0);
                            registro.put("estado", "activo");
                            registro.put("verificacion", "numero_verificado");

                            servicio.setValue(registro);

                            //guardar_id_en_el _telefono
                            mPref = getApplicationContext().getSharedPreferences("sessiones", MODE_PRIVATE);
                            mEditor = mPref.edit();
                            mEditor.putString("telefono", mTeleno_certificado);
                            mEditor.putString("nombre", mNombre.getText().toString());

                            mEditor.putString("foto", miFoto);
                            mEditor.putString("pantalla", "plataforma");
                            mEditor.apply();

                            progressDialog.dismiss();
                            Intent intent = new Intent(pantalla_registro.this, plataforma.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setAction(Intent.ACTION_RUN);

                            startActivity(intent);


                        }
                    });
                } else {
                    mBtn_siguiente_registro.setEnabled(true);
                    Toast.makeText(pantalla_registro.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}