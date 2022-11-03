package com.company.Onix.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoofgleApi {
    @GET
    Call<String> getDirections(@Url String url);

}
