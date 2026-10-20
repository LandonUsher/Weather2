package com.example.weather

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class MainActivity : AppCompatActivity() {

    private lateinit var locationText: TextView
    private lateinit var timeText: TextView
    private lateinit var temperatureText: TextView
    private lateinit var windText: TextView

    private var cityIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Main layout
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(60, 100, 60, 60)
        }

        // Title
        val titleText = TextView(this).apply {
            text = "Weather Checker"
            textSize = 30f
            setTypeface(null, Typeface.BOLD)
        }

        // Location
        locationText = TextView(this).apply {
            textSize = 22f
            setTypeface(null, Typeface.BOLD)
        }

        // API results
        timeText = TextView(this).apply {
            textSize = 18f
        }

        temperatureText = TextView(this).apply {
            textSize = 20f
        }

        windText = TextView(this).apply {
            textSize = 20f
        }

        // Button
        val newWeatherButton = Button(this).apply {
            text = "Get New Weather"
        }

        // Add everything to the screen
        layout.addView(titleText)
        layout.addView(locationText)
        layout.addView(timeText)
        layout.addView(temperatureText)
        layout.addView(windText)
        layout.addView(newWeatherButton)

        setContentView(layout)

        // Get weather when app first starts
        getWeather()

        // Get a different location when button is clicked
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
        timeText.text = "Loading..."
        temperatureText.text = ""
        windText.text = ""

        val url =
            "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,wind_speed_10m"

        val client = AsyncHttpClient()
        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JSON
            ) {
                val current = json.jsonObject.getJSONObject("current")
                val time = current.getString("time")
                val temperature = current.getDouble("temperature_2m")
                val wind = current.getDouble("wind_speed_10m")

                timeText.text = "Time: $time"
                temperatureText.text = "Temperature: $temperature"
                windText.text = "Wind Speed: $wind"
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                timeText.text = "Could not load weather."
                temperatureText.text = ""
                windText.text = ""
            }
        })
    }
}
