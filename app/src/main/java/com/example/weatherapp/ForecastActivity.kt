package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ForecastActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private val adapterData = listOf<DayForecast>(
        DayForecast(1644442980),
        DayForecast(1644529380),
        DayForecast(1644615780),
        DayForecast(1644702180),
        DayForecast(1644788580),
        DayForecast(1644874980),
        DayForecast(1644961380),
        DayForecast(1645047780),
        DayForecast(1645134180),
        DayForecast(1645220580),
        DayForecast(1645306980),
        DayForecast(1645393380),
        DayForecast(1645479780),
        DayForecast(1645566180),
        DayForecast(1645652580),
        DayForecast(1645738980),

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        recyclerView = findViewById(R.id.forecastView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = MyAdapter(adapterData)
    }
}