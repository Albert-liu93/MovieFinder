package com.example.moviefinder.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.moviefinder.AboutActivity;
import com.example.moviefinder.Callbacks.SuccessCallback;
import com.example.moviefinder.Constants.Constants;
import com.example.moviefinder.Model.Movie;
import com.example.moviefinder.R;
import com.example.moviefinder.Utils.Utils;
import com.raizlabs.android.dbflow.sql.language.Operator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context mContext;
    List<Movie> movieList = new ArrayList<>();


    public SearchAdapter(Context context, List<Movie> movieList ) {
        this.mContext = context;
        this.movieList = movieList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_search_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void addItems(List<Movie> movies) {
        this.movieList.addAll(movies);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.title.setText("");
        holder.year.setText("");

        if (Utils.isStringNotNullorBlank(movieList.get(position).getTitle())) {
            holder.title.setText(movieList.get(position).getTitle());
        }
        if (Utils.isStringNotNullorBlank(movieList.get(position).getReleaseDate())) {
            String date = movieList.get(position).getReleaseDate();
                    if (!date.isEmpty() || date.length() > 4) {
                        date = date.substring(0,4);
                    } else {
                        date = "N/A";
                    }
            holder.year.setText(date);
        }
        Picasso.get()
                .load(Constants.movieDB_Image_URL + movieList.get(position).getPosterPath())
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

        ViewCompat.setTransitionName(holder.poster, movieList.get(position).getId().toString());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(mContext);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Gathering Information");
                progressDialog.show();
                Utils.getMovieDetails(movieList.get(position).getId(), mContext, holder.poster, new SuccessCallback() {
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
            }
        });
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
    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layout;
        TextView title;
        TextView year;
        ImageView poster;
        ProgressBar progressBar;


        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.movie_search_row_container);
            title = itemView.findViewById(R.id.movie_search_TV);
            year = itemView.findViewById(R.id.movie_search_date_TV);
            poster = itemView.findViewById(R.id.movie_search_IV);
            progressBar = itemView.findViewById(R.id.movieSearch_IV_ProgressBar);
        }
    }

    public interface OnClickListener {
        void onClick(int position);
    }

}
