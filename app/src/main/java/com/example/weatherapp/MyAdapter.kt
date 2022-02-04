package com.example.weatherapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MyAdapter(private val data: List<DayForecast>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val dateView: TextView = view.findViewById(R.id.date)
        private val tempView: TextView = view.findViewById(R.id.temperature)
        private val highView: TextView = view.findViewById(R.id.high)
        private val lowView: TextView = view.findViewById(R.id.low)
        private val sunriseView: TextView = view.findViewById(R.id.sunrise)
        private val sunsetView: TextView = view.findViewById(R.id.sunset)


        @SuppressLint("NewApi")
        fun bind(dayForecast: DayForecast){
            //month and day view
            var instant = Instant.ofEpochSecond(dayForecast.date)
            var dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            var formatter = DateTimeFormatter.ofPattern("MMM dd")
            dateView.text = formatter.format(dateTime)

            //temperature views
            val temp = dayForecast.temp
            tempView.text = "Temp: " + temp.day.toString() + "°"
            highView.text = "High: " + temp.max.toString() + "°"
            lowView.text = "Low: " + temp.min.toString() + "°"

            //sunrise and sunset view
            instant = Instant.ofEpochSecond(dayForecast.sunrise)
            dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            formatter = DateTimeFormatter.ofPattern("hh:mm a")
            sunriseView.text = "Sunrise: " + formatter.format(dateTime)

            instant = Instant.ofEpochSecond(dayForecast.sunset)
            dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            formatter = DateTimeFormatter.ofPattern("hh:mm a")
            sunsetView.text = "Sunrise: " + formatter.format(dateTime)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.forecast_dates, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size
}