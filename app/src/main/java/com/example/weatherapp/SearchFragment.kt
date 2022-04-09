package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.databinding.FragmentSearchBinding
import com.example.weatherapp.dataclass.CurrentConditions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    @Inject lateinit var viewModel : SearchViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_PERMISSION = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().title = "Search"
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding = FragmentSearchBinding.inflate(inflater)

        viewModel.enableButton.observe(viewLifecycleOwner){enable ->
            binding.submit.isEnabled = enable
        }

        viewModel.showErrorDialog.observe(viewLifecycleOwner){ showError ->
            if(showError){
                ErrorDialogFragment().show(childFragmentManager, ErrorDialogFragment.TAG)
            }
        }

        binding.zipCode.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.toString()?.let { viewModel.updateZipCode(it)}
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submit.setOnClickListener {
            viewModel.submitButtonClicked()
            viewModel.showErrorDialog.value?.let {
                if (!it) {
                     viewModel.currentConditions.observe(viewLifecycleOwner){ current ->
                         val conditions: CurrentConditions = current
                             val action = SearchFragmentDirections.searchToCurrent(conditions, viewModel.zipCode.value,viewModel.longitude.value.toString(),viewModel.latitude.value.toString())
                             findNavController().navigate(action)
                     }
                }
            }
        }

        //location button handling
        binding.location.setOnClickListener{
            //using lon lat so setting zip to an invalid value used for checking
            viewModel.updateZipCode("-11111")
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestLocationPermission()
            }else{
                getLocation()
            }
        }
    }

    //after requesting permission get location
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == REQUEST_LOCATION_PERMISSION){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation()
            }
        }
    }

    //get lat and lon of location and pass it to current conditions fragment
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {return}
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if(location != null){
                    viewModel.updateLatLon(location.latitude,location.longitude)
                    viewModel.locationButtonClicked()
                    viewModel.currentConditions.observe(viewLifecycleOwner){ current ->
                        val action = SearchFragmentDirections.searchToCurrent(current, viewModel.zipCode.value,viewModel.longitude.value.toString(),viewModel.latitude.value.toString())
                        findNavController().navigate(action)
                    }
                }
            }
    }

    //request permission from phone
    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)){
            showCoarseLocationRationale()
        }else{
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    //dialog box to ask for permission
    private fun showCoarseLocationRationale() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.location_permission_rationale)
            .setNeutralButton(R.string.ok){_,_ ->
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
            .create()
            .show()
    }
}