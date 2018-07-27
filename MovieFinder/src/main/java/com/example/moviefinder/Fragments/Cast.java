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

import com.example.moviefinder.Adapters.CastRecyclerViewAdapter;
import com.example.moviefinder.Adapters.RecyclerViewAdapter;
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

public class Cast extends Fragment {

    ProgressBar progressBar;
    JsonObject castJson;
    HashMap<Integer, ArrayList<String>> castHashmap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_movie_cast, container, false);
        progressBar = view.findViewById(R.id.cast_progressBar);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("JSONObject")) {
            String jsonString = bundle.getString("JSONObject");
            JsonParser jsonParser = new JsonParser();
            castJson = (JsonObject) jsonParser.parse(jsonString);
            getInfo(castJson, view);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }


        return view;
    }


    private void getInfo(JsonObject castJson, View view) {
        JsonArray castArray = castJson.getAsJsonArray("cast");
        int MAX_RESULT = 0;
        for (JsonElement element : castArray) {
            if (MAX_RESULT == 5) {
                break;
            } else {
                JsonObject castObject = element.getAsJsonObject();
                if (castObject.isJsonNull() || castObject == null) {
                    break;
                }
                Log.e("castobject", " = " + castObject);
                ArrayList<String> castpicList = new ArrayList<>();
                if (!castObject.get("name").isJsonNull()) {
                    castpicList.add(castObject.get("name").getAsString());
                } else {
                    castpicList.add("");
                }
                if (!castObject.get("profile_path").isJsonNull()) {
                    castpicList.add(castObject.get("profile_path").getAsString());
                } else {
                    castpicList.add("");
                }
                if (!castObject.get("order").isJsonNull()) {
                    castHashmap.put(castObject.get("order").getAsInt(), castpicList);
                } else {
                    castHashmap.put(MAX_RESULT, castpicList);
                }
            }
            MAX_RESULT++;
        }

        if (!castHashmap.isEmpty()) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            RecyclerView recyclerView = view.findViewById(R.id.cast_recyclerview);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            CastRecyclerViewAdapter castRecyclerViewAdapter = new CastRecyclerViewAdapter(getContext(), castHashmap );
            recyclerView.setAdapter(castRecyclerViewAdapter);
        }

    }
}
