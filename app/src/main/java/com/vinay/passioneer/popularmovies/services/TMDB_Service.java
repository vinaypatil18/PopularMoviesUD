package com.vinay.passioneer.popularmovies.services;


import com.vinay.passioneer.popularmovies.model.MovieModel;
import com.vinay.passioneer.popularmovies.model.Reviews;
import com.vinay.passioneer.popularmovies.model.Trailers;
import com.vinay.passioneer.popularmovies.resources.TMDB_Endpoints;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class TMDB_Service {


    private static Retrofit retrofit;
    private static TMDB_Endpoints service;
    private static final String BASE_URI = "http://api.themoviedb.org/3/";

    static {
        System.out.println("Building Retrofit");
        retrofit = new Retrofit.Builder().baseUrl(BASE_URI).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(TMDB_Endpoints.class);
    }

    public static List<MovieModel> getPopularMovies(String sortBy, String apiKey) throws IOException {
        System.out.println("Inside fetchPopularMovies");
        Call<MovieModel.PopularMoviesResponse> call = service.getPopularMovies(sortBy, apiKey);
        Response<MovieModel.PopularMoviesResponse> response = call.execute();
        MovieModel.PopularMoviesResponse responseModel = response.body();
        return responseModel.getMovieModels();
    }

    public static List<Trailers> getMovieTrailers(int movieID, String apiKey) throws IOException {
        Call<Trailers.TrailersResponse> call = service.getMovieTrailers(movieID, apiKey);
        Response<Trailers.TrailersResponse> response = call.execute();
        Trailers.TrailersResponse trailersResponse = response.body();
        return trailersResponse.getTrailers();
    }

    public static List<Reviews> getMovieReviews(int movieID, String apiKey) throws IOException {
        Call<Reviews.ReviewsResponse> call = service.getMovieReviews(movieID, apiKey);
        Response<Reviews.ReviewsResponse> response = call.execute();
        Reviews.ReviewsResponse reviewsResponse = response.body();
        return reviewsResponse.getReviews();
    }

}
