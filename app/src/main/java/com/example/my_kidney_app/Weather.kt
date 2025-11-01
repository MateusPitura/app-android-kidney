package com.example.my_kidney_app

data class CurrentWeather(
    val temperature: Double
)

data class Weather(
    val current_weather: CurrentWeather
)
