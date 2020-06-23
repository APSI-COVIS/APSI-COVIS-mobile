package com.main.covis.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.main.covis.covid_plot.CovidCase;

import org.json.JSONArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/api/world/epidemy-info")
    Call<JsonObject> getCovidData(@Query("date") String date, @Query("type") String type);

    @GET("/api/countries/{country-slug}/epidemy-info")
    Call<List<CovidCase>> getListEpidemyDataInCountry(@Path("country-slug") String cSlug, @Query("from") String from, @Query("to")String to, @Query("type") String type);

    @GET("/api/countries/{country-slug}/epidemy-forecast")
    Call<List<CovidCase>> getListEpidemyForecastInCountry(@Path("country-slug") String cSlug, @Query("from") String from, @Query("to") String to, @Query("type") String type);

    @GET("/api/countries/{country-slug}/population")
    Call<JsonObject> getCountryPopulation(@Path("country-slug") String cSlug,@Query("when") String when);

}
