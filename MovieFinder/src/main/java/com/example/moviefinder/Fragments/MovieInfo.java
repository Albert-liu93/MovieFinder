package com.example.moviefinder.Fragments;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.moviefinder.Database.DatabaseHelper;
import com.example.moviefinder.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MovieInfo extends Fragment implements View.OnClickListener {

    String TAG = "MovieInfoFragment";
    TextView overview, genres, releaseDate, note_TV_blurb;
    JsonObject movieJSON;
    ImageView noteBtn;
    int movieId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_movie_info_fragment, container, false);
        getArguments(this.getArguments());
        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        overview = getView().findViewById(R.id.movie_overview_TV);
        genres = getView().findViewById(R.id.movie_genres_TV);
        releaseDate = getView().findViewById(R.id.movie_release_date_TV);
        noteBtn = getView().findViewById(R.id.note_IB);
        note_TV_blurb = getView().findViewById(R.id.note_TV_blurb);
        if (noteBtn != null) {
            noteBtn.setOnClickListener(this);
        }
        if (movieJSON != null || movieJSON.isJsonNull()) {
            loadText();
        }
        loadNote();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.note_IB:
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final EditText editText = new EditText(getContext());
                builder.setTitle("Enter Note");
                builder.setView(editText);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String note = editText.getText().toString();
                        //save note to database
                        DatabaseHelper databaseHelper = DatabaseHelper.getsInstance(getContext());
                        databaseHelper.updateNote(movieId,note);
                        dialogInterface.dismiss();
                        //update UI to reflect new
                        loadNote();
                    }
                });
                builder.show();
        }
    }

    private void getArguments(Bundle bundle) {
        String JSONString = bundle.getString("JSONObject");
        movieId = bundle.getInt("movieId");
        JsonParser jsonParser = new JsonParser();
        movieJSON = (JsonObject) jsonParser.parse(JSONString);
    }

    private void loadText() {
        Log.e(TAG, "json " + movieJSON);
        overview.setText(movieJSON.get("overview").getAsString());
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
    }

    private void loadNote() {
        String note = DatabaseHelper.getsInstance(getContext()).loadNote(movieId);
        note_TV_blurb.setText(note);
    }
}
