package com.main.covis.network;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "https://covis-server-test.herokuapp.com";

    /**
     * This method returns retrofit client instance
     */
    public static Retrofit getClient() {

            return new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
    }
}
