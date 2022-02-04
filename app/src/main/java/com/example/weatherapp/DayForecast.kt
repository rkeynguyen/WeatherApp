package com.example.weatherapp

data class DayForecast(val date: Long,
                       val sunrise: Long = 1645682400,
                       val sunset: Long = 1645729200,
                       val temp: ForecastTemp = ForecastTemp(30f, 1f, 35f),
                       val pressure: Float = 1023f,
                       val humidity: Int = 100
                       )