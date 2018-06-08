package com.example.retrofitmoviedb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.retrofitmoviedb.Constants.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Albert on 5/11/2018.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    JsonObject movieJSON = new JsonObject();
    String TAG = "MovieDetailsActivity";
    TextView title, overview, genres, releaseDate, rating;
    ImageView poster, backdrop;
    RatingBar ratingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.hide();
        title = findViewById(R.id.movie_original_title_TV);
        overview = findViewById(R.id.movie_overview_TV);
        genres = findViewById(R.id.movie_genres_TV);
        releaseDate = findViewById(R.id.movie_release_date_TV);
        poster = findViewById(R.id.movie_IV);
        backdrop = findViewById(R.id.movie_background_poster_IV);
        rating = findViewById(R.id.rating_TV);
        ratingBar = findViewById(R.id.ratingBar);

        Intent intent = getIntent();
        if (intent.hasExtra("JSONObject")) {
            String JSONString = intent.getStringExtra("JSONObject");
            JsonParser jsonParser = new JsonParser();
            movieJSON = (JsonObject) jsonParser.parse(JSONString);
        }
        loadText(movieJSON);
        ImageLoader imageLoader = ImageLoader.getInstance();
        String imageURL = Constants.movieDB_Image_URL + movieJSON.get("poster_path").getAsString();
        String posterURL = Constants.movieDB_Image_URL + movieJSON.get("backdrop_path").getAsString();
        imageLoader.displayImage(imageURL, poster);
        imageLoader.displayImage(posterURL, backdrop);

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


//        rating.append(movieJSON.get("vote_average").getAsString());
        ratingBar.setRating(Float.parseFloat("5"));

//        ratingBar.setRating(Float.parseFloat(movieJSON.get("vote_average").getAsString()));

    }
}
