package com.company.Onix.providers;

import android.content.Context;

import com.company.Onix.R;
import com.company.Onix.retrofit.IGoofgleApi;
import com.company.Onix.retrofit.RetrofitClient;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;

public class GoogleApiProvider {

    private Context context;

    public GoogleApiProvider(Context context) {
        this.context = context;

    }




    public Call<String> getDirections(LatLng orginLatLng, LatLng destionationLatLng) {
        String baseUrl = "https://maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + orginLatLng.latitude + "," + orginLatLng.longitude + "&"
                + "destination=" + destionationLatLng.latitude + "," + destionationLatLng.longitude + "&"
                + "key=" + context.getResources().getString(R.string.google_maps_key);
        return RetrofitClient.getClient(baseUrl).create(IGoofgleApi.class).getDirections(baseUrl + query);
    }


}