package com.example.retrofitmoviedb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Albert on 5/11/2018.
 */

public class MovieDetails extends AppCompatActivity {

    JsonObject movieJSON = new JsonObject();
    String TAG = "MovieDetails";
    TextView title, overview, genres, releaseDate;
    ImageView poster;

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

        Intent intent = getIntent();
        if (intent.hasExtra("JSONObject")) {
            String JSONString = intent.getStringExtra("JSONObject");
            JsonParser jsonParser = new JsonParser();
            movieJSON = (JsonObject) jsonParser.parse(JSONString);
        }
        Log.e(TAG, "json " + movieJSON);
        title.append(movieJSON.get("original_title").getAsString());
        overview.append(movieJSON.get("overview").getAsString());
        JsonArray jsonArray = movieJSON.getAsJsonArray("genres");
        Log.e(TAG, "jsonArray" + jsonArray);
        int size = jsonArray.size();
        Log.e(TAG, "jsonArray" + size);
        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            genres.append(jsonObject.get("name").getAsString());
            genres.append(",");
        }
        releaseDate.append(movieJSON.get("release_date").getAsString());

        ImageLoader imageLoader = ImageLoader.getInstance();
        String imageURL = Constants.movieDB_Image_URL + movieJSON.get("poster_path").getAsString();
        imageLoader.displayImage(imageURL, poster);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
