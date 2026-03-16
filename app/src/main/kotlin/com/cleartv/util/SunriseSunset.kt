package com.cleartv.util

import java.time.ZonedDateTime
import kotlin.math.*

/**
 * Simplified sunrise/sunset computation using the NOAA solar position algorithm.
 *
 * Accurate to within a few minutes for latitudes between ±60°.
 * Returns whether it is currently night (after sunset or before sunrise)
 * at the given coordinates.
 */
object SunriseSunset {

    /**
     * Returns true if it is currently nighttime at the given coordinates.
     */
    fun isNight(lat: Double, lon: Double): Boolean {
        val now = ZonedDateTime.now()
        val (sunriseMin, sunsetMin) = computeLocalSunriseSunset(lat, lon, now)
        val nowMin = now.hour * 60 + now.minute
        return nowMin < sunriseMin || nowMin >= sunsetMin
    }

    /**
     * Returns (sunriseMinuteOfDay, sunsetMinuteOfDay) in the device's local time.
     * Uses the NOAA fractional year approach.
     */
    private fun computeLocalSunriseSunset(
        lat: Double,
        lon: Double,
        now: ZonedDateTime,
    ): Pair<Int, Int> {
        val dayOfYear = now.dayOfYear
        val latRad = Math.toRadians(lat)

        // Fractional year (radians)
        val gamma = 2.0 * PI / 365.0 * (dayOfYear - 1 + (now.hour - 12.0) / 24.0)

        // Equation of time (minutes)
        val eqTime = 229.18 * (0.000075
                + 0.001868 * cos(gamma)
                - 0.032077 * sin(gamma)
                - 0.014615 * cos(2 * gamma)
                - 0.04089 * sin(2 * gamma))

        // Solar declination (radians)
        val decl = (0.006918
                - 0.399912 * cos(gamma)
                + 0.070257 * sin(gamma)
                - 0.006758 * cos(2 * gamma)
                + 0.000907 * sin(2 * gamma)
                - 0.002697 * cos(3 * gamma)
                + 0.00148 * sin(3 * gamma))

        // Cosine of hour angle at sunrise/sunset (solar zenith = 90.833°)
        val cosHa = (cos(Math.toRadians(90.833)) -
                sin(latRad) * sin(decl)) / (cos(latRad) * cos(decl))

        // Clamp: polar day / polar night
        if (cosHa <= -1.0) return Pair(0, 23 * 60 + 59)  // midnight sun
        if (cosHa >= 1.0) return Pair(12 * 60, 12 * 60)   // polar night

        val haDeg = Math.toDegrees(acos(cosHa))

        // Solar noon (minutes from midnight UTC)
        val solarNoonUtc = 720.0 - 4.0 * lon - eqTime

        val sunriseUtc = solarNoonUtc - 4.0 * haDeg
        val sunsetUtc = solarNoonUtc + 4.0 * haDeg

        // Convert UTC minutes to local minutes
        val offsetMin = now.offset.totalSeconds / 60
        val sunriseLocal = (sunriseUtc + offsetMin).toInt().coerceIn(0, 23 * 60 + 59)
        val sunsetLocal = (sunsetUtc + offsetMin).toInt().coerceIn(0, 23 * 60 + 59)

        return Pair(sunriseLocal, sunsetLocal)
    }
}
