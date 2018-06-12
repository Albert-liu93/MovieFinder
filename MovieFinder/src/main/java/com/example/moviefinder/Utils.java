package com.example.moviefinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.moviefinder.API.MovieClient;
import com.example.moviefinder.API.RetrofitUtils;
import com.example.moviefinder.Adapters.RecyclerViewAdapter;
import com.example.moviefinder.Constants.Constants;
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

    public static void getMovieDetails(int movieId, final Context mContext, final View view, final int resourceId) {

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
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext,
                            view.findViewById(resourceId), "IV_transition");
                    mContext.startActivity(intent, optionsCompat.toBundle());
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

    public static boolean checkInternetStatus(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


}
