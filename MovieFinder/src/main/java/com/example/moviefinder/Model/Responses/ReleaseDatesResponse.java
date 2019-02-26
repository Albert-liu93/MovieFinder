package com.example.moviefinder.Model.Responses;

import java.io.Serializable;
import java.util.List;

import com.example.moviefinder.Model.Results.ReleaseDateResults;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ReleaseDatesResponse implements Serializable {

    @SerializedName("results")
    @Expose
    private List<ReleaseDateResults> results = null;

    public List<ReleaseDateResults> getResults() {
        return results;
    }

    public void setResults(List<ReleaseDateResults> results) {
        this.results = results;
    }

}