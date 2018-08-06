package com.example.moviefinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviefinder.API.MovieClient;
import com.example.moviefinder.Adapters.EndlessScrollListener;
import com.example.moviefinder.Callbacks.OnTaskCompleted;
import com.example.moviefinder.Callbacks.SuccessCallback;
import com.example.moviefinder.Constants.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.moviefinder.Utils.Utils.getMovieDetails;


/**
 * Created by Albert on 5/15/2018.
 */

public class SearchMovieActivity extends AppCompatActivity {


    ListView searchListView;
    TextView noResultTV;
    RelativeLayout progressLayout;
    Context mContext;
    String TAG = "SearchMovieActivity";
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    ArrayList<String> posterBackDrop = new ArrayList<>();
    HashMap<Integer, Integer> idHashMap = new HashMap<>();
    ArrayAdapter arrayAdapter;
    String query = "";
    int key = 1;
    int page = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_movie);
        mContext = this;
        searchListView = findViewById(R.id.movieSearch_lv);
        noResultTV = findViewById(R.id.search_no_results);
        progressLayout = findViewById(R.id.loadingMore_progressBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        if (intent.hasExtra("query")) {
            query = intent.getStringExtra("query");
            searchMovie(query, 1);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_movie);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search For Movie Title");
        if (!query.isEmpty()) {
            searchView.setQuery(query, false);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String inputQuery) {
                query = inputQuery;
                clearData();
                searchMovie(query, 1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    private void searchMovie(final String query, int page) {
        if (page == 1) {
            key = 1;
        } else {
            progressLayout.setVisibility(View.VISIBLE);
        }

        Log.e(TAG, "searchMovie");
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(logging);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/search/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build());

        Retrofit retrofit = builder.build();
        MovieClient client = retrofit.create(MovieClient.class);

        Call<JsonObject> call = client.getSearchQuery(query, page, false, Constants.API_KEY);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "jsonobject = " + response.body());
                    displayData(response.body());
                } else {
                    Toast.makeText(mContext, "Unsuccessful response!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "response unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mContext, "Failure!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "failed");

            }
        });
    }


    private void displayData(JsonObject jsonObject) {
        searchListView.setVisibility(View.VISIBLE);
        noResultTV.setVisibility(View.INVISIBLE);
        JsonObject result = jsonObject;
        int count = result.get("total_results").getAsInt();
        if (count == 0 || result.isJsonNull()) {
            searchListView.setVisibility(View.INVISIBLE);
            noResultTV.setVisibility(View.VISIBLE);
        } else {
            Log.e(TAG, "total results = " + count);
            JsonArray resultsArray = result.getAsJsonArray("results");
            for (JsonElement element : resultsArray) {
                JsonObject object = element.getAsJsonObject();
                titles.add(object.get("title").getAsString());
                Log.e(TAG, "titles =" + object.get("title").getAsString());
                idHashMap.put(key, object.get("id").getAsInt());
                if (!object.get("poster_path").isJsonNull()) {
                    posterBackDrop.add(object.get("poster_path").getAsString());
                } else {
                    posterBackDrop.add("");
                }
                if (!object.get("release_date").isJsonNull()) {
                    String date = object.get("release_date").getAsString();
                    if (!date.isEmpty() || date.length() > 4) {
                        dates.add(date.substring(0,4));
                    } else {
                        dates.add("N/A");
                    }
                }
                key++;
            }
            Log.e("page = ", " =" + page);
            if (arrayAdapter == null) {
                arrayAdapter = new MovieAdapter(this, titles, posterBackDrop,dates);
            }
            if (page == 1) {
                searchListView.setAdapter(arrayAdapter);
                searchListView.setOnScrollListener(new EndlessScrollListener() {
                    @Override
                    public boolean onLoadMore(int loadPage, int totalItemsCount) {
                        page = loadPage;
                        searchMovie(query, loadPage);
                        return true;
                    }
                });
            } else {
                arrayAdapter.notifyDataSetChanged();
                progressLayout.setVisibility(View.GONE);
            }


            searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //get value from key of id
                    Log.e(TAG, "id = " + id);
                    int movieId = idHashMap.get((int)id+1);
                    Log.e(TAG, "movideID clicked" + movieId);
                    getMovieDetails(movieId, mContext, view, R.id.movie_search_IV, new SuccessCallback() {
                        @Override
                        public void success() {

                        }

                        @Override
                        public void error() {

                        }
                    });
                }
            });
        }
    }

    private void clearData() {
        titles.clear();
        dates.clear();
        posterBackDrop.clear();
        idHashMap.clear();
    }



    class MovieAdapter extends ArrayAdapter<String> {

        ArrayList<String> titles;
        ArrayList<String> posterBackDrop;
        ArrayList<String> dates;

        public MovieAdapter(@NonNull Context context, ArrayList<String> titles, ArrayList<String> backdrop, ArrayList<String> dates) {
            super(context, R.layout.movie_list_row, titles);
            this.titles = titles;
            this.posterBackDrop = backdrop;
            this.dates = dates;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater movieInflater = LayoutInflater.from(getContext());
            View customView = movieInflater.inflate(R.layout.movie_list_row, parent, false);
            TextView title = customView.findViewById(R.id.movie_search_TV);
            TextView year = customView.findViewById(R.id.movie_search_date_TV);
            ImageView poster = customView.findViewById(R.id.movie_search_IV);
            final ProgressBar progressBar = customView.findViewById(R.id.movieSearch_IV_ProgressBar);

            title.setText(titles.get(position));
            year.setText("(" + dates.get(position) + ")");

            String imageURL = Constants.movieDB_Image_URL + posterBackDrop.get(position);

            Picasso.get()
                    .load(imageURL)
                    .error(android.R.drawable.stat_notify_error)
                    .into(poster, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
            return customView;
        }
    }

}
