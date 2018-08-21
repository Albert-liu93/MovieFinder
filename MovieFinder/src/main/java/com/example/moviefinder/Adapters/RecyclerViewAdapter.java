package com.example.moviefinder.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
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

/**
 * Created by Albert on 5/24/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> posterURLs;
    private HashMap<Integer, Integer> idHashMap;
    private Context mContext;
    private OnTaskCompleted onTaskCompleted;

    public RecyclerViewAdapter(Context context, ArrayList<String> posterURLs, HashMap<Integer, Integer> idHM) {
        this.posterURLs = posterURLs;
        this.mContext = context;
        this.idHashMap = idHM;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_poster_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        String imageURL = Constants.movieDB_Image_URL + posterURLs.get(position);
        Picasso.get()
                .load(imageURL)
                .placeholder(R.drawable.ic_photo_grey_24dp)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
        ViewCompat.setTransitionName(holder.image, idHashMap.get(position+1).toString());
        holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog progressDialog = new ProgressDialog(mContext);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Gathering Information");
                    progressDialog.show();
                    Log.d(TAG, "Clicked on image url " + posterURLs.get(position));
                    Log.d(TAG, "Movie has id" + idHashMap.get(position+1));
                    int movieId = idHashMap.get(position+1);

                    Utils.getMovieDetails(movieId, mContext, null, holder.image, new SuccessCallback() {

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
                        }
                    });
                }
            }
        );
    }

    @Override
    public int getItemCount() {
        return posterURLs.size();
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
