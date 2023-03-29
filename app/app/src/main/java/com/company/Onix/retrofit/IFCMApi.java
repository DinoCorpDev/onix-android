package com.company.Onix.retrofit;


import com.company.Onix.Modelos.FCMBody;
import com.company.Onix.Modelos.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAoxFX_Bc:APA91bGxHfeQ-ktMUU7XTC8uQMmmz8xCU_ExdoQO6Z2RYyWAWrCYKMZ4BKPjze8wvM6iIxxjFsJbvUKmKUq_7yfnYs_kIHfbUV3oYci1QqNO8YmGpJEqWayp8f9J7yqeUNMOmVO2HCN7"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
