package com.example.weatherapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private val apiKey = "08bfedc04e02aa05f572eb2fb9b6d25e"
    private lateinit var api: Api

    private lateinit var cityView: TextView
    private lateinit var tempView: TextView
    private lateinit var feelsLikeView: TextView
    private lateinit var lowView: TextView
    private lateinit var highView: TextView
    private lateinit var humidityView: TextView
    private lateinit var pressureView: TextView
    private lateinit var conditionIcon: ImageView

    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityView = findViewById(R.id.city_name)
        tempView = findViewById(R.id.temperature)
        feelsLikeView = findViewById(R.id.feels_like)
        lowView = findViewById(R.id.low)
        highView = findViewById(R.id.high)
        humidityView = findViewById(R.id.humidity)
        pressureView = findViewById(R.id.pressure)
        conditionIcon = findViewById(R.id.condition_icon)


        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        api = retrofit.create(Api::class.java)

        button = findViewById(R.id.forecastButton)
        button.setOnClickListener { startActivity(Intent(this, ForecastActivity::class.java)) }

    }

    override fun onResume() {
        super.onResume()
        val call: Call<CurrentConditions> = api.getCurrentConditions("55423")
        call.enqueue(object : Callback<CurrentConditions>{
            override fun onResponse(call: Call<CurrentConditions>, response: Response<CurrentConditions>) {
                val currentConditions = response.body()
                currentConditions?.let{
                    bindData(it)
                }
            }

            override fun onFailure(call: Call<CurrentConditions>, t: Throwable) {

            }

        })
    }

    private fun bindData(currentConditions: CurrentConditions){
        cityView.text = currentConditions.name
        tempView.text = getString(R.string.temperature, currentConditions.main.temp.toInt())
        feelsLikeView.text = getString(R.string.feels_like, currentConditions.main.feelsLike.toInt())
        lowView.text = getString(R.string.low, currentConditions.main.tempMin.toInt())
        highView.text = getString(R.string.high, currentConditions.main.tempMax.toInt())
        humidityView.text = getString(R.string.humidity, currentConditions.main.humidity.toInt())
        pressureView.text = getString(R.string.pressure, currentConditions.main.pressure.toInt())

        val iconName = currentConditions.weather.firstOrNull()?.icon
        val iconUrl = "https://openweathermap.org/img/wn/${iconName}@2x.png"

        Glide.with(this)
            .load(iconUrl)
            .into(conditionIcon)
    }
}