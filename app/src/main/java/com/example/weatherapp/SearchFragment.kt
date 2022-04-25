package com.example.weatherapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
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
    private val CHANNEL_ID = "channel_id"
    private val notificationId = 101
    private var makeNotification = false
    private var notificationsOn = false

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
        binding.notification.text = "Turn Notifications On"
        createNotificationChannel()

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
            makeNotification = false
            //using lon lat so setting zip to an invalid value used for checking
            viewModel.updateZipCode("-11111")
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestLocationPermission()
            }else{
                getLocation()
            }
        }

        //notification button handling
        binding.notification.setOnClickListener{
            if(!notificationsOn) {
                makeNotification = true
                viewModel.updateZipCode("-11111")
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestLocationPermission()
                } else {
                    getLocation()
                }
            }
            else{
                cancelNotification()
                binding.notification.text = "Turn Notifications On"
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

                    //if its making a notification send a notification else go to current conditions
                    if(makeNotification){
                        notificationsOn = true
                        binding.notification.text = "Turn Notifications Off"
                        sendNotification()
                    } else{
                        viewModel.currentConditions.observe(viewLifecycleOwner) { current ->
                            val action = SearchFragmentDirections.searchToCurrent(
                                current,
                                viewModel.zipCode.value,
                                viewModel.longitude.value.toString(),
                                viewModel.latitude.value.toString()
                            )
                            findNavController().navigate(action)
                        }
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

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "test"
            val descriptionText = "testing"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(){
        val intent = Intent(requireContext(), MainActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        var cityName:String = ""
        var temp:String = ""

        viewModel.currentConditions.observe(viewLifecycleOwner){ current ->
            cityName = current.name
            temp = current.main.temp.toInt().toString()
        }
        val builder : NotificationCompat.Builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.sun)
            .setContentTitle("Current Weather")
            .setStyle(NotificationCompat.BigTextStyle().bigText(temp + "°, "+ cityName))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(requireContext())){
            notify(notificationId, builder.build())
        }
    }

    private fun cancelNotification(){
        notificationsOn = false
        val notificationManager: NotificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}