package com.example.moviefinder.Fragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.moviefinder.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MovieInfo extends Fragment {

    String TAG = "MovieInfoFragment";
    TextView title, overview, genres, releaseDate, rating;
    RatingBar ratingBar;
    JsonObject movieJSON;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_movie_info_fragment, container, false);

        getArguments(this.getArguments());
        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        title = getView().findViewById(R.id.movie_original_title_TV);
        overview = getView().findViewById(R.id.movie_overview_TV);
        genres = getView().findViewById(R.id.movie_genres_TV);
        releaseDate = getView().findViewById(R.id.movie_release_date_TV);
        rating = getView().findViewById(R.id.rating_TV);
        ratingBar = getView().findViewById(R.id.ratingBar);
        if (movieJSON != null || movieJSON.isJsonNull()) {
            loadText();

        }
    }

    private void getArguments(Bundle bundle) {
        String JSONString = bundle.getString("JSONObject");
        JsonParser jsonParser = new JsonParser();
        movieJSON = (JsonObject) jsonParser.parse(JSONString);
    }

    private void loadText() {
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
}
