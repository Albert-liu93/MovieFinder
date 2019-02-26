package com.example.moviefinder;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.moviefinder.API.MovieClient;
import com.example.moviefinder.API.RetrofitUtils;
import com.example.moviefinder.Constants.Constants;
import com.example.moviefinder.Model.Movie;
import com.example.moviefinder.Model.Responses.MovieResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivityLoad extends AppCompatActivity {

    public static final String TAG = "MainActivityLoad";
    private Context mContext;
    private boolean failed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main_load);
        loadPopularImages();
        loadUpcomingImages();
    }


    private void loadPopularImages() {

        Retrofit retrofit = RetrofitUtils.getRetrofitClient("https://api.themoviedb.org/3/movie/");

        MovieClient client = retrofit.create(MovieClient.class);

        Call<MovieResponse> call = client.getNowPlaying(Constants.API_KEY, "US");
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {

                    Log.e(TAG, "jsonobject = " + response.body());
                    ArrayList<Movie> movieList = (ArrayList) response.body().getResults();
                    displayData(movieList, "popular");
                } else {
                    Toast.makeText(mContext, "Unsuccessful response!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "response unsuccessful" + response.code());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(mContext, "Failure!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "failed");
            }
        });

    }

    private void loadUpcomingImages() {
        Retrofit retrofit = RetrofitUtils.getRetrofitClient("https://api.themoviedb.org/3/movie/");

        MovieClient client = retrofit.create(MovieClient.class);


        Call<MovieResponse> call = client.getUpcomingmovies(Constants.API_KEY, "US");
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "movie response = " + response.body());
                    ArrayList<Movie> movieList = (ArrayList) response.body().getResults();
                    displayData(movieList, "upcoming");
                } else {
                    Toast.makeText(mContext, "Unsuccessful response!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "response unsuccessful" + response.code());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(mContext, "Failure!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "failed");
            }
        });
    }

    private void displayData(ArrayList<Movie> movies, String type) {
//        if (type.equals("popular")) {
//            initPopularRecyclerView(movies);
//        } else if (type.equals("upcoming")) {
//            initUpcomingRecyclerView(movies);
//        }
    }
}
