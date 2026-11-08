package com.example.weather

data class WeatherItem(
    val cityName: String,
    val time: String,
    val temperature: String,
    val windSpeed: String,
    val weatherCode: Int,
    val condition: String
)
