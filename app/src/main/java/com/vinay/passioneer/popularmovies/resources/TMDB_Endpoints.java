package com.vinay.passioneer.popularmovies.resources;


import com.vinay.passioneer.popularmovies.model.MovieModel;
import com.vinay.passioneer.popularmovies.model.Reviews;
import com.vinay.passioneer.popularmovies.model.Trailers;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface TMDB_Endpoints {

    @GET("discover/movie")
    Call<MovieModel.PopularMoviesResponse> getPopularMovies(@Query("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<Trailers.TrailersResponse> getMovieTrailers(@Path("id") int movieID, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<Reviews.ReviewsResponse> getMovieReviews(@Path("id") int movieID, @Query("api_key") String apiKey);

}
