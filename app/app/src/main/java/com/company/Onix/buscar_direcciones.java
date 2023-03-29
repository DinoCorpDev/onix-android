package com.company.Onix;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Response;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Request;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class buscar_direcciones extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private TextView mResultados;
    private TextInputEditText mDireccion;
    private String mBuscar="palmeto,cali";
    private SearchView mBuscardor;
    private ArrayList<String> miArreglo = new ArrayList<String>();




    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_direcciones);
        mResultados=findViewById(R.id.resultado);

        mBuscardor=findViewById(R.id.buscador);
        mBuscardor.setOnQueryTextListener(this);






    }



    @Override
    public boolean onQueryTextSubmit(String query) {

        mBuscar= query;

        String encontrar=mBuscar+" cali";
        Geocoder coder= new Geocoder(buscar_direcciones.this);


        List<Address> addressList = null;


        try {
            addressList=coder.getFromLocationName(encontrar,5);



        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addressList==null){
            Toast.makeText(this, "no se encontro nada", Toast.LENGTH_SHORT).show();
        }else {
            Address location= addressList.get(0);


            //  mResultados.setText(String.valueOf(addressList.get(0)));
            Toast.makeText(this, ""+addressList.size(), Toast.LENGTH_SHORT).show();

            mResultados.setText(String.valueOf(location.getAddressLine(0)));

        }


        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {



        return false;
    }
}