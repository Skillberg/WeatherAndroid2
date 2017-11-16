package com.skillberg.weather2.api;

import com.skillberg.weather2.api.models.CurrentWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * API Interface
 */
public interface Api {

    @GET("weather")
    Call<CurrentWeather> getCurrentWeather(
            @Query("lat") double latitude,
            @Query("lon") double longitude,

            @Query("appid") String apiKey,
            @Query("units") String units
    );

}