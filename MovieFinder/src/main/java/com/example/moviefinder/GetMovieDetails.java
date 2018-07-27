package com.example.moviefinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviefinder.API.MovieClient;
import com.example.moviefinder.Constants.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Albert on 5/1/2018.
 */

public class GetMovieDetails extends AppCompatActivity  implements View.OnClickListener{

    private Button getMovieDetailBtn;
    private TextView movieResult;
    private EditText movieId;
    private Context mContext;
    private String TAG = "Getmoviedetail";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_movie_details);

        mContext = this;
        movieResult = findViewById(R.id.movieResult_TV);
        movieId = findViewById(R.id.movieId_et);
        getMovieDetailBtn = findViewById(R.id.getMovieDetail_btn);
        getMovieDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movieId.getText().toString().isEmpty()) {
                    Toast.makeText(mContext , "Must enter a movie Id to retrieve data!", Toast.LENGTH_LONG).show();
                } else {
                    getMovieDetail();
                    InputMethodManager inputManager = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(GetMovieDetails.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_btn:
                finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        movieId.setText(null);
    }

    private void getMovieDetail() {
        Log.e(TAG, "getmovie detail");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();
        MovieClient client = retrofit.create(MovieClient.class);
        final int MOVIE_ID;
        try {
            MOVIE_ID = Integer.parseInt(movieId.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Entered text is not a number!", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<JsonObject> call = client.getMovieDetail(MOVIE_ID, Constants.API_KEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "response code" + response.code());
                    Log.e(TAG, "jsonobject = " + response.body());
                    Intent intent = new Intent(mContext, MovieDetailsActivityTabbed.class);
                    intent.putExtra("JSONObject", response.body().toString());
                    intent.putExtra("movieId", MOVIE_ID);
                    startActivity(intent);
                } else {
                    movieResult.setText("Movie Could Not Be Found, Try Another ID");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext, "Failure!", Toast.LENGTH_LONG).show();

            }
        });


    }
}
