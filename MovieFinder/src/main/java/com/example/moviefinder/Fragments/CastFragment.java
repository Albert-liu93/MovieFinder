package com.example.moviefinder.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moviefinder.Adapters.CastRecyclerViewAdapter;
import com.example.moviefinder.Adapters.RecyclerViewAdapter;
import com.example.moviefinder.Model.Cast;
import com.example.moviefinder.R;
import com.google.api.client.json.Json;
import com.google.api.client.util.StringUtils;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CastFragment extends Fragment {

    ProgressBar progressBar;
    ArrayList<Cast> castArrayList;
    TextView noneFoundTV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_movie_cast, container, false);
        progressBar = view.findViewById(R.id.cast_progressBar);
        noneFoundTV = view.findViewById(R.id.cast_nonefound_TV);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("cast")) {
            castArrayList = (ArrayList<Cast>) bundle.getSerializable("cast");
            if (!castArrayList.isEmpty()) {
                renderCast(view);
            } else {
                noneFoundTV.setVisibility(View.VISIBLE);
            }
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private boolean checkIntegrity(JsonObject castJson) {
        JsonArray castArray;
        castArray = castJson.getAsJsonArray("cast").getAsJsonArray();
        if (castJson.getAsJsonArray("cast").isJsonNull() || castArray.size() == 0) {
            return false;
        } else {
            return true;
        }
    }



    public void renderCast(View view) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.cast_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        CastRecyclerViewAdapter castRecyclerViewAdapter = new CastRecyclerViewAdapter(getContext(), castArrayList);
        recyclerView.setAdapter(castRecyclerViewAdapter);

    }
}
