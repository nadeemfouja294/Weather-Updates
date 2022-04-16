package com.logical.weatherupdate.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.logical.weatherupdate.R
import com.logical.weatherupdate.models.ModelItemHours
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class HoursAdapter(private val context: Context, private val dataList: List<ModelItemHours>): RecyclerView.Adapter<HoursAdapter.HoursViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursViewHolder {
       val view=LayoutInflater.from(context).inflate(R.layout.hours_item_layout,parent,false)
        return HoursViewHolder(view)
    }

    override fun onBindViewHolder(holder: HoursViewHolder, position: Int) {
        val modelItemHours: ModelItemHours =dataList[position]
        holder.tvTemperatureItem.text="${modelItemHours.temperature} °c"
        holder.tvWindSpeedItem.text="${modelItemHours.windSpeed} km/h"
        Picasso.get().load("https:".plus(modelItemHours.conditionIcon)).into(holder.ivConditionItem)
         val inputTimeFormat= SimpleDateFormat("yyyy-MM-dd hh:mm")
        val requiredTimeFormat=SimpleDateFormat("h aa")
        try {
            if(modelItemHours.time=="Now")
                holder.tvTimeItem.text="Now"
            else {
                val date: Date = inputTimeFormat.parse(modelItemHours.time)
                holder.tvTimeItem.text = requiredTimeFormat.format(date)
            }
        }catch (e:Exception){
            Log.i("WeatherAdapter",e.localizedMessage.toString())
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    inner class HoursViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val tvTimeItem:TextView=itemView.findViewById(R.id.tvTimeItem)
        val tvTemperatureItem:TextView=itemView.findViewById(R.id.tvTemperatureItem)
        val tvWindSpeedItem:TextView=itemView.findViewById(R.id.tvWindSpeedItem)
        val ivConditionItem:ImageView=itemView.findViewById(R.id.ivConditionItem)
    }
}