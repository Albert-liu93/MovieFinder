package com.example.moviefinder.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.moviefinder.Constants.Constants;
import com.example.moviefinder.R;
import com.example.moviefinder.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Albert on 5/24/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> posterURLs;
    private HashMap<Integer, Integer> idHashMap;
    private Context mContext;

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

        ImageLoader imageLoader = ImageLoader.getInstance();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(android.R.drawable.stat_notify_error)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .build();

        String imageURL = Constants.movieDB_Image_URL + posterURLs.get(position);
        imageLoader.displayImage(imageURL, holder.image, options);

        holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Clicked on image url " + posterURLs.get(position));
                    Log.d(TAG, "Movie has id" + idHashMap.get(position+1));
                    int movieId = idHashMap.get(position+1);
                    Utils.getMovieDetails(movieId, mContext, holder.itemView, R.id.recycle_IV);
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

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.recycle_IV);

        }
    }









}
