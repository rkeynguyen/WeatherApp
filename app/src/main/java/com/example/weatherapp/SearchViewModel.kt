package com.example.weatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.dataclass.CurrentConditions
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SearchViewModel @Inject constructor(private val api : Api): ViewModel() {
    private val _currentConditions: MutableLiveData<CurrentConditions> = MutableLiveData()
    val currentConditions: LiveData<CurrentConditions>
        get() = _currentConditions

    private val _enableButton = MutableLiveData(false)
    val enableButton: LiveData<Boolean>
        get() = _enableButton

    private val _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    private val _longitude: MutableLiveData<Double> = MutableLiveData()
    val longitude: LiveData<Double>
        get() = _longitude

    private val _latitude: MutableLiveData<Double> = MutableLiveData()
    val latitude: LiveData<Double>
        get() = _latitude

    private val _zipCode: MutableLiveData<String> = MutableLiveData()
    val zipCode: LiveData<String>
        get() = _zipCode

    fun updateZipCode(zipCode: String){
        if(zipCode != _zipCode.value){
            _zipCode.value = zipCode
            _enableButton.value = isValidZipCode(zipCode)
        }
    }

    fun updateLatLon(latitude: Double, longitude: Double){
        if(_latitude.value != latitude){
            _latitude.value = latitude
        }
        if(_longitude.value != longitude){
            _longitude.value = longitude
        }
    }

    private fun isValidZipCode(zipCode: String):Boolean {
        return zipCode.length == 5 && zipCode.all { it.isDigit() }
    }

    fun submitButtonClicked(){
        if(_zipCode.value != null && isValidZipCode(_zipCode.value?:"00000")){
            try {
                loadData()
                _showErrorDialog.value = false
            }catch (e: Exception){
                _showErrorDialog.value = true
            }
        }
    }

    fun locationButtonClicked(){
        loadDataLonLat()
    }

    private fun loadData() = runBlocking{
        launch {_currentConditions.value = _zipCode.value?.let { api.getCurrentConditions(it) } }
    }

    private fun loadDataLonLat()= runBlocking{
        launch {_currentConditions.value = api.getLatLonCurrentConditions(_latitude.value.toString(), _longitude.value.toString()) }
    }
}