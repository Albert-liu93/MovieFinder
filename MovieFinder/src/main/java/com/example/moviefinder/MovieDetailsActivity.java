package com.example.moviefinder;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.moviefinder.Constants.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.ImageLoader;

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

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener, OnTaskCompleted {

    JsonObject movieJSON = new JsonObject();
    String TAG = "MovieDetailsActivity";
    TextView title, overview, genres, releaseDate, rating;
    ImageView poster, backdrop;
    RatingBar ratingBar;
    Button trailerBtn;
    int movieId;
    String videoURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        title = findViewById(R.id.movie_original_title_TV);
        overview = findViewById(R.id.movie_overview_TV);
        genres = findViewById(R.id.movie_genres_TV);
        releaseDate = findViewById(R.id.movie_release_date_TV);
        poster = findViewById(R.id.movie_IV);
        backdrop = findViewById(R.id.movie_background_poster_IV);
        rating = findViewById(R.id.rating_TV);
        ratingBar = findViewById(R.id.ratingBar);
        trailerBtn = findViewById(R.id.trailer_btn);


        Intent intent = getIntent();
        if (intent.hasExtra("JSONObject")) {
            String JSONString = intent.getStringExtra("JSONObject");
            JsonParser jsonParser = new JsonParser();
            movieJSON = (JsonObject) jsonParser.parse(JSONString);
        }
        if (intent.hasExtra("movieId")) {
            movieId = intent.getIntExtra("movieId", 0);
            VideoCheck videoCheck = new VideoCheck(this);
            videoCheck.execute();
        }
        loadText(movieJSON);
        ImageLoader imageLoader = ImageLoader.getInstance();
        String imageURL = Constants.movieDB_Image_URL + movieJSON.get("poster_path").getAsString();
        String posterURL = Constants.movieDB_Image_URL + movieJSON.get("backdrop_path").getAsString();
        imageLoader.displayImage(imageURL, poster);
        imageLoader.displayImage(posterURL, backdrop);

    }

    @Override
    public void onTaskCompleted(String result) {
        trailerBtn.setOnClickListener(this);
        videoURL = result;
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

    private void loadText(JsonObject movieJSON) {
        Log.e(TAG, "json " + movieJSON);
        title.append(movieJSON.get("original_title").getAsString());
        overview.append(movieJSON.get("overview").getAsString());
        JsonArray jsonArray = movieJSON.getAsJsonArray("genres");
        Log.e(TAG, "jsonArray" + jsonArray);
        int size = jsonArray.size();
        Log.e(TAG, "jsonArray" + size);
        for (JsonElement element : jsonArray) {
            JsonObject jsonObject2 = element.getAsJsonObject();
            genres.append(jsonObject2.get("name").getAsString());
            genres.append(" ");
        }

        String releaseDateString = movieJSON.get("release_date").getAsString();

        SimpleDateFormat inputDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDate = new SimpleDateFormat("MMMM-dd-yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = inputDate.parse(releaseDateString);
        } catch (ParseException e) {
            Log.e(TAG, "Error" + e);
        }
        String releaseDateFormatted = outputDate.format(convertedDate);
        releaseDate.append(releaseDateFormatted);
        ratingBar.setRating((Float.parseFloat(movieJSON.get("vote_average").getAsString()))/2.0F);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.trailer_btn) {
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra("video_URL", videoURL);
            startActivity(intent);
        }
    }

    private class VideoCheck extends AsyncTask<Void, Void, Void>{

        private OnTaskCompleted onTaskCompleted;


        public VideoCheck(OnTaskCompleted activityContext) {
            this.onTaskCompleted = activityContext;
        }

        @Override
        protected void onPreExecute() {

        }


        @Override
        protected Void doInBackground(Void... voids) {
            Retrofit retrofit = RetrofitUtils.getRetrofitClient("https://api.themoviedb.org/3/movie/");
            MovieClient client = retrofit.create(MovieClient.class);
            Call<JsonObject> call = client.getTrailers(movieId, Constants.API_KEY, "en-US");
            call.enqueue(new Callback<JsonObject>() {
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
                                trailerBtn.setClickable(true);
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
            return null;
        };

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}



