package com.logical.weatherupdate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.logical.weatherupdate.R
import com.logical.weatherupdate.models.ModelItemDays
import com.squareup.picasso.Picasso

class DaysAdapter(private val context: Context, private val dataListDays:List<ModelItemDays>):RecyclerView.Adapter<DaysAdapter.DaysViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.days_item_layout,parent,false)
        return DaysViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaysViewHolder, position: Int) {
        val modelItemDays=dataListDays[position]
        holder.minTempDay.text="${modelItemDays.minTempDay}°c"
        holder.maxTempDay.text="${modelItemDays.maxTempDay}°c"
        holder.day.text=modelItemDays.day
        Picasso.get().load("https:".plus(modelItemDays.conditionIconDay)).into(holder.conditionIconDay)
    }

    override fun getItemCount(): Int {
       return dataListDays.size
    }
   inner class DaysViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
       val minTempDay:TextView=itemView.findViewById(R.id.tvMinTempDay)
       val maxTempDay:TextView=itemView.findViewById(R.id.tvMaxTempDay)
       val conditionIconDay:ImageView=itemView.findViewById(R.id.ivDayIcon)
       val day:TextView=itemView.findViewById(R.id.tvDay)

    }

}