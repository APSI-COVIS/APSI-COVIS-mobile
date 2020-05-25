package com.main.covis.network;

import com.google.gson.JsonObject;
import com.mapbox.geojson.GeoJson;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/api/world/epidemy-info")
    Call<JsonObject> getCovidData(@Query("date") String date, @Query("type") String type);
}
