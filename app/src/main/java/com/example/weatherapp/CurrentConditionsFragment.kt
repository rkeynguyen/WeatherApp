package com.example.weatherapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.FragmentCurrentConditionsBinding
import com.example.weatherapp.dataclass.CurrentConditions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class CurrentConditionsFragment : Fragment() {

    private val args: CurrentConditionsFragmentArgs by navArgs()
    private lateinit var binding: FragmentCurrentConditionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().title = "Current Conditions"
        binding = FragmentCurrentConditionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindData(args.currentConditions)
        binding.forecastButton.setOnClickListener {
            val action = CurrentConditionsFragmentDirections.currentToForecast(args.zipCode)
            findNavController().navigate(action)
        }
    }


    private fun bindData(currentConditions: CurrentConditions){
        binding.cityName.text = currentConditions.name
        binding.temperature.text = getString(R.string.temperature, currentConditions.main.temp.toInt())
        binding.feelsLike.text = getString(R.string.feels_like, currentConditions.main.feelsLike.toInt())
        binding.low.text = getString(R.string.low, currentConditions.main.tempMin.toInt())
        binding.high.text = getString(R.string.high, currentConditions.main.tempMax.toInt())
        binding.humidity.text = getString(R.string.humidity, currentConditions.main.humidity.toInt())
        binding.pressure.text = getString(R.string.pressure, currentConditions.main.pressure.toInt())

        val iconName = currentConditions.weather.firstOrNull()?.icon
        val iconUrl = "https://openweathermap.org/img/wn/${iconName}@2x.png"

        Glide.with(this)
            .load(iconUrl)
            .into(binding.conditionIcon)
    }
}