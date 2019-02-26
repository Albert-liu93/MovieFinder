package com.example.moviefinder.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.moviefinder.Callbacks.OnTaskCompleted;
import com.example.moviefinder.Callbacks.SuccessCallback;
import com.example.moviefinder.Constants.Constants;
import com.example.moviefinder.Model.Movie;
import com.example.moviefinder.MovieDetailsActivityTabbed;
import com.example.moviefinder.R;
import com.example.moviefinder.Utils.Utils;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Albert on 5/24/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private Context mContext;
    private ArrayList<Movie> movieList;
    private OnTaskCompleted onTaskCompleted;

    public RecyclerViewAdapter(Context context, ArrayList<Movie> movies) {
        this.mContext = context;
        this.movieList = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_poster_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Movie movie = movieList.get(position);

        holder.image.setClickable(false);
        String imageURL = Constants.movieDB_Image_URL + movie.getPosterPath();
        Picasso.get()
                .load(imageURL)
                .placeholder(R.drawable.ic_photo_grey_24dp)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.image.setClickable(true);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
        ViewCompat.setTransitionName(holder.image, movie.getId().toString());
        holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Clicked on image url " + movie.getPosterPath());
                    Log.d(TAG, "Movie has id" + movie.getId());
                    int movieId = movie.getId();
                    if (movieId != 0 ) {
                        final ProgressDialog progressDialog = new ProgressDialog(mContext);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Gathering Information");
                        progressDialog.show();
                        Utils.getMovieDetails(movieId, mContext, holder.image, new SuccessCallback() {
                            @Override
                            public void success() {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void error() {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                showError(mContext);
                            }
                        });

//                        Intent intent = new Intent(mContext, MovieDetailsActivityTabbed.class);
//                        intent.putExtra("movie", movie);
//                        intent.putExtra("transitionName", ViewCompat.getTransitionName(holder.image));
//                        if (Build.VERSION.SDK_INT >= 21) {
//                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                                    (Activity) mContext,
//                                    holder.image,
//                                    ViewCompat.getTransitionName(holder.image));
//                            mContext.startActivity(intent, optionsCompat.toBundle());
//                        } else {
//                            mContext.startActivity(intent);
//                        }
//                    } else {
//                        showError(mContext);
                    }
                }
            }
        );
    }

    private void showError(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.retrieve_info_error_title);
        builder.setMessage("Unfortunately, the info  for this movie could not be retrieved, please try again later.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }
    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.recycle_IV);
            progressBar = itemView.findViewById(R.id.movie_poster_progressbar);
        }
    }
}
