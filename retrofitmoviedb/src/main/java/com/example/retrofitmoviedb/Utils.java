package com.example.retrofitmoviedb;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.retrofitmoviedb.API.MovieClient;
import com.example.retrofitmoviedb.API.RetrofitUtils;
import com.example.retrofitmoviedb.Constants.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Albert on 5/24/2018.
 */

public class Utils {

    private static final String TAG = "Utils";

    public static void getMovieDetails(int movieId, final Context mContext) {

        Retrofit retrofit = RetrofitUtils.getRetrofitClient("https://api.themoviedb.org/3/movie/");
        MovieClient client = retrofit.create(MovieClient.class);

        Call<JsonObject> call = client.getMovieDetail(movieId, Constants.API_KEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "response code" + response.code());
                    //go to new activity, pass jsonobject as string
                    Log.e(TAG, "jsonobject = " + response.body());
                    Intent intent = new Intent(mContext, MovieDetailsActivity.class);
                    intent.putExtra("JSONObject", response.body().toString());
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "Error in response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext, "Failure!", Toast.LENGTH_SHORT).show();
            }
        });



    }


}
