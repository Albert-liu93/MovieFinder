package com.example.retrofitmoviedb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.retrofitmoviedb.API.MovieClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Albert on 5/1/2018.
 */

public class GetMovieDetails extends AppCompatActivity {

    private String API_KEY = "4ee4d1e00b07b0aa548326a083c25eb3";
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
                }
            }
        });
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
        int MOVIE_ID;
        try {
            MOVIE_ID = Integer.parseInt(movieId.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Entered text is not a number!", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<JsonObject> call = client.getMovieDetail(MOVIE_ID, API_KEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "response code" + response.code());
                    //go to new activity, pass jsonobject as string
                    Log.e(TAG, "jsonobject = " + response.body());
                    Intent intent = new Intent(mContext, MovieDetails.class);
                    intent.putExtra("JSONObject", response.body().toString());
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
