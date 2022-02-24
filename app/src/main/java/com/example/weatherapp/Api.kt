package com.example.weatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("weather")
    fun getCurrentConditions(
        @Query("zip") zip: String,
        @Query("units") units: String = "imperial",
        @Query("appid") appId: String = "08bfedc04e02aa05f572eb2fb9b6d25e",
    ) : Call<CurrentConditions>

    @GET("forecast/daily")
    fun getForecast(
        @Query("zip") zip: String,
        @Query("units") units: String = "imperial",
        @Query("appid") appId: String = "08bfedc04e02aa05f572eb2fb9b6d25e",
        @Query("cnt") count: String = "16"
    ) : Call<Forecast>


}