package com.example.moviefinder.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moviefinder.Constants.Constants;
import com.example.moviefinder.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.HashMap;


public class CastRecyclerViewAdapter extends RecyclerView.Adapter<CastRecyclerViewAdapter.ViewHolder>{

    HashMap<Integer, ArrayList<String>> cast;
    Context mContext;

    public CastRecyclerViewAdapter(Context context, HashMap<Integer, ArrayList<String>> castMembers) {
        this.cast = castMembers;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_cast_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ArrayList<String> temp = cast.get(position);
        String name = temp.get(0);
        String url = temp.get(1);

        ImageLoader imageLoader = ImageLoader.getInstance();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(android.R.drawable.stat_notify_error)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .build();
        String imageURL;
        if (url.isEmpty()) {
            imageURL = "";
        } else {
            imageURL = Constants.movieDB_Image_URL + url;
        }
        imageLoader.displayImage(imageURL, holder.image, options);
        holder.name.setText(name);
    }

    @Override
    public int getItemCount() {
        return cast.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cast_picture_IV);
            name = itemView.findViewById(R.id.cast_name_TV);
        }
    }

}
