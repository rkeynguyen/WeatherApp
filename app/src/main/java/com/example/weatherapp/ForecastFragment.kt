package com.example.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentForecastBinding
import com.example.weatherapp.dataclass.Forecast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForecastFragment : Fragment() {
    private val args: ForecastFragmentArgs by navArgs()
    private lateinit var binding: FragmentForecastBinding
    @Inject lateinit var viewModel : ForecastViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().title = "Forecast"
        binding = FragmentForecastBinding.inflate(inflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.forecastView.layoutManager = LinearLayoutManager(activity)
        viewModel.forecast.observe(viewLifecycleOwner){
            bindData(it)
        }
        viewModel.loadData(args.zipCode)
    }
    private fun bindData(forecast: Forecast) {
        binding.forecastView.adapter = MyAdapter(forecast.list)
    }
}