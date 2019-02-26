package com.example.moviefinder.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReleaseDate implements Serializable {

    @SerializedName("certification")
    @Expose
    private String certification;
    @SerializedName("iso_639_1")
    @Expose
    private String iso6391;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("type")
    @Expose
    private Integer type;

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getIso6391() {
        return iso6391;
    }

    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
