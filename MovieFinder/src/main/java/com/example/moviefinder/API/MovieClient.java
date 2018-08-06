package com.example.moviefinder.API;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
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
    Call<JsonObject> getMovieDetail(
            @Path("id") int id,
            @Query("api_key") String api_key);

    @GET("movie")
    Call<JsonObject> getSearchQuery(
            @Query("query") String text,
            @Query("page") int page,
            @Query("include_adult") boolean allowAdult,
            @Query("api_key") String api_key);

    @GET("popular")
    Call<JsonObject> getPopularMovies(
            @Query("api_key") String api_key,
            @Query("region") String region);


    @GET("upcoming")
    Call<JsonObject> getUpcomingmovies(
            @Query("api_key") String api_key,
            @Query("region") String region);

    @GET("{id}/videos")
    Call<JsonObject> getTrailers(
            @Path("id") int id,
            @Query("api_key") String api_key,
            @Query("language") String language);

    @GET("{id}/credits")
    Call<JsonObject> getCredits(
            @Path("id") int id,
            @Query("api_key") String api_key);

}
