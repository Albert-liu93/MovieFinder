package com.example.moviefinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviefinder.API.MovieClient;
import com.example.moviefinder.API.RetrofitUtils;
import com.example.moviefinder.Adapters.RecyclerViewAdapter;
import com.example.moviefinder.Constants.Constants;
import com.example.moviefinder.Model.Movie;
import com.example.moviefinder.Model.Responses.MovieResponse;
import com.example.moviefinder.Utils.TypefaceUtil;
import com.example.moviefinder.Utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

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
    private TextView playingNow_TV, upcoming_TV;
    private Context mContext;
    private View progressOverlay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Futura Book font.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf

        setContentView(R.layout.activity_main);

        //admob
//        MobileAds.initialize(mContext, "ca-app-pub-3940256099942544~3347511713");
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice("74D028AA77CD10324FBC73179CDE0E8F").build();
//        adView.loadAd(adRequest);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        drawerLayout = findViewById(R.id.drawer_layout);
        playingNow_TV = findViewById(R.id.playingNowMovie_TV);
        upcoming_TV = findViewById(R.id.upcomingMovies_TV);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Oswald-Medium.ttf");
        playingNow_TV.setTypeface(tf);
        upcoming_TV.setTypeface(tf);

        progressOverlay = findViewById(R.id.progress_overlay);


        //nav drawer
        mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        if (Utils.checkInternetStatus(mContext)) {
            Utils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
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

        return super.onCreateOptionsMenu(menu);
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
        Call<MovieResponse> call = client.getNowPlaying(Constants.API_KEY, "US");
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {

                    Log.e(TAG, "jsonobject = " + response.body());
                    ArrayList<Movie> movieList = (ArrayList) response.body().getResults();
                    displayData(movieList, "popular");
                } else {
                    Toast.makeText(mContext, "Unsuccessful response!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "response unsuccessful" + response.code());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(mContext, "Failure!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "failed");
            }
        });
    }

    private void loadUpcomingImages() {
        Retrofit retrofit = RetrofitUtils.getRetrofitClient("https://api.themoviedb.org/3/movie/");

        MovieClient client = retrofit.create(MovieClient.class);


        Call<MovieResponse> call = client.getUpcomingmovies(Constants.API_KEY, "US");
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "movie response = " + response.body());
                    ArrayList<Movie> movieList = (ArrayList) response.body().getResults();
                    displayData(movieList, "upcoming");
                } else {
                    Toast.makeText(mContext, "Unsuccessful response!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "response unsuccessful" + response.code());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(mContext, "Failure!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "failed");
            }
        });
    }

    private void displayData(ArrayList<Movie> movies, String type) {
        if (type.equals("popular")) {
            initPopularRecyclerView(movies);
        } else if (type.equals("upcoming")) {
            initUpcomingRecyclerView(movies);
        }
    }

    private void initPopularRecyclerView(ArrayList<Movie> movies) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.mainPage_popularRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, movies);
        recyclerView.setAdapter(recyclerViewAdapter);
        Utils.animateView(progressOverlay, View.GONE, 0, 200);
        showText();
    }

    private void initUpcomingRecyclerView(ArrayList<Movie> movies) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.mainPage_upcomingRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, movies);
        recyclerView.setAdapter(recyclerViewAdapter);
        Utils.animateView(progressOverlay, View.GONE, 0, 200);
        showText();
    }

    private void showText() {
        playingNow_TV.setVisibility(View.VISIBLE);
        upcoming_TV.setVisibility(View.VISIBLE);
    }
}
