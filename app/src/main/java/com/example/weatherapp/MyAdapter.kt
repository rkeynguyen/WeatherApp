package com.example.weatherapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MyAdapter(private val data: List<DayForecast>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private var dateView: TextView = view.findViewById(R.id.date)
        private var tempView: TextView = view.findViewById(R.id.temperature)
        private var highView: TextView = view.findViewById(R.id.high)
        private var lowView: TextView = view.findViewById(R.id.low)
        private var sunriseView: TextView = view.findViewById(R.id.sunrise)
        private var sunsetView: TextView = view.findViewById(R.id.sunset)
        private var conditionIcon: ImageView = view.findViewById(R.id.condition_icon)

        @SuppressLint("NewApi")
        fun bind(dayForecast: DayForecast){
            //set up icon
            val iconName = dayForecast.weather.firstOrNull()?.icon
            val iconUrl = "https://openweathermap.org/img/wn/${iconName}@2x.png"

            Glide.with(conditionIcon)
                .load(iconUrl)
                .into(conditionIcon)

            //month and day view
            var instant = Instant.ofEpochSecond(dayForecast.dt)
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