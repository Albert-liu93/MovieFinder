package com.example.moviefinder.Callbacks;

import com.google.api.client.json.Json;
import com.google.gson.JsonObject;

public interface OnTaskCompleted {


    void onTaskCompleted(String result);

    void onTaskCompleted(JsonObject object);
}
