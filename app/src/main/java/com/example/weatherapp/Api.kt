package com.example.weatherapp

import com.example.weatherapp.dataclass.CurrentConditions
import com.example.weatherapp.dataclass.Forecast
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("weather")
    suspend fun getCurrentConditions(
        @Query("zip") zip: String,
        @Query("units") units: String = "imperial",
        @Query("appid") appId: String = "08bfedc04e02aa05f572eb2fb9b6d25e",
    ) : CurrentConditions

    @GET("forecast/daily")
    suspend fun getForecast(
        @Query("zip") zip: String,
        @Query("units") units: String = "imperial",
        @Query("appid") appId: String = "08bfedc04e02aa05f572eb2fb9b6d25e",
        @Query("cnt") count: String = "16"
    ) : Forecast


}