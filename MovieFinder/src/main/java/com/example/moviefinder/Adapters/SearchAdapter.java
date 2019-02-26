package com.example.moviefinder.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moviefinder.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> titles;
    private ArrayList<String> posterBackDrop;
    private ArrayList<String> dates;


    public SearchAdapter(Context context, ArrayList<String> mTitles, ArrayList<String> mPosterBackDrops, ArrayList<String> mDates ) {
        this.mContext = context;
        this.titles = mTitles;
        this.posterBackDrop = mPosterBackDrops;
        this.dates = mDates;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.movie_search_row, parent);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String title = titles.get(position);
        String year = dates.get(position);
        String URL = posterBackDrop.get(position);

        holder.title.setText(title);
        holder.year.setText(year);

        Picasso.get()
                .load(URL)
                .error(R.drawable.ic_photo_grey_24dp)
                .into(holder.poster, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView year;
        ImageView poster;
        ProgressBar progressBar;


        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.movie_search_TV);
            year = itemView.findViewById(R.id.movie_search_date_TV);
            poster = itemView.findViewById(R.id.movie_search_IV);
            progressBar = itemView.findViewById(R.id.movieSearch_IV_ProgressBar);
        }
    }

}
