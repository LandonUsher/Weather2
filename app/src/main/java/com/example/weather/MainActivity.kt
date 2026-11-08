package com.example.weather

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class MainActivity : AppCompatActivity() {

    private lateinit var locationText: TextView
    private lateinit var statusText: TextView
    private lateinit var newWeatherButton: Button
    private lateinit var weatherRecyclerView: RecyclerView

    private var cityIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find views from XML layout
        locationText = findViewById(R.id.locationText)
        statusText = findViewById(R.id.statusText)
        newWeatherButton = findViewById(R.id.newWeatherButton)
        weatherRecyclerView = findViewById(R.id.weatherRecyclerView)

        // Set up vertical RecyclerView
        weatherRecyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch weather when app starts
        getWeather()

        // Button listener to switch cities
        newWeatherButton.setOnClickListener {
            cityIndex = (cityIndex + 1) % 3
            getWeather()
        }
    }

    private fun getWeather() {
        val (cityName, latitude, longitude) = when (cityIndex) {
            1 -> Triple("New York", "40.7128", "-74.0060")
            2 -> Triple("Los Angeles", "34.0522", "-118.2437")
            else -> Triple("Berlin", "52.52", "13.41")
        }

        locationText.text = cityName
        statusText.text = "Loading forecast..."

        val url =
            "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&hourly=temperature_2m,wind_speed_10m,weather_code"

        val client = AsyncHttpClient()
        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JSON
            ) {
                try {
                    val hourly = json.jsonObject.getJSONObject("hourly")
                    val timeArray = hourly.getJSONArray("time")
                    val tempArray = hourly.getJSONArray("temperature_2m")
                    val windArray = hourly.getJSONArray("wind_speed_10m")
                    val codeArray = hourly.getJSONArray("weather_code")

                    val weatherList = mutableListOf<WeatherItem>()

                    for (i in 0 until timeArray.length()) {
                        val rawTime = timeArray.getString(i).replace("T", " ")
                        val temp = "${tempArray.getDouble(i)} °C"
                        val wind = "Wind: ${windArray.getDouble(i)} km/h"
                        val code = codeArray.getInt(i)

                        val condition = when (code) {
                            0 -> "Clear"
                            1, 2, 3 -> "Cloudy"
                            45, 48 -> "Fog"
                            51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> "Rain"
                            71, 73, 75, 77, 85, 86 -> "Snow"
                            95, 96, 99 -> "Thunderstorm"
                            else -> "Clear"
                        }

                        weatherList.add(
                            WeatherItem(
                                cityName = cityName,
                                time = rawTime,
                                temperature = temp,
                                windSpeed = wind,
                                weatherCode = code,
                                condition = condition
                            )
                        )
                    }

                    // Display entries in RecyclerView
                    weatherRecyclerView.adapter = WeatherAdapter(weatherList)
                    statusText.text = "Showing ${weatherList.size} forecast entries"

                } catch (_: Exception) {
                    statusText.text = "Error parsing weather data."
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                statusText.text = "Could not load weather forecast."
            }
        })
    }
}
