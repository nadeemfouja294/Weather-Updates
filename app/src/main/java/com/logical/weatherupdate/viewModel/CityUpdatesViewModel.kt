package com.logical.weatherupdate.viewModel

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.logical.weatherupdate.models.ModelItemDays
import com.logical.weatherupdate.models.ModelItemHours
import com.logical.weatherupdate.utilities.toast
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class CityUpdatesViewModel(private val context: Context, private val listener: CallbackListener) {
    private var time: String = ""
    private var temperature: String = ""
    private var conditionIcon: String = ""
    private var windSpeed: String = ""
    private var condition: String = ""
    var dataListForDays = mutableListOf<ModelItemDays>()
    private var dataListForTwoDays = mutableListOf<ModelItemHours>()
    var dataListForNextTwentyFourHours = mutableListOf<ModelItemHours>()
    var model = ModelItemHours(time, temperature, conditionIcon, windSpeed, condition)
    fun getWeatherInfo(cityName: String) {
        erasePreviousData()
        var url=
            "https://api.weatherapi.com/v1/forecast.json?key=7fb53f7c2fdb4ea8a63142816213011&q=${cityName}&days=3&aqi=no&alerts=no"
        WeatherTask(url).execute()
    }


    inner class WeatherTask(private val url: String) : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            var response: String = ""
            try {
                response = URL(url).readText(Charsets.UTF_8)
            } catch (e: Exception) {
                Log.i("MAIN", "${e.localizedMessage}")
                context.toast("${e.localizedMessage}")
            }
            return response
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPostExecute(result: String?) {
            try {
                super.onPostExecute(result)
                todayWeather(result)
                hourlyForecast(result)
                dailyForecast(result)

            } catch (e: Exception) {
                context.toast("Please provide a valid place name")
            }

        }

    }

    private fun dailyForecast(result: String?) {
        val resultObj = JSONObject(result)
        val forecast = resultObj.getJSONObject("forecast")


        // //Get  weather update for three days
        for (day in 0..2) {
            val forecastToday = forecast.getJSONArray("forecastday").getJSONObject(day)
            dailyForecastData(forecastToday, getDayOfTheWeek(day))
        }
        listener.updateDataListForDailyUpdates()
    }

    private fun dailyForecastData(forecast: JSONObject, dayOfTheWeek: String) {

        val dayData = forecast.getJSONObject("day")
        val minTemperatureDay = dayData.getString("mintemp_c")
        val maxTemperatureDay = dayData.getString("maxtemp_c")
        val conditionIconDay = dayData.getJSONObject("condition").getString("icon")
        val modelDay =
            ModelItemDays(minTemperatureDay, maxTemperatureDay, conditionIconDay, dayOfTheWeek)
        dataListForDays.add(modelDay)


    }

    private fun hourlyForecast(result: String?) {
        val resultObj = JSONObject(result)
        val forecast = resultObj.getJSONObject("forecast")

        //Get today weather update  from 00 to  24 hours
        val forecastToday = forecast.getJSONArray("forecastday").getJSONObject(0)
        hourlyForecastData(forecastToday)

        //Get tomorrow weather update  from 00 to  24 hours
        val forecastTomorrow = forecast.getJSONArray("forecastday").getJSONObject(1)
        hourlyForecastData(forecastTomorrow)

        //Get weather update from now to next 24 hours
        getDataForNextTwentyFourHours()
        listener.updateDataListForHourlyUpdates()

    }


    //Get today's weather update and show it on UI
    //This is overall whole day update (not per hour update)
    private fun todayWeather(result: String?) {
        val jsonObj = JSONObject(result)
        temperature = jsonObj.getJSONObject("current").getString("temp_c")
        condition =
            jsonObj.getJSONObject("current").getJSONObject("condition").getString("text")
        conditionIcon =
            jsonObj.getJSONObject("current").getJSONObject("condition").getString("icon")
        model = ModelItemHours(time, temperature, conditionIcon, windSpeed, condition)

        //Show today's weather update in UI
        listener.showTodayWeatherInfo()
    }

    // Save forecast data in  list(dataListForTwoDays)
    // The list(dataListForTwoDays) has per hour update
    // The list(dataListForTwoDays) will have 48 entries(1 entry/hour)
    private fun hourlyForecastData(forecast: JSONObject) {
        val hoursArray: JSONArray = forecast.getJSONArray("hour")
        for (i in 0 until hoursArray.length()) {
            val hour = hoursArray.getJSONObject(i)
            val hourTemperature = hour.getString("temp_c")
            val hourTime = hour.getString("time")
            val hourConditionIcon = hour.getJSONObject("condition").getString("icon")
            val hourWindSpeed = hour.getString("wind_kph")
            model = ModelItemHours(hourTime, hourTemperature, hourConditionIcon, hourWindSpeed, "")
            dataListForTwoDays.add(model)
        }
    }

    // Get forecast data for next 24 hours(from now to onward)
    // The list(dataListForNextTwentyFourHours) has per hour updates
    // The list(dataListForNextTwentyFourHours) will have 24 entries(1 entry/hour)
    private fun getDataForNextTwentyFourHours() {
        var currentHour = 0
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:00")
        val currentTime = simpleDateFormat.format(Date())

        //Get current hour
        for (i in 0 until dataListForTwoDays.size) {
            if (dataListForTwoDays[i].time == currentTime) {
                //dataListForTwoDays[i].time= "Now"
                break
            }
            currentHour++
        }

        //Get forecast data from now to next 24 hours in list
        for (iterate in 0..23) {
            dataListForNextTwentyFourHours.add(dataListForTwoDays[currentHour + iterate])
        }

    }

    private fun getDayOfTheWeek(add: Int): String {
        val calender = Calendar.getInstance()
        var date = Date()
        calender.time = date
        calender.add(Calendar.DATE, add)
        date = calender.time
        val sdf = SimpleDateFormat("EEEE")
        val dayOfTheWeek = sdf.format(date)
        return if (add == 0) "Today"
        else dayOfTheWeek
    }
    private fun erasePreviousData() {
        dataListForDays=mutableListOf<ModelItemDays>()
        dataListForTwoDays = mutableListOf<ModelItemHours>()
        dataListForNextTwentyFourHours = mutableListOf<ModelItemHours>()
    }

    interface CallbackListener {
        fun showTodayWeatherInfo()
        fun updateDataListForHourlyUpdates()
        fun updateDataListForDailyUpdates()
    }
}