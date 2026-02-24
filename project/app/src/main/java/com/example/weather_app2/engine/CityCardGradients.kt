package com.example.weather_app2.engine

/**
 * CityCardGradients provides weather-condition-based gradient colors
 * for city selection cards in the Weather 2 app.
 *
 * Contains 28 gradient variants: 14 weather conditions × 2 (day/night)
 * Each variant returns 3 colors (top, middle, bottom) for vertical LinearGradient
 */
object CityCardGradients {

    /**
     * Gets the card gradient colors (top, middle, bottom) based on weather condition and time of day
     *
     * @param wmoCode The WMO weather code from the API
     * @param isNight True if it's nighttime, false for daytime
     * @return IntArray of 3 colors [topColor, middleColor, bottomColor] for gradient
     */
    fun getCardGradient(wmoCode: Int, isNight: Boolean): IntArray {
        return when {
            // Clear/Sunny (0)
            wmoCode == 0 -> {
                if (isNight) intArrayOf(0xFF0D1A47.toInt(), 0xFF0D1A47.toInt(), 0xFF0D1A47.toInt())
                else intArrayOf(0xFF4A90D9.toInt(), 0xFF6BAFE0.toInt(), 0xFF87CEEB.toInt())
            }
            // Mainly clear (1)
            wmoCode == 1 -> {
                if (isNight) intArrayOf(0xFF0F1E4A.toInt(), 0xFF0F1E4A.toInt(), 0xFF0F1E4A.toInt())
                else intArrayOf(0xFF4A8BC8.toInt(), 0xFF6AA8D8.toInt(), 0xFF7EBCE5.toInt())
            }
            // Partly cloudy (2)
            wmoCode == 2 -> {
                if (isNight) intArrayOf(0xFF0D1A47.toInt(), 0xFF1A2555.toInt(), 0xFF0D1A47.toInt())
                else intArrayOf(0xFF5A7FA8.toInt(), 0xFF7A95BB.toInt(), 0xFF8FA8C8.toInt())
            }
            // Overcast (3)
            wmoCode == 3 -> {
                if (isNight) intArrayOf(0xFF1A2535.toInt(), 0xFF1A2535.toInt(), 0xFF0F1A2B.toInt())
                else intArrayOf(0xFF5F6E7F.toInt(), 0xFF7A8A9B.toInt(), 0xFF8FA3B8.toInt())
            }
            // Fog/Mist (45, 48)
            wmoCode == 45 || wmoCode == 48 -> {
                if (isNight) intArrayOf(0xFF0D1A47.toInt(), 0xFF1A2A40.toInt(), 0xFF0D1A47.toInt())
                else intArrayOf(0xFF7E8E9E.toInt(), 0xFF9AABBB.toInt(), 0xFFB5C5D5.toInt())
            }
            // Light drizzle (51)
            wmoCode == 51 -> {
                if (isNight) intArrayOf(0xFF4A5A6D.toInt(), 0xFF3A4A5D.toInt(), 0xFF2A3A4D.toInt())
                else intArrayOf(0xFF456070.toInt(), 0xFF607080.toInt(), 0xFF708090.toInt())
            }
            // Drizzle (53, 55)
            wmoCode == 53 || wmoCode == 55 -> {
                if (isNight) intArrayOf(0xFF4A5A6D.toInt(), 0xFF3A4A5D.toInt(), 0xFF2A3A4D.toInt())
                else intArrayOf(0xFF4A6080.toInt(), 0xFF607090.toInt(), 0xFF7080A0.toInt())
            }
            // Freezing drizzle (56, 57)
            wmoCode == 56 || wmoCode == 57 -> {
                if (isNight) intArrayOf(0xFF3A4A5D.toInt(), 0xFF2A3A50.toInt(), 0xFF1A2A40.toInt())
                else intArrayOf(0xFF4A6070.toInt(), 0xFF6A7580.toInt(), 0xFF7A8590.toInt())
            }
            // Light rain (61, 80)
            wmoCode == 61 || wmoCode == 80 -> {
                if (isNight) intArrayOf(0xFF4D5D70.toInt(), 0xFF3D4D60.toInt(), 0xFF2D3D50.toInt())
                else intArrayOf(0xFF354555.toInt(), 0xFF556575.toInt(), 0xFF708090.toInt())
            }
            // Moderate rain (63, 81)
            wmoCode == 63 || wmoCode == 81 -> {
                if (isNight) intArrayOf(0xFF3D4D60.toInt(), 0xFF2D3D50.toInt(), 0xFF1D2D40.toInt())
                else intArrayOf(0xFF2B3A47.toInt(), 0xFF4A5A67.toInt(), 0xFF656F7F.toInt())
            }
            // Heavy rain (65, 82)
            wmoCode == 65 || wmoCode == 82 -> {
                if (isNight) intArrayOf(0xFF1A2A3D.toInt(), 0xFF0D1A2D.toInt(), 0xFF050F1A.toInt())
                else intArrayOf(0xFF1E2B38.toInt(), 0xFF2A3845.toInt(), 0xFF151F2B.toInt())
            }
            // Sleet (66, 67)
            wmoCode == 66 || wmoCode == 67 -> {
                if (isNight) intArrayOf(0xFF455565.toInt(), 0xFF354555.toInt(), 0xFF254555.toInt())
                else intArrayOf(0xFF445565.toInt(), 0xFF607080.toInt(), 0xFF758090.toInt())
            }
            // Light snow (71, 85)
            wmoCode == 71 || wmoCode == 85 -> {
                if (isNight) intArrayOf(0xFF708090.toInt(), 0xFF608080.toInt(), 0xFF507070.toInt())
                else intArrayOf(0xFF90A3B5.toInt(), 0xFFA8B8C8.toInt(), 0xFFC0CDD8.toInt())
            }
            // Moderate snow (73, 77)
            wmoCode == 73 || wmoCode == 77 -> {
                if (isNight) intArrayOf(0xFF708090.toInt(), 0xFF607080.toInt(), 0xFF507070.toInt())
                else intArrayOf(0xFF9EB0C0.toInt(), 0xFFB0C0D0.toInt(), 0xFFC5D5E5.toInt())
            }
            // Heavy snow (75, 86)
            wmoCode == 75 || wmoCode == 86 -> {
                if (isNight) intArrayOf(0xFF708090.toInt(), 0xFF607070.toInt(), 0xFF506060.toInt())
                else intArrayOf(0xFF9EAFC0.toInt(), 0xFFB8C8D8.toInt(), 0xFFCDD5E0.toInt())
            }
            // Thunderstorm (95, 96, 99)
            wmoCode == 95 || wmoCode == 96 || wmoCode == 99 -> {
                if (isNight) intArrayOf(0xFF1A1A2E.toInt(), 0xFF0D0D1A.toInt(), 0xFF050508.toInt())
                else intArrayOf(0xFF2A2A3E.toInt(), 0xFF1A1A2E.toInt(), 0xFF0D0D1A.toInt())
            }
            // Default (fallback for unknown codes)
            else -> {
                if (isNight) intArrayOf(0xFF0D1A47.toInt(), 0xFF0D1A47.toInt(), 0xFF0D1A47.toInt())
                else intArrayOf(0xFF5A7FA8.toInt(), 0xFF7A95BB.toInt(), 0xFF8FA8C8.toInt())
            }
        }
    }

    /**
     * Gets the background gradient colors (top, middle, bottom) for full-screen backgrounds
     * These can have different color ranges than card gradients for better visual separation
     *
     * @param wmoCode The WMO weather code from the API
     * @param isNight True if it's nighttime, false for daytime
     * @return IntArray of 3 colors [topColor, middleColor, bottomColor] for gradient
     */
    fun getBackgroundGradient(wmoCode: Int, isNight: Boolean): IntArray {
        return when {
            // Clear/Sunny (0) - bright blue sky
            wmoCode == 0 -> {
                if (isNight) intArrayOf(0xFF0D1A47.toInt(), 0xFF0D1A47.toInt(), 0xFF0D1A47.toInt())
                else intArrayOf(0xFF3A7FD9.toInt(), 0xFF5B9FE5.toInt(), 0xFF8DDBF5.toInt())
            }
            // Mainly clear (1) - slightly less bright
            wmoCode == 1 -> {
                if (isNight) intArrayOf(0xFF0F1E4A.toInt(), 0xFF0F1E4A.toInt(), 0xFF0F1E4A.toInt())
                else intArrayOf(0xFF3A80D0.toInt(), 0xFF5BA5E0.toInt(), 0xFF7DCFE5.toInt())
            }
            // Partly cloudy (2) - lighter blue with gray tint
            wmoCode == 2 -> {
                if (isNight) intArrayOf(0xFF0D1A47.toInt(), 0xFF1A2555.toInt(), 0xFF0D1A47.toInt())
                else intArrayOf(0xFF5590B8.toInt(), 0xFF75B0D0.toInt(), 0xFF95C8E8.toInt())
            }
            // Overcast (3) - gray
            wmoCode == 3 -> {
                if (isNight) intArrayOf(0xFF1A2535.toInt(), 0xFF1A2535.toInt(), 0xFF0F1A2B.toInt())
                else intArrayOf(0xFF6F7E8F.toInt(), 0xFF8FA3B8.toInt(), 0xFFAFBBC8.toInt())
            }
            // Fog/Mist (45, 48) - white-gray
            wmoCode == 45 || wmoCode == 48 -> {
                if (isNight) intArrayOf(0xFF0D1A47.toInt(), 0xFF1A2A40.toInt(), 0xFF0D1A47.toInt())
                else intArrayOf(0xFF8E9EAE.toInt(), 0xFFAEBEC8.toInt(), 0xFFC8D8E5.toInt())
            }
            // Light drizzle (51) - light blue-gray
            wmoCode == 51 -> {
                if (isNight) intArrayOf(0xFF4A5A6D.toInt(), 0xFF3A4A5D.toInt(), 0xFF2A3A4D.toInt())
                else intArrayOf(0xFF557080.toInt(), 0xFF708090.toInt(), 0xFF8B9AB0.toInt())
            }
            // Drizzle (53, 55) - blue-gray
            wmoCode == 53 || wmoCode == 55 -> {
                if (isNight) intArrayOf(0xFF4A5A6D.toInt(), 0xFF3A4A5D.toInt(), 0xFF2A3A4D.toInt())
                else intArrayOf(0xFF5A7090.toInt(), 0xFF7A90A8.toInt(), 0xFF9ABAB8.toInt())
            }
            // Freezing drizzle (56, 57) - blue-gray
            wmoCode == 56 || wmoCode == 57 -> {
                if (isNight) intArrayOf(0xFF3A4A5D.toInt(), 0xFF2A3A50.toInt(), 0xFF1A2A40.toInt())
                else intArrayOf(0xFF5A7080.toInt(), 0xFF7A8590.toInt(), 0xFF9AA5B0.toInt())
            }
            // Light rain (61, 80) - blue-gray
            wmoCode == 61 || wmoCode == 80 -> {
                if (isNight) intArrayOf(0xFF4D5D70.toInt(), 0xFF3D4D60.toInt(), 0xFF2D3D50.toInt())
                else intArrayOf(0xFF455565.toInt(), 0xFF657585.toInt(), 0xFF8090A5.toInt())
            }
            // Moderate rain (63, 81) - darker blue-gray
            wmoCode == 63 || wmoCode == 81 -> {
                if (isNight) intArrayOf(0xFF3D4D60.toInt(), 0xFF2D3D50.toInt(), 0xFF1D2D40.toInt())
                else intArrayOf(0xFF3B4A57.toInt(), 0xFF5A6A77.toInt(), 0xFF757F8F.toInt())
            }
            // Heavy rain (65, 82) - very dark blue-black
            wmoCode == 65 || wmoCode == 82 -> {
                if (isNight) intArrayOf(0xFF0A1420.toInt(), 0xFF050F1A.toInt(), 0xFF000A10.toInt())
                else intArrayOf(0xFF0D1A27.toInt(), 0xFF1A2735.toInt(), 0xFF0D1520.toInt())
            }
            // Sleet (66, 67) - blue-gray
            wmoCode == 66 || wmoCode == 67 -> {
                if (isNight) intArrayOf(0xFF455565.toInt(), 0xFF354555.toInt(), 0xFF254555.toInt())
                else intArrayOf(0xFF546575.toInt(), 0xFF708090.toInt(), 0xFF8DAAB5.toInt())
            }
            // Light snow (71, 85) - light blue-white
            wmoCode == 71 || wmoCode == 85 -> {
                if (isNight) intArrayOf(0xFF708090.toInt(), 0xFF608080.toInt(), 0xFF507070.toInt())
                else intArrayOf(0xFFA0B3C5.toInt(), 0xFFBCC8D8.toInt(), 0xFFD0DDE8.toInt())
            }
            // Moderate snow (73, 77) - blue-white
            wmoCode == 73 || wmoCode == 77 -> {
                if (isNight) intArrayOf(0xFF708090.toInt(), 0xFF607080.toInt(), 0xFF507070.toInt())
                else intArrayOf(0xFFB0C0D0.toInt(), 0xFFC8D5E0.toInt(), 0xFFE0E8F0.toInt())
            }
            // Heavy snow (75, 86) - white-gray
            wmoCode == 75 || wmoCode == 86 -> {
                if (isNight) intArrayOf(0xFF708090.toInt(), 0xFF607070.toInt(), 0xFF506060.toInt())
                else intArrayOf(0xFFC0CDD8.toInt(), 0xFFD8E0E8.toInt(), 0xFFE8F0F5.toInt())
            }
            // Thunderstorm (95, 96, 99) - very dark purple
            wmoCode == 95 || wmoCode == 96 || wmoCode == 99 -> {
                if (isNight) intArrayOf(0xFF0D0D1A.toInt(), 0xFF050508.toInt(), 0xFF000000.toInt())
                else intArrayOf(0xFF1A1A3E.toInt(), 0xFF0D0D2E.toInt(), 0xFF050520.toInt())
            }
            // Default (fallback for unknown codes)
            else -> {
                if (isNight) intArrayOf(0xFF0D1A47.toInt(), 0xFF0D1A47.toInt(), 0xFF0D1A47.toInt())
                else intArrayOf(0xFF5A7FA8.toInt(), 0xFF7A95BB.toInt(), 0xFF8FA8C8.toInt())
            }
        }
    }

    fun getCardGradient(iconTag: String): IntArray {
        return when (iconTag) {
            "clear_dawn" -> intArrayOf(0xFF1A2555.toInt(), 0xFF2A3A6A.toInt(), 0xFF4A5A80.toInt())
            "clear_morning" -> intArrayOf(0xFF55A0E0.toInt(), 0xFF75BBE8.toInt(), 0xFF95D5F0.toInt())
            "clear_afternoon" -> intArrayOf(0xFF5A8ACC.toInt(), 0xFF7AA0D0.toInt(), 0xFFA0B8D0.toInt())
            "clear_sunset" -> intArrayOf(0xFFC06040.toInt(), 0xFFD08060.toInt(), 0xFF806090.toInt())
            "clear_evening" -> intArrayOf(0xFF152050.toInt(), 0xFF1A2A5A.toInt(), 0xFF0D1A47.toInt())
            "mainly_clear_morning" -> intArrayOf(0xFF5095D0.toInt(), 0xFF70ADD8.toInt(), 0xFF85C0E0.toInt())
            "mainly_clear_sunset" -> intArrayOf(0xFFA07050.toInt(), 0xFFB08868.toInt(), 0xFF7A7090.toInt())
            "partly_cloudy_morning" -> intArrayOf(0xFF5580A8.toInt(), 0xFF7098B8.toInt(), 0xFF88AAC5.toInt())
            "partly_cloudy_sunset" -> intArrayOf(0xFF8A7080.toInt(), 0xFF9A8090.toInt(), 0xFF7A7595.toInt())
            "overcast_morning" -> intArrayOf(0xFF687888.toInt(), 0xFF8090A0.toInt(), 0xFF98ABB8.toInt())
            "overcast_sunset" -> intArrayOf(0xFF706870.toInt(), 0xFF887880.toInt(), 0xFF7A7585.toInt())
            "hail_d" -> intArrayOf(0xFF2A2A3E.toInt(), 0xFF1A1A2E.toInt(), 0xFF0D0D1A.toInt())
            "hail_n" -> intArrayOf(0xFF1A1A2E.toInt(), 0xFF0D0D1A.toInt(), 0xFF050508.toInt())
            "clear_d" -> intArrayOf(0xFF4A90D9.toInt(), 0xFF6BAFE0.toInt(), 0xFF87CEEB.toInt())
            "clear_n" -> intArrayOf(0xFF0D1A47.toInt(), 0xFF0D1A47.toInt(), 0xFF0D1A47.toInt())
            "mainly_clear_d" -> intArrayOf(0xFF4A8BC8.toInt(), 0xFF6AA8D8.toInt(), 0xFF7EBCE5.toInt())
            "mainly_clear_n" -> intArrayOf(0xFF0F1E4A.toInt(), 0xFF0F1E4A.toInt(), 0xFF0F1E4A.toInt())
            "partly_cloudy_d" -> intArrayOf(0xFF5A7FA8.toInt(), 0xFF7A95BB.toInt(), 0xFF8FA8C8.toInt())
            "partly_cloudy_n" -> intArrayOf(0xFF0D1A47.toInt(), 0xFF1A2555.toInt(), 0xFF0D1A47.toInt())
            "overcast_d" -> intArrayOf(0xFF5F6E7F.toInt(), 0xFF7A8A9B.toInt(), 0xFF8FA3B8.toInt())
            "overcast_n" -> intArrayOf(0xFF1A2535.toInt(), 0xFF1A2535.toInt(), 0xFF0F1A2B.toInt())
            "fog_d" -> intArrayOf(0xFF7E8E9E.toInt(), 0xFF9AABBB.toInt(), 0xFFB5C5D5.toInt())
            "fog_n" -> intArrayOf(0xFF0D1A47.toInt(), 0xFF1A2A40.toInt(), 0xFF0D1A47.toInt())
            "light_drizzle_d" -> intArrayOf(0xFF456070.toInt(), 0xFF607080.toInt(), 0xFF708090.toInt())
            "light_drizzle_n" -> intArrayOf(0xFF4A5A6D.toInt(), 0xFF3A4A5D.toInt(), 0xFF2A3A4D.toInt())
            "drizzle_d" -> intArrayOf(0xFF4A6080.toInt(), 0xFF607090.toInt(), 0xFF7080A0.toInt())
            "drizzle_n" -> intArrayOf(0xFF4A5A6D.toInt(), 0xFF3A4A5D.toInt(), 0xFF2A3A4D.toInt())
            "freezing_drizzle_d" -> intArrayOf(0xFF4A6070.toInt(), 0xFF6A7580.toInt(), 0xFF7A8590.toInt())
            "freezing_drizzle_n" -> intArrayOf(0xFF3A4A5D.toInt(), 0xFF2A3A50.toInt(), 0xFF1A2A40.toInt())
            "light_rain_d" -> intArrayOf(0xFF354555.toInt(), 0xFF556575.toInt(), 0xFF708090.toInt())
            "light_rain_n" -> intArrayOf(0xFF4D5D70.toInt(), 0xFF3D4D60.toInt(), 0xFF2D3D50.toInt())
            "rain_d" -> intArrayOf(0xFF2B3A47.toInt(), 0xFF4A5A67.toInt(), 0xFF656F7F.toInt())
            "rain_n" -> intArrayOf(0xFF3D4D60.toInt(), 0xFF2D3D50.toInt(), 0xFF1D2D40.toInt())
            "heavy_rain_d" -> intArrayOf(0xFF1E2B38.toInt(), 0xFF2A3845.toInt(), 0xFF151F2B.toInt())
            "heavy_rain_n" -> intArrayOf(0xFF1A2A3D.toInt(), 0xFF0D1A2D.toInt(), 0xFF050F1A.toInt())
            "sleet_d" -> intArrayOf(0xFF445565.toInt(), 0xFF607080.toInt(), 0xFF758090.toInt())
            "sleet_n" -> intArrayOf(0xFF455565.toInt(), 0xFF354555.toInt(), 0xFF254555.toInt())
            "light_snow_d" -> intArrayOf(0xFF90A3B5.toInt(), 0xFFA8B8C8.toInt(), 0xFFC0CDD8.toInt())
            "light_snow_n" -> intArrayOf(0xFF708090.toInt(), 0xFF608080.toInt(), 0xFF507070.toInt())
            "snow_d" -> intArrayOf(0xFF9EB0C0.toInt(), 0xFFB0C0D0.toInt(), 0xFFC5D5E5.toInt())
            "snow_n" -> intArrayOf(0xFF708090.toInt(), 0xFF607080.toInt(), 0xFF507070.toInt())
            "heavy_snow_d" -> intArrayOf(0xFF9EAFC0.toInt(), 0xFFB8C8D8.toInt(), 0xFFCDD5E0.toInt())
            "heavy_snow_n" -> intArrayOf(0xFF708090.toInt(), 0xFF607070.toInt(), 0xFF506060.toInt())
            "thunderstorm_d" -> intArrayOf(0xFF2A2A3E.toInt(), 0xFF1A1A2E.toInt(), 0xFF0D0D1A.toInt())
            "thunderstorm_n" -> intArrayOf(0xFF1A1A2E.toInt(), 0xFF0D0D1A.toInt(), 0xFF050508.toInt())
            else -> intArrayOf(0xFF5A7FA8.toInt(), 0xFF7A95BB.toInt(), 0xFF8FA8C8.toInt())
        }
    }

