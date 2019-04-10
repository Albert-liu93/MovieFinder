package com.example.moviefinder.API;

import com.example.moviefinder.Model.Movie;
import com.example.moviefinder.Model.Responses.CastResponse;
import com.example.moviefinder.Model.Responses.MovieResponse;
import com.example.moviefinder.Model.Responses.SearchResponse;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Albert on 5/1/2018.
 */

public interface MovieClient {


    @GET("{id}")
    Call<String> getMovieDetailAsString(
            @Path("id") int id,
            @Query("api_key") String api_key);

    @GET("{id}")
    Call<Movie> getMovieDetail(
            @Path("id") int id,
            @Query("api_key") String api_key,
            @Query("region") String region,
            @Query("append_to_response") String release_date);

    @GET("movie")
    Call<SearchResponse> getSearchQuery(
            @Query("query") String text,
            @Query("page") int page,
            @Query("include_adult") boolean allowAdult,
            @Query("api_key") String api_key);

    @GET("now_playing")
    Call<MovieResponse> getNowPlaying(
            @Query("api_key") String api_key,
            @Query("region") String region);


    @GET("popular")
    Call<JsonObject> getPopularMovies(
            @Query("api_key") String api_key,
            @Query("region") String region);


    @GET("upcoming")
    Call<MovieResponse> getUpcomingmovies(
            @Query("api_key") String api_key,
            @Query("region") String region);

    @GET("{id}/videos")
    Call<JsonObject> getTrailers(
            @Path("id") int id,
            @Query("api_key") String api_key,
            @Query("language") String language);

    @GET("{id}/credits")
    Call<CastResponse> getCredits(
            @Path("id") int id,
            @Query("api_key") String api_key);

}
