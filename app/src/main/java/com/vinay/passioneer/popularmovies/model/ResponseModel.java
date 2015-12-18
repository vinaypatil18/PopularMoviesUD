package com.vinay.passioneer.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResponseModel {
    @SerializedName("results")
    List<MovieModel> results = new ArrayList<>();

    public List<MovieModel> getResults() {
        return results;
    }
}
