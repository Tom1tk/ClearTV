package com.cleartv.data

import com.cleartv.data.model.CurrentWeather
import com.cleartv.data.model.DayForecast
import com.cleartv.data.model.WeatherCodes
import com.cleartv.data.model.WeatherData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Weather API client using Open-Meteo (free, no API key required).
 * https://open-meteo.com/en/docs
 */
class WeatherRepository {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    /**
     * Fetches current weather and 4-day forecast for the given coordinates.
     */
    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        useCelsius: Boolean = true,
    ): Result<WeatherData> = withContext(Dispatchers.IO) {
        try {
            val tempUnit = if (useCelsius) "celsius" else "fahrenheit"

            val response: OpenMeteoResponse = client.get(BASE_URL) {
                parameter("latitude", latitude)
                parameter("longitude", longitude)
                parameter("current", "temperature_2m,weathercode")
                parameter("daily", "temperature_2m_max,temperature_2m_min,weathercode")
                parameter("forecast_days", 4)
                parameter("timezone", "auto")
                parameter("temperature_unit", tempUnit)
            }.body()

            val (conditionText, conditionIcon) = WeatherCodes.toCondition(
                response.current.weatherCode
            )

            val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

            val forecast = response.daily.time.mapIndexed { i, dateStr ->
                val dayName = if (i == 0) "Today" else {
                    try {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = sdf.parse(dateStr)
                        dayFormat.format(date ?: Date())
                    } catch (_: Exception) {
                        dateStr
                    }
                }
                val (_, icon) = WeatherCodes.toCondition(response.daily.weatherCode[i])
                DayForecast(
                    dayName = dayName,
                    high = response.daily.temperatureMax[i],
                    low = response.daily.temperatureMin[i],
                    weatherCode = response.daily.weatherCode[i],
                    conditionIcon = icon,
                )
            }

            Result.success(
                WeatherData(
                    current = CurrentWeather(
                        temperature = response.current.temperature,
                        weatherCode = response.current.weatherCode,
                        conditionText = conditionText,
                        conditionIcon = conditionIcon,
                    ),
                    forecast = forecast.drop(1), // drop today, show next 3
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Geocodes a city name or postcode to coordinates using Open-Meteo's geocoding API.
     */
    suspend fun geocode(query: String): Result<GeoResult> = withContext(Dispatchers.IO) {
        try {
            val response: GeoResponse = client.get(GEOCODE_URL) {
                parameter("name", query)
                parameter("count", 1)
                parameter("language", "en")
            }.body()

            val result = response.results?.firstOrNull()
            if (result != null) {
                Result.success(result)
            } else {
                Result.failure(Exception("Location not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val BASE_URL = "https://api.open-meteo.com/v1/forecast"
        private const val GEOCODE_URL = "https://geocoding-api.open-meteo.com/v1/search"
    }
}

// ─── API response models ─────────────────────────────────────────────────────

@Serializable
data class OpenMeteoResponse(
    val current: CurrentResponse,
    val daily: DailyResponse,
)

@Serializable
data class CurrentResponse(
    @SerialName("temperature_2m") val temperature: Double,
    @SerialName("weathercode") val weatherCode: Int,
)

@Serializable
data class DailyResponse(
    val time: List<String>,
    @SerialName("temperature_2m_max") val temperatureMax: List<Double>,
    @SerialName("temperature_2m_min") val temperatureMin: List<Double>,
    @SerialName("weathercode") val weatherCode: List<Int>,
)

@Serializable
data class GeoResponse(
    val results: List<GeoResult>? = null,
)

@Serializable
data class GeoResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    @SerialName("admin1") val region: String? = null,
)
