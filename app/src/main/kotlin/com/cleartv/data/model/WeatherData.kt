package com.cleartv.data.model

import kotlinx.serialization.Serializable

/**
 * Weather data from Open-Meteo API.
 */
data class WeatherData(
    val current: CurrentWeather,
    val forecast: List<DayForecast>,
    val locationName: String = "",
)

data class CurrentWeather(
    val temperature: Double,
    val weatherCode: Int,
    val conditionText: String,
    val conditionIcon: String,
)

data class DayForecast(
    val dayName: String,
    val high: Double,
    val low: Double,
    val weatherCode: Int,
    val conditionIcon: String,
)

/**
 * WMO Weather interpretation codes → human-readable text + emoji.
 * https://www.nodc.noaa.gov/archive/arc0021/0002199/1.1/data/0-data/HTML/WMO-CODE/WMO4677.HTM
 */
object WeatherCodes {
    fun toCondition(code: Int): Pair<String, String> = when (code) {
        0 -> "Clear Sky" to "☀️"
        1 -> "Mainly Clear" to "🌤"
        2 -> "Partly Cloudy" to "⛅"
        3 -> "Overcast" to "☁️"
        45, 48 -> "Fog" to "🌫"
        51, 53, 55 -> "Drizzle" to "🌦"
        56, 57 -> "Freezing Drizzle" to "🌧"
        61, 63, 65 -> "Rain" to "🌧"
        66, 67 -> "Freezing Rain" to "🌧"
        71, 73, 75 -> "Snow" to "🌨"
        77 -> "Snow Grains" to "🌨"
        80, 81, 82 -> "Showers" to "🌧"
        85, 86 -> "Snow Showers" to "🌨"
        95 -> "Thunderstorm" to "⛈"
        96, 99 -> "Thunderstorm + Hail" to "⛈"
        else -> "Unknown" to "❓"
    }
}
