package com.vinay.passioneer.popularmovies.network;


import com.vinay.passioneer.popularmovies.model.ResponseModel;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Url;

public interface RestInterface {

    @GET
    Call<ResponseModel> getPopularMovies(@Url String url);
}
