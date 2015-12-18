package com.vinay.passioneer.popularmovies.network;


import com.vinay.passioneer.popularmovies.model.MovieModel;
import com.vinay.passioneer.popularmovies.model.ResponseModel;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class WebService {

    public static List<MovieModel> fetchPopularMovies(String url) throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestInterface service = retrofit.create(RestInterface.class);
        Call<ResponseModel> call = service.getPopularMovies(url);
        Response<ResponseModel> response = call.execute();
        ResponseModel responseModel = response.body();
        return responseModel.getResults();
    }
}