    fun getBackgroundGradient(iconTag: String): IntArray {
        return when (iconTag) {
            "clear_dawn" -> intArrayOf(0xFF1A2050.toInt(), 0xFF2A3570.toInt(), 0xFF5A5585.toInt())
            "clear_morning" -> intArrayOf(0xFF45A0E5.toInt(), 0xFF65C0F0.toInt(), 0xFF90E0F8.toInt())
            "clear_afternoon" -> intArrayOf(0xFF4580CC.toInt(), 0xFF6598D5.toInt(), 0xFF90B0D5.toInt())
            "clear_sunset" -> intArrayOf(0xFFD06838.toInt(), 0xFFE08858.toInt(), 0xFF906090.toInt())
            "clear_evening" -> intArrayOf(0xFF101845.toInt(), 0xFF152250.toInt(), 0xFF0D1A47.toInt())
            "mainly_clear_morning" -> intArrayOf(0xFF4090D5.toInt(), 0xFF60B0E0.toInt(), 0xFF80C8E8.toInt())
            "mainly_clear_sunset" -> intArrayOf(0xFFB07848.toInt(), 0xFFC09060.toInt(), 0xFF8A7898.toInt())
            "partly_cloudy_morning" -> intArrayOf(0xFF5088B5.toInt(), 0xFF6AA0C8.toInt(), 0xFF88B8D8.toInt())
            "partly_cloudy_sunset" -> intArrayOf(0xFF987888.toInt(), 0xFFA88898.toInt(), 0xFF8880A0.toInt())
            "overcast_morning" -> intArrayOf(0xFF708898.toInt(), 0xFF8FA0B0.toInt(), 0xFFA5B5C5.toInt())
            "overcast_sunset" -> intArrayOf(0xFF787078.toInt(), 0xFF908088.toInt(), 0xFF858090.toInt())
            "hail_d" -> intArrayOf(0xFF1A1A3E.toInt(), 0xFF0D0D2E.toInt(), 0xFF050520.toInt())
            "hail_n" -> intArrayOf(0xFF0D0D1A.toInt(), 0xFF050508.toInt(), 0xFF000000.toInt())
            else -> getBackgroundGradient(iconTagToWmoCode(iconTag), iconTag.endsWith("_n"))
        }
    }

    private fun iconTagToWmoCode(icon: String): Int {
        val base = icon.removeSuffix("_d").removeSuffix("_n")
        return when (base) {
            "clear" -> 0
            "mainly_clear" -> 1
            "partly_cloudy" -> 2
            "overcast" -> 3
            "fog" -> 45
            "light_drizzle" -> 51
            "drizzle" -> 53
            "freezing_drizzle" -> 56
            "light_rain" -> 61
            "rain" -> 63
            "heavy_rain" -> 65
            "sleet" -> 66
            "light_snow" -> 71
            "snow" -> 73
            "heavy_snow" -> 75
            "thunderstorm" -> 95
            "hail" -> 99
            else -> 2
        }
    }

    private fun hexToColorInt(hex: String): Int {
        return ("FF" + hex.removePrefix("#")).toLong(16).toInt()
    }
}
