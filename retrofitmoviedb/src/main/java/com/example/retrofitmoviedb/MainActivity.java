package com.example.retrofitmoviedb;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.retrofitmoviedb.API.MovieClient;
import com.example.retrofitmoviedb.API.RetrofitUtils;
import com.example.retrofitmoviedb.Adapters.RecyclerViewAdapter;
import com.example.retrofitmoviedb.Constants.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private Button getMovieDetailBtn, searchMovieBtn;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        searchMovieBtn = findViewById(R.id.mainActivity_searchBtn);
        searchMovieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SearchMovie.class));
            }
        });

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        loadPopularImages();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.about:
                startActivity(new Intent(mContext, AboutActivity.class));


        }
        return false;
    }

    private void loadPopularImages() {

        Retrofit retrofit = RetrofitUtils.getRetrofitClient("https://api.themoviedb.org/3/movie/");

        MovieClient client = retrofit.create(MovieClient.class);


        Call<JsonObject> call = client.getPopularMovies(Constants.API_KEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "jsonobject = " + response.body());
                    displayData(response.body());
                } else {
                    Toast.makeText(mContext, "Unsuccessful response!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "response unsuccessful" + response.code());
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext, "Failure!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "failed");
            }
        });

    }

    private void displayData(JsonObject responseResult) {
        int count = responseResult.get("total_results").getAsInt();
        Log.e(TAG, "total results = " + count);
        JsonArray resultsArray = responseResult.getAsJsonArray("results");

        ArrayList<String> posterBackDrop = new ArrayList<>();
        final HashMap<Integer, Integer> idHashMap = new HashMap<>();
        int MAX_RESULT = 10;
        int key = 1;
        for (JsonElement element : resultsArray) {
            if (MAX_RESULT == 0) {
                break;
            } else {
                JsonObject object = element.getAsJsonObject();
                Log.e(TAG, "titles =" + object.get("title").getAsString());
                idHashMap.put(key, object.get("id").getAsInt());
                if (!object.get("poster_path").isJsonNull()) {
                    posterBackDrop.add(object.get("poster_path").getAsString());
                } else {
                    posterBackDrop.add("");
                }
                key++;
            }
            MAX_RESULT--;
        }

        initRecyclerView(posterBackDrop, idHashMap);
    }

    private void initRecyclerView(ArrayList<String> posterBackDrop, HashMap<Integer, Integer> idHashMap) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.mainPage_recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, posterBackDrop, idHashMap);
        recyclerView.setAdapter(recyclerViewAdapter);

    }
}
