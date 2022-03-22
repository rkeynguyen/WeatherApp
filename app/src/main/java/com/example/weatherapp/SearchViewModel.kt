package com.example.weatherapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.dataclass.CurrentConditions
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import javax.inject.Inject

class SearchViewModel @Inject constructor(private val api : Api): ViewModel() {
    private val _currentConditions: MutableLiveData<CurrentConditions> = MutableLiveData()
    val currentConditions: LiveData<CurrentConditions>
        get() = _currentConditions

    private val _enableButton = MutableLiveData(false)
    private val _showErrorDialog = MutableLiveData(false)

    var zipCode: String? = null

    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    val enableButton: LiveData<Boolean>
        get() = _enableButton

    fun updateZipCode(zipCode: String){
        if(zipCode != this.zipCode){
            this.zipCode = zipCode
            _enableButton.value = isValidZipCode(zipCode)
        }
    }

    private fun isValidZipCode(zipCode: String):Boolean {
        return zipCode.length == 5 && zipCode.all { it.isDigit() }
    }

    fun submitButtonClicked(){
        Log.d(SearchFragment::class.simpleName, zipCode ?: "No Zip Yet")

        if(zipCode != null && isValidZipCode(zipCode?:"00000")){
            try {
                loadData()
                _showErrorDialog.value = false
            }catch (e: Exception){
                _showErrorDialog.value = true
            }
        }
    }

    private fun loadData() = runBlocking{
        launch {_currentConditions.value = zipCode?.let { api.getCurrentConditions(it) } }
    }


}