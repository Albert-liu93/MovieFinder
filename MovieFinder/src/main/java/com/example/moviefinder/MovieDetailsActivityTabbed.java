package com.example.moviefinder;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.moviefinder.API.MovieClient;
import com.example.moviefinder.API.RetrofitUtils;
import com.example.moviefinder.Callbacks.OnTaskCompleted;
import com.example.moviefinder.Constants.Constants;
import com.example.moviefinder.Fragments.Cast;
import com.example.moviefinder.Fragments.MovieInfo;
import com.example.moviefinder.Fragments.Trailer;
import com.google.api.client.util.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Albert on 5/11/2018.
 */

public class MovieDetailsActivityTabbed extends AppCompatActivity implements View.OnClickListener, OnTaskCompleted {

    JsonObject movieJSON = new JsonObject();
    JsonObject creditsJSON = new JsonObject();
    String TAG = "MovieDetailsActivityTabbed";
    TextView title, overview, genres, releaseDate, rating;
    ImageView poster, backdrop;
    RatingBar ratingBar;
    int movieId;
    String videoURL, JSONString, creditsJSONString;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details_tabbed);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        poster = findViewById(R.id.movie_IV);
        backdrop = findViewById(R.id.movie_background_poster_IV);
        backdrop.setAlpha(50);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        title = findViewById(R.id.movie_title);
        ratingBar = findViewById(R.id.ratingBar);
        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        if (Build.VERSION.SDK_INT >= 21) {
            String imageTransitionName = intent.getStringExtra("transitionName");
            poster.setTransitionName(imageTransitionName);
        }
        if (intent.hasExtra("JSONObject")) {
            JSONString = intent.getStringExtra("JSONObject");
            JsonParser jsonParser = new JsonParser();
            movieJSON = (JsonObject) jsonParser.parse(JSONString);
            bundle.putString("JSONObject", JSONString);
        }
        if (intent.hasExtra("movieId")) {
            movieId = intent.getIntExtra("movieId", 0);
            bundle.putInt("movieId", movieId);
            backgroundInfo backgroundInfo = new backgroundInfo(this, movieId);
            backgroundInfo.execute();
        }


        loadText(movieJSON);
        String imageURL = "";
        String backdropURL = "";
        if (!movieJSON.get("poster_path").isJsonNull()) {
            imageURL = Constants.movieDB_Image_URL + movieJSON.get("poster_path").getAsString();
        }
        if (!movieJSON.get("backdrop_path").isJsonNull()) {
            backdropURL = Constants.movieDB_Image_URL + movieJSON.get("backdrop_path").getAsString();
        }
        if (!imageURL.isEmpty()) {
            Picasso.get()
                    .load(imageURL)
                    .error(android.R.drawable.stat_notify_error)
                    .into(poster, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
        if (!backdropURL.isEmpty()) {
            Picasso.get()
                    .load(imageURL)
                    .error(android.R.drawable.stat_notify_error)
                    .into(backdrop, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        } else {
            poster.setImageResource(R.drawable.ic_broken_image_black_24dp);
        }
        MovieInfo movieInfo = new MovieInfo();
        movieInfo.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.moviedetail_fragmentcontainer, movieInfo).commit();

    }

    @Override
    public void onTaskCompleted(String result) {
        videoURL = result;
    }

    @Override
    public void onTaskCompleted(JsonObject object) {
        creditsJSON = object;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    Bundle bundle = new Bundle();
                    switch (item.getItemId()) {
                        case R.id.action_movie_info:
                            bundle.putInt("movieId", movieId);
                            bundle.putString("JSONObject", JSONString);
                            bundle.putInt("movieId", movieId);
                            selectedFragment = new MovieInfo();
                            selectedFragment.setArguments(bundle);
                            break;
                        case R.id.action_movie_cast:
                            bundle.putString("JSONObject", creditsJSON.toString());
                            selectedFragment = new Cast();
                            selectedFragment.setArguments(bundle);
                            break;
                        case R.id.action_movie_trailer:
                            bundle.putString("video_URL", videoURL);
                            selectedFragment = new Trailer();
                            selectedFragment.setArguments(bundle);
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.moviedetail_fragmentcontainer, selectedFragment).commit();
                    return true;
                }
            };

    private void loadText(JsonObject movieJSON) {
        Log.e(TAG, "json " + movieJSON);
        title.append(movieJSON.get("original_title").getAsString());
//        overview.append(movieJSON.get("overview").getAsString());
//        JsonArray jsonArray = movieJSON.getAsJsonArray("genres");
//        Log.e(TAG, "jsonArray" + jsonArray);
//        int size = jsonArray.size();
//        Log.e(TAG, "jsonArray" + size);
//        for (JsonElement element : jsonArray) {
//            JsonObject jsonObject2 = element.getAsJsonObject();
//            genres.append(jsonObject2.get("name").getAsString());
//            genres.append(" ");
//        }
//
//        String releaseDateString = movieJSON.get("release_date").getAsString();
//
//        SimpleDateFormat inputDate = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat outputDate = new SimpleDateFormat("MMMM-dd-yyyy");
//        Date convertedDate = new Date();
//        try {
//            convertedDate = inputDate.parse(releaseDateString);
//        } catch (ParseException e) {
//            Log.e(TAG, "Error" + e);
//        }
//        String releaseDateFormatted = outputDate.format(convertedDate);
//        releaseDate.append(releaseDateFormatted);
        ratingBar.setRating((Float.parseFloat(movieJSON.get("vote_average").getAsString()))/2.0F);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.action_movie_trailer) {
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra("video_URL", videoURL);
            startActivity(intent);
        }
    }

    private class backgroundInfo extends AsyncTask<Void, Void, Void>{

        private OnTaskCompleted onTaskCompleted;
        private int movieId;

        public backgroundInfo(OnTaskCompleted activityContext, int id) {
            this.onTaskCompleted = activityContext;
            this.movieId = id;
        }

        @Override
        protected void onPreExecute() {

        }


        @Override
        protected Void doInBackground(Void... voids) {
            Retrofit retrofit = RetrofitUtils.getRetrofitClient("https://api.themoviedb.org/3/movie/");
            MovieClient client = retrofit.create(MovieClient.class);
            Call<JsonObject> trailerCall = client.getTrailers(movieId, Constants.API_KEY, "en-US");
            trailerCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Log.e(TAG, "response " + response.code());
                        JsonObject object = response.body();
                        JsonArray results = object.getAsJsonArray("results");
                        for (JsonElement element : results) {
                            JsonObject arrayObject = element.getAsJsonObject();
                            String trailerURL = arrayObject.get("key").getAsString();
                            if (!trailerURL.isEmpty()) {
                                onTaskCompleted.onTaskCompleted(trailerURL);
                            }
                        }
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e(TAG, "Couldn't retrieve trailers!");
                }
            });

            Call<JsonObject> creditsCall = client.getCredits(movieId, Constants.API_KEY);
            creditsCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Log.e(TAG, "response " + response.code());
                        JsonObject object = response.body();
                        if (!object.isJsonNull()) {
                            onTaskCompleted.onTaskCompleted(object);
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "Couldn't retrieve credits!");
                }
            });
            return null;
        };

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}



