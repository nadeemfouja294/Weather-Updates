package com.logical.weatherupdate.city_updates

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.logical.weatherupdate.adapter.DaysAdapter
import com.logical.weatherupdate.adapter.HoursAdapter
import com.logical.weatherupdate.databinding.ActivityCityUpdatesBinding
import com.logical.weatherupdate.models.ModelItemDays
import com.logical.weatherupdate.models.ModelItemHours
import com.logical.weatherupdate.utilities.hideKeyboard
import com.logical.weatherupdate.utilities.toast
import com.logical.weatherupdate.viewModel.CityUpdatesViewModel
import com.squareup.picasso.Picasso
import java.util.*

class CityUpdatesActivity : AppCompatActivity() {
    private var hourlyDataList= mutableListOf<ModelItemHours>()
    private var dailyDataList= mutableListOf<ModelItemDays>()
    private lateinit var hoursAdapter:HoursAdapter
    private lateinit var daysAdapter:DaysAdapter
    private lateinit var binding:ActivityCityUpdatesBinding
    private var cityName="Slough"
    private val context:Context=this
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION_CODE=1
    private lateinit var cityUpdatesViewModel:CityUpdatesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityUpdatesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        getUserLocation()

        initializeViewModelObject()

        cityUpdatesViewModel.getWeatherInfo(cityName)

        getSearchedCityName()


    }

    private fun initializeViewModelObject() {
        cityUpdatesViewModel= CityUpdatesViewModel(this, object :CityUpdatesViewModel.CallbackListener {
            override fun showTodayWeatherInfo() {
                showTodayWeather()
            }

            override fun updateDataListForHourlyUpdates() {
                updateDataListAndSetHoursAdapter()
            }

            override fun updateDataListForDailyUpdates() {
                updateDataListAndSetDaysAdapter()
            }
        })
    }

    private fun updateDataListAndSetDaysAdapter() {
        dailyDataList = cityUpdatesViewModel.dataListForDays
        daysAdapter = DaysAdapter(context, dailyDataList)
        binding.recycleViewDays.adapter = daysAdapter

        //Hide key board
        hideKeyboard(this@CityUpdatesActivity)
    }

    private fun updateDataListAndSetHoursAdapter() {
        hourlyDataList=cityUpdatesViewModel.dataListForNextTwentyFourHours
        hourlyDataList[0].time="Now"
        hoursAdapter= HoursAdapter(context,hourlyDataList)
        binding.recycleViewHours.adapter=hoursAdapter

        //Adjust today's current temperature
        val currentTemperature=hourlyDataList[0].temperature
        binding.tvTemperature.text="${currentTemperature}°c"

    }

    private fun getUserLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@CityUpdatesActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_CODE)

        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                cityName=getLastKnownLocation(location!!.longitude,location!!.latitude)

            }
    }

    private fun getLastKnownLocation(longitude: Double, latitude: Double): String {
        var cityName="Glasgow"
        val gcd=Geocoder(context,Locale.getDefault())
        try {
            var adresses=gcd.getFromLocation(latitude,longitude,10)
        for(add in adresses){
            if(add!=null){
                val city=add.locality
                if(city.isNullOrEmpty())
                    cityName= city
                else toast("Default location can't be accessed")
            }
        }

        }catch (e:Exception){
            e.printStackTrace()
            toast(e.localizedMessage)
        }
        return cityName
    }

    private fun showTodayWeather() {
        val tempInInt=cityUpdatesViewModel.model.temperature
        binding.tvCityName.text=cityName
        binding.tvCondition.text=cityUpdatesViewModel.model.condition
        binding.tvTemperature.text="$tempInInt °c"
        Picasso.get().load("https:".plus(cityUpdatesViewModel.model.conditionIcon)).into(binding.ivCondition)
        //Picasso.get().load("https:".plus("url").into(uiiLayout)



        //Picasso.with(this).load("https:".plus(mainActivityViewModel.model.conditionIcon)).into(uiiLayout)
    }
    private fun getSearchedCityName() {
        binding.etEnterCityName.setOnClickListener{
            //hideKeyboard(this@MainActivity)
            cityName=binding.etEnterCityName.text.toString()
            if(cityName.isNullOrEmpty()){
                Toast.makeText(this,"Please enter a valid city name",Toast.LENGTH_LONG).show()
            }else{
                cityUpdatesViewModel.getWeatherInfo(cityName)
                binding.etEnterCityName.setText("")
            }
        }
    }


}