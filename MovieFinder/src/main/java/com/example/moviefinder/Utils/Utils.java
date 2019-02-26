package com.example.moviefinder.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moviefinder.API.MovieClient;
import com.example.moviefinder.API.RetrofitUtils;
import com.example.moviefinder.Callbacks.SuccessCallback;
import com.example.moviefinder.Constants.Constants;
import com.example.moviefinder.Model.Movie;
import com.example.moviefinder.Model.ReleaseDate;
import com.example.moviefinder.Model.Responses.ReleaseDatesResponse;
import com.example.moviefinder.Model.Results.ReleaseDateResults;
import com.example.moviefinder.MovieDetailsActivityTabbed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Albert on 5/24/2018.
 */

public class Utils {

    private static final String TAG = "Utils";

    public static void getMovieDetails(final int movieId, final Context mContext, final ImageView imageView, final SuccessCallback callback) {

        Retrofit retrofit = RetrofitUtils.getRetrofitClient("https://api.themoviedb.org/3/movie/");
        MovieClient client = retrofit.create(MovieClient.class);

        Call<Movie> call = client.getMovieDetail(movieId, Constants.API_KEY, "US", "release_dates");
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful()) {
                    Movie movie = response.body();
                    Intent intent = new Intent(mContext, MovieDetailsActivityTabbed.class);
                    intent.putExtra("movie", movie);
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
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(mContext, "Failure!", Toast.LENGTH_SHORT).show();
                callback.error();
            }
        });
    }

    public static void getMovieDetails(int movieId, final Context mContext) {

        Retrofit retrofit = RetrofitUtils.getRetrofitClient("https://api.themoviedb.org/3/movie/");
        MovieClient client = retrofit.create(MovieClient.class);

//        Call<JsonObject> call = client.getMovieDetail(movieId, Constants.API_KEY);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if (response.isSuccessful()) {
//                    Log.e(TAG, "response code" + response.code());
//                    //go to new activity, pass jsonobject as string
//                    Log.e(TAG, "jsonobject = " + response.body());
//                    Intent intent = new Intent(mContext, MovieDetailsActivityTabbed.class);
//                    intent.putExtra("JSONObject", response.body().toString());
//                    mContext.startActivity(intent);
//                } else {
//                    Toast.makeText(mContext, "Error in response", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Toast.makeText(mContext, "Failure!", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    public static boolean checkInternetStatus(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static boolean isStringNotNullorBlank(String string) {
        return string != null && !string.isEmpty();
    }

    /** checks to see if character name has been set, default is
     * 'Character Name'
     * @param name
     * @return boolean
     */
    public static boolean isCharacterNameValid(String name){
        if (name != null && !name.equals("Character Name")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param view         View to animate
     * @param toVisibility Visibility at the end of animation
     * @param toAlpha      Alpha at the end of animation
     * @param duration     Animation duration in ms
     */
    public static void animateView(final View view, final int toVisibility, float toAlpha, int duration) {
        boolean show = toVisibility == View.VISIBLE;
        if (show) {
            view.setAlpha(0);
        }
        view.setVisibility(View.VISIBLE);
        view.animate()
                .setDuration(duration)
                .alpha(show ? toAlpha : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(toVisibility);
                    }
                });
    }

    public static String getReleaseDateForRegion(Context mContext, ReleaseDatesResponse releaseDatesResponse) {
        String locale = mContext.getResources().getConfiguration().locale.getCountry();
        if (Utils.isStringNotNullorBlank(locale)) {
            List<ReleaseDateResults> releaseDateResultsList = releaseDatesResponse.getResults();

            for (ReleaseDateResults releaseDateResults : releaseDateResultsList) {
                if (releaseDateResults.getIso31661().equals(locale)) {
                    List<ReleaseDate> releaseDates = releaseDateResults.getReleaseDates();
                    ReleaseDate mReleaseDate = releaseDates.get(0);
                    if (mReleaseDate != null) {
                        String result = mReleaseDate.getReleaseDate().substring(0, 10);
                        SimpleDateFormat inputDate = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat outputDate = new SimpleDateFormat("MMMM-dd-yyyy");
                        Date convertedDate = new Date();
                        try {
                            convertedDate = inputDate.parse(result);
                        } catch (ParseException e) {
                            Log.e(TAG, "Error" + e);
                        }
                        String releaseDateFormatted = outputDate.format(convertedDate);
                        return releaseDateFormatted;
                    }
                }
            }
        }
        return "";
    }

}
