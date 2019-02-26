package com.example.moviefinder.Model.Results;

import java.io.Serializable;
import java.util.List;

import com.example.moviefinder.Model.ReleaseDate;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReleaseDateResults implements Serializable {

    @SerializedName("iso_3166_1")
    @Expose
    private String iso31661;
    @SerializedName("release_dates")
    @Expose
    private List<ReleaseDate> releaseDates = null;

    public String getIso31661() {
        return iso31661;
    }

    public void setIso31661(String iso31661) {
        this.iso31661 = iso31661;
    }

    public List<ReleaseDate> getReleaseDates() {
        return releaseDates;
    }

    public void setReleaseDates(List<ReleaseDate> releaseDates) {
        this.releaseDates = releaseDates;
    }
}
