package com.example.moviefinder.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moviefinder.API.MovieClient;
import com.example.moviefinder.API.RetrofitUtils;
import com.example.moviefinder.Adapters.RecyclerViewAdapter;
import com.example.moviefinder.Callbacks.OnTaskCompleted;
import com.example.moviefinder.Callbacks.SuccessCallback;
import com.example.moviefinder.Constants.Constants;
import com.example.moviefinder.MovieDetailsActivityTabbed;
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

    public static void getMovieDetails(final int movieId, final Context mContext, final View view, final ImageView imageView, final SuccessCallback callback) {

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
                    Intent intent = new Intent(mContext, MovieDetailsActivityTabbed.class);

                    intent.putExtra("JSONObject", response.body().toString());
                    intent.putExtra("movieId", movieId);
                    intent.putExtra("transitionName", ViewCompat.getTransitionName(imageView));
                    if (Build.VERSION.SDK_INT >= 21) {
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (Activity) mContext,
                                imageView,
                                ViewCompat.getTransitionName(imageView));
                        mContext.startActivity(intent, optionsCompat.toBundle());
                    } else {
                        mContext.startActivity(intent);
                    }
                    callback.success();
                } else {
                    Toast.makeText(mContext, "Error in response", Toast.LENGTH_SHORT).show();
                    callback.error();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext, "Failure!", Toast.LENGTH_SHORT).show();
                callback.error();
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
                    Intent intent = new Intent(mContext, MovieDetailsActivityTabbed.class);
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
