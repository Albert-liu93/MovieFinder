package com.example.moviefinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.moviefinder.API.MovieClient;
import com.example.moviefinder.API.RetrofitUtils;
import com.example.moviefinder.Adapters.RecyclerViewAdapter;
import com.example.moviefinder.Constants.Constants;
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
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private SearchView searchView;
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

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        if (Utils.checkInternetStatus(mContext)) {
            loadPopularImages();
            loadUpcomingImages();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("No Internet Connectivity");
            builder.setMessage("Sorry, no internet connection detected. Main features of this app will not be available");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.clearFocus();
            searchView.setIconified(true);
        }

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

        return super.onCreateOptionsMenu(menu);    }

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
                    displayData(response.body(), "popular");
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

    private void loadUpcomingImages() {
        Retrofit retrofit = RetrofitUtils.getRetrofitClient("https://api.themoviedb.org/3/movie/");

        MovieClient client = retrofit.create(MovieClient.class);


        Call<JsonObject> call = client.getUpcomingmovies(Constants.API_KEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "jsonobject = " + response.body());
                    displayData(response.body(), "upcoming");
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

    private void displayData(JsonObject responseResult, String type) {
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
        if (type.equals("popular")) {
            initPopularRecyclerView(posterBackDrop, idHashMap);
        } else if (type.equals("upcoming")) {
            initUpcomingRecyclerView(posterBackDrop, idHashMap);
        }
    }

    private void initPopularRecyclerView(ArrayList<String> posterBackDrop, HashMap<Integer, Integer> idHashMap) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.mainPage_popularRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, posterBackDrop, idHashMap);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void initUpcomingRecyclerView(ArrayList<String> posterBackDrop, HashMap<Integer, Integer> idHashMap) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.mainPage_upcomingRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, posterBackDrop, idHashMap);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}
