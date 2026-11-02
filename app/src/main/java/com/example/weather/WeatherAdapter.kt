package com.example.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeatherAdapter(private val weatherList: List<WeatherItem>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weatherIcon: ImageView = itemView.findViewById(R.id.weatherIcon)
        val timeText: TextView = itemView.findViewById(R.id.timeText)
        val tempText: TextView = itemView.findViewById(R.id.tempText)
        val windText: TextView = itemView.findViewById(R.id.windText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val item = weatherList[position]

        holder.timeText.text = item.time
        holder.tempText.text = item.temperature
        holder.windText.text = item.windSpeed

        // Set weather symbol image based on WMO weather code
        val iconRes = when (item.weatherCode) {
            0 -> R.drawable.ic_weather_sunny
            1, 2, 3, 45, 48 -> R.drawable.ic_weather_cloudy
            51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> R.drawable.ic_weather_rain
            71, 73, 75, 77, 85, 86 -> R.drawable.ic_weather_snow
            95, 96, 99 -> R.drawable.ic_weather_thunderstorm
            else -> R.drawable.ic_weather_sunny
        }
        holder.weatherIcon.setImageResource(iconRes)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }
}
