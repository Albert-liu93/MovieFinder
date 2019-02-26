package com.example.moviefinder.Fragments;

import android.content.Context;
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
import com.example.moviefinder.Database.DatabaseQueryHelper;
import com.example.moviefinder.Model.Genre;
import com.example.moviefinder.Model.Movie;
import com.example.moviefinder.Model.Note;
import com.example.moviefinder.R;
import com.example.moviefinder.Utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieInfoFragment extends Fragment implements View.OnClickListener {

    String TAG = "MovieInfoFragment";
    TextView overview, genres, releaseDate, note_TV_blurb;
    Movie currentMovie;
    Context mContext;
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
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
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
        if (currentMovie != null) {
            loadText();
        }
        Note note = DatabaseQueryHelper.findNoteForParentMovie(movieId);
        if (note != null) {
            loadNote(note);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.note_IB:
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final EditText editText = new EditText(getContext());
                final Note note = DatabaseQueryHelper.findNoteForParentMovie(movieId);
                if (note != null) {
                    editText.setText(note.getNote());
                }
                builder.setTitle("Enter note about this movie");
                builder.setView(editText);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String noteText = editText.getText().toString();
                        //save note to database
                        if (note != null) {
                            note.setNote(noteText);
                            note.save();
                            loadNote(note);
                        } else {
                            Note newNote = new Note();
                            newNote.setParent(movieId);
                            newNote.setNote(noteText);
                            newNote.save();
                            loadNote(newNote);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
        }
    }

    private void getArguments(Bundle bundle) {
        if (bundle.containsKey("movie")) {
            currentMovie = (Movie) bundle.getSerializable("movie");
        } else {
            //Error
        }
    }

    private void loadText() {
        overview.setText(currentMovie.getOverview());
        List<Genre> genreList = currentMovie.getGenres();
        for (Genre genre : genreList) {
            if (genre.getName() != null) {
                genres.append(genre.getName() + ", ");
            }
        }
        String textString = genres.getText().toString();
        if (textString.length() > 2) {
            genres.setText(textString.substring(0, textString.length() - 2));
        }
        String releaseDateString = Utils.getReleaseDateForRegion(mContext, currentMovie.getReleaseDates());
        releaseDate.append(releaseDateString);
    }

    private void loadNote(Note note) {
        note_TV_blurb.setText(note.getNote());
    }
}
