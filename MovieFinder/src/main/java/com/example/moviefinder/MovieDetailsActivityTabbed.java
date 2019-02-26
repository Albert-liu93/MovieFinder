package com.example.moviefinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.moviefinder.API.MovieClient;
import com.example.moviefinder.API.RetrofitUtils;
import com.example.moviefinder.Callbacks.OnTaskCompleted;
import com.example.moviefinder.Constants.Constants;
import com.example.moviefinder.Fragments.CastFragment;
import com.example.moviefinder.Fragments.MovieInfoFragment;
import com.example.moviefinder.Fragments.TrailerFragment;
import com.example.moviefinder.Model.Cast;
import com.example.moviefinder.Model.Movie;
import com.example.moviefinder.Model.Responses.CastResponse;
import com.example.moviefinder.Utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Albert on 5/11/2018.
 */

public class MovieDetailsActivityTabbed extends AppCompatActivity {

    private String TAG = "MovieDetailsActivityTabbed";
    private TextView title, runtimeTV;
    private SearchView searchView;
    private ImageView poster, backdrop;
    private RatingBar ratingBar;
    private Movie currentMovie;
    private Context mContext;
    private String videoURL;
    private List<Cast> castArrayList;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_movie_details_tabbed);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);


        poster = findViewById(R.id.movie_IV);
        backdrop = findViewById(R.id.movie_background_poster_IV);
        backdrop.setAlpha(50);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        title = findViewById(R.id.movie_title);
        ratingBar = findViewById(R.id.ratingBar);
        runtimeTV = findViewById(R.id.runtimeTV);


        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        if (Build.VERSION.SDK_INT >= 21) {
            String imageTransitionName = intent.getStringExtra("transitionName");
            poster.setTransitionName(imageTransitionName);
        }


        if (intent.hasExtra("movie")) {
            currentMovie = (Movie) intent.getSerializableExtra("movie");

            //Execute background process to gather videoURL and cast info
            new BackgroundInfo().execute();

            loadText();
            if (Utils.isStringNotNullorBlank(currentMovie.getPosterPath())) {
                Picasso.get()
                        .load(Constants.movieDB_Image_URL + currentMovie.getPosterPath())
                        .error(android.R.drawable.stat_notify_error)
                        .into(poster);
            }
            if (Utils.isStringNotNullorBlank(currentMovie.getBackdropPath())) {
                Picasso.get()
                        .load(Constants.movieDB_Image_URL + currentMovie.getPosterPath())
                        .error(android.R.drawable.stat_notify_error)
                        .into(backdrop);
            }
            MovieInfoFragment movieInfo = new MovieInfoFragment();
            bundle.putSerializable("movie", currentMovie);
            movieInfo.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.moviedetail_fragmentcontainer, movieInfo).commit();
        } else {
            //TODO error message
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_movie);
        searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("Search For Movie Title");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(mContext, SearchMovieActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
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
                            bundle.putSerializable("movie", currentMovie);
                            selectedFragment = new MovieInfoFragment();
                            selectedFragment.setArguments(bundle);
                            break;
                        case R.id.action_movie_cast:
                            if (castArrayList != null && !castArrayList.isEmpty()) {
                                bundle.putSerializable("cast", (ArrayList) castArrayList);
                                selectedFragment = new CastFragment();
                                selectedFragment.setArguments(bundle);
                            } else {
                                //TODO error message
                                displayErrorMessage("cast");
                            }

                            break;
                        case R.id.action_movie_trailer:
                            bundle.putString("video_URL", videoURL);
                            selectedFragment = new TrailerFragment();
                            selectedFragment.setArguments(bundle);
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.moviedetail_fragmentcontainer, selectedFragment).commit();
                    return true;
                }
            };

    private void loadText() {
        title.append(currentMovie.getTitle());
        ratingBar.setRating((Float.parseFloat(currentMovie.getVoteAverage().toString()))/2.0F);
        if (currentMovie.getRuntime() != null) {
            int runtime = currentMovie.getRuntime();
            int hours = runtime / 60;
            int minutes = runtime % 60;
            runtimeTV.append( hours + "hr " + minutes + "m");
        }
    }

    private void displayErrorMessage(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        switch (type) {
            case "cast":
                builder.setTitle(R.string.cast_error_title);
                builder.setMessage(R.string.cast_error_message);
            default:
                builder.setTitle(R.string.cast_error_title);
        }
        builder.show();
    }

    private class BackgroundInfo extends AsyncTask<Void, Void, Void>{

        Retrofit retrofit = RetrofitUtils.getRetrofitClient("https://api.themoviedb.org/3/movie/");
        MovieClient client = retrofit.create(MovieClient.class);


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected Void doInBackground(Void... voids) {
            Call<JsonObject> trailerCall = client.getTrailers(currentMovie.getId(), Constants.API_KEY, "en-US");
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
                                videoURL = trailerURL;
                            }
                        }
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "Couldn't retrieve trailers!");
                }
            });

            Call<CastResponse> castCall = client.getCredits(currentMovie.getId(), Constants.API_KEY);
            castCall.enqueue(new Callback<CastResponse>() {
                @Override
                public void onResponse(Call<CastResponse> call, Response<CastResponse> response) {
                    if (response.isSuccessful()) {
                        castArrayList = response.body().getCast();
                        if (castArrayList.size() > 10) {
                            castArrayList.subList(10,castArrayList.size()).clear();
                        }
                    }
                }

                @Override
                public void onFailure(Call<CastResponse> call, Throwable t) {

                }
            });
            return null;
        };

        @Override
        protected void onPostExecute(Void aVoid) {
            bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        }
    }
}



