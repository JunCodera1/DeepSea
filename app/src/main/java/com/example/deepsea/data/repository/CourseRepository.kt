package com.example.deepsea.repository

import android.graphics.Color
import android.util.Log
import com.example.deepsea.R
import com.example.deepsea.data.api.CourseApiService
import com.example.deepsea.data.dto.UserProgressDto
import com.example.deepsea.ui.components.SectionData
import com.example.deepsea.ui.components.UnitData
import com.example.deepsea.ui.theme.*
import retrofit2.Response
import java.io.IOException

class CourseRepository(private val courseApiService: CourseApiService) {
    val UnitColors = mapOf(
        "#Purple80" to Purple80,
        "#PurpleGrey80" to PurpleGrey80,
        "#Pink80" to Pink80,

        "#Purple40" to Purple40,
        "#PurpleGrey40" to PurpleGrey40,
        "#Pink40" to Pink40,

        "#Shadow11" to Shadow11,
        "#Shadow10" to Shadow10,
        "#Shadow9" to Shadow9,
        "#Shadow8" to Shadow8,
        "#Shadow7" to Shadow7,
        "#Shadow6" to Shadow6,
        "#Shadow5" to Shadow5,
        "#Shadow4" to Shadow4,
        "#Shadow3" to Shadow3,
        "#Shadow2" to Shadow2,
        "#Shadow1" to Shadow1,
        "#Shadow0" to Shadow0,

        "#Ocean11" to Ocean11,
        "#Ocean10" to Ocean10,
        "#Ocean9" to Ocean9,
        "#Ocean8" to Ocean8,
        "#Ocean7" to Ocean7,
        "#Ocean6" to Ocean6,
        "#Ocean5" to Ocean5,
        "#Ocean4" to Ocean4,
        "#Ocean3" to Ocean3,
        "#Ocean2" to Ocean2,
        "#Ocean1" to Ocean1,
        "#Ocean0" to Ocean0,

        "#Lavender11" to Lavender11,
        "#Lavender10" to Lavender10,
        "#Lavender9" to Lavender9,
        "#Lavender8" to Lavender8,
        "#Lavender7" to Lavender7,
        "#Lavender6" to Lavender6,
        "#Lavender5" to Lavender5,
        "#Lavender4" to Lavender4,
        "#Lavender3" to Lavender3,
        "#Lavender2" to Lavender2,
        "#Lavender1" to Lavender1,
        "#Lavender0" to Lavender0,

        "#Rose11" to Rose11,
        "#Rose10" to Rose10,
        "#Rose9" to Rose9,
        "#Rose8" to Rose8,
        "#Rose7" to Rose7,
        "#Rose6" to Rose6,
        "#Rose5" to Rose5,
        "#Rose4" to Rose4,
        "#Rose3" to Rose3,
        "#Rose2" to Rose2,
        "#Rose1" to Rose1,
        "#Rose0" to Rose0,

        "#Neutral8" to Neutral8,
        "#Neutral7" to Neutral7,
        "#Neutral6" to Neutral6,
        "#Neutral5" to Neutral5,
        "#Neutral4" to Neutral4,
        "#Neutral3" to Neutral3,
        "#Neutral2" to Neutral2,
        "#Neutral1" to Neutral1,
        "#Neutral0" to Neutral0,

        "#FunctionalRed" to FunctionalRed,
        "#FunctionalRedDark" to FunctionalRedDark,
        "#FunctionalGreen" to FunctionalGreen,
        "#FunctionalGrey" to FunctionalGrey,
        "#FunctionalDarkGrey" to FunctionalDarkGrey,

        "#Polar" to Polar,
        "#Swan" to Swan,
        "#Gallery" to Gallery,
        "#Hare" to Hare,

        "#Gray" to Gray,
        "#GrayDark" to GrayDark,

        "#FeatherGreenDark" to FeatherGreenDark,
        "#FeatherGreen" to FeatherGreen,
        "#MaskGreen" to MaskGreen,
        "#Beetle" to Beetle,

        "#Pink" to Pink,
        "#PinkDark" to PinkDark,

        "#Cyan" to Cyan,
        "#CyanDark" to CyanDark,

        "#Blue" to Blue,
        "#BlueDark" to BlueDark,

        "#YellowPrimary" to YellowPrimary,
        "#PurpleLight" to PurpleLight,
        "#Purple" to Purple,
        "#BlueLight" to BlueLight,
        "#BluePrimary" to BluePrimary,
        "#GreenLight" to GreenLight,
        "#Green" to Green
    )

    fun getUnitColor(colorName: String): androidx.compose.ui.graphics.Color {
        return UnitColors[colorName] ?: FunctionalGrey // fallback nếu không tìm thấy
    }

    // Helper function to safely make API calls
    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Log.e("API_ERROR", "Error: ${response.code()} - ${response.message()}")
                Result.failure(IOException("API call failed with error code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Exception during API call", e)
            Result.failure(e)
        }
    }

    suspend fun getAllSections(): Result<List<SectionData>> {
        return try {
            val sectionsResult = safeApiCall { courseApiService.getAllSections() }

            if (sectionsResult.isSuccess) {
                val sectionDtos = sectionsResult.getOrThrow()
                val sectionDataList = mutableListOf<SectionData>()

                for (sectionDto in sectionDtos) {
                    // Get units for each section
                    val unitsResult = safeApiCall {
                        courseApiService.getUnitsBySection(sectionDto.id)
                    }

                    if (unitsResult.isSuccess) {
                        val unitDtos = unitsResult.getOrThrow()
                        val unitDataList = unitDtos.map { unitDto ->
                            // Log the color values to assist with debugging
                            Log.d("COLOR_DEBUG", "Unit ${unitDto.title}: color=${unitDto.color}, darkerColor=${unitDto.darkerColor}")

                            UnitData(
                                title = unitDto.title,
                                description = unitDto.description,
                                // Using the image field directly from the DTO (with a default drawable if null)
                                image = unitDto.image ?: R.drawable.cut,
                                // Directly use the color and darkerColor fields from the DTO
                                color = convertHexToColor(unitDto.color),
                                darkerColor = convertHexToColor(unitDto.darkerColor)
                            )
                        }

                        sectionDataList.add(
                            SectionData(
                                title = sectionDto.title,
                                description = sectionDto.description,
                                level = sectionDto.level ?: "A1",  // Provide a default value if null
                                image = sectionDto.imageResId ?: R.drawable.cut,  // Provide default if null
                                color = convertHexToColor(sectionDto.color),
                                darkerColor = convertHexToColor(sectionDto.darkerColor),
                                units = unitDataList
                            )
                        )
                    } else {
                        // Handle failure for units
                        return Result.failure(unitsResult.exceptionOrNull() ?: IOException("Failed to fetch units"))
                    }
                }

                Result.success(sectionDataList)
            } else {
                // Handle failure for sections
                Result.failure(sectionsResult.exceptionOrNull() ?: IOException("Failed to fetch sections"))
            }
        } catch (e: Exception) {
            Log.e("REPO_ERROR", "Exception in getAllSections", e)
            Result.failure(e)
        }
    }

    suspend fun getUserProgress(userId: Long): Result<UserProgressDto> {
        return safeApiCall { courseApiService.getUserProgress(userId) }
    }

    // Improved utility function to convert hex string to Color
    private fun convertHexToColor(hexColor: String?): androidx.compose.ui.graphics.Color {
        // Early return if hexColor is null or blank
        if (hexColor.isNullOrBlank()) {
            Log.e("COLOR_CONVERT", "Hex color is null or blank")
            return FunctionalGrey
        }

        try {
            // Handle both formats: with or without # prefix
            val normalized = if (hexColor.startsWith("#")) hexColor else "#$hexColor"

            // Check if it's a valid hex color format
            if (!normalized.matches(Regex("^#[0-9a-fA-F]{6}(?:[0-9a-fA-F]{2})?$"))) {
                // If not a valid hex, check if it's a named color
                if (UnitColors.containsKey(normalized)) {
                    return UnitColors[normalized]!!
                }
                Log.e("COLOR_CONVERT", "Invalid hex color format: $hexColor")
                return FunctionalGrey
            }

            // Remove the # prefix and parse the hex value
            val colorStr = normalized.removePrefix("#")

            // Parse based on length (with or without alpha)
            return when (colorStr.length) {
                6 -> {
                    // RGB format
                    val colorInt = colorStr.toLong(16).toInt()
                    androidx.compose.ui.graphics.Color(colorInt or 0xFF000000.toInt())
                }
                8 -> {
                    // ARGB format
                    val colorLong = colorStr.toLong(16)
                    androidx.compose.ui.graphics.Color(
                        alpha = ((colorLong shr 24) and 0xFF) / 255f,
                        red = ((colorLong shr 16) and 0xFF) / 255f,
                        green = ((colorLong shr 8) and 0xFF) / 255f,
                        blue = (colorLong and 0xFF) / 255f
                    )
                }
                else -> {
                    Log.e("COLOR_CONVERT", "Unexpected color format length: $colorStr")
                    FunctionalGrey
                }
            }
        } catch (e: Exception) {
            Log.e("COLOR_CONVERT", "Failed to convert hex color: $hexColor", e)
            return FunctionalGrey
        }
    }

    private fun resolveColor(input: String?): androidx.compose.ui.graphics.Color {
        return convertHexToColor(input)
    }

    // Function to provide fallback data in case API fails
    fun getFallbackSections(): List<List<UnitData>> {
        return listOf(
            // Section 1: Beginner
            listOf(
                UnitData(title = "Unit 1", color = FeatherGreen, description = "Introduce yourself", image = R.drawable.ic_self_intro),
                UnitData(title = "Unit 2", color = Shadow3, description = "Greet others", image = R.drawable.ic_greeting),
                UnitData(title = "Unit 3", color = PinkDark, description = "Say goodbye", image = R.drawable.ic_goodbye),
                UnitData(title = "Unit 4", color = FunctionalRedDark, darkerColor = Pink40, description = "Talk about where you're from", image = R.drawable.ic_world_map),
                UnitData(title = "Unit 5", color = Cyan, description = "Exchange contact info", image = R.drawable.ic_contact_info),
            ),

            // Section 2: Explorer
            listOf(
                UnitData(title = "Unit 6", color = CyanDark, description = "Talk about daily routines", image = R.drawable.ic_routine),
                UnitData(title = "Unit 7", color = Shadow3, description = "Describe people", image = R.drawable.ic_people),
                UnitData(title = "Unit 8", color = Rose3, description = "Talk about your home", image = R.drawable.ic_home),
                UnitData(title = "Unit 9", color = FeatherGreen, description = "Talk about your family", image = R.drawable.ic_family),
                UnitData(title = "Unit 10", color = Pink40, description = "Talk about weather", image = R.drawable.ic_weather),
            ),

            // Section 3: Traveler
            listOf(
                UnitData(title = "Unit 11", color = Blue, description = "Order food", image = R.drawable.ic_food),
                UnitData(title = "Unit 12", color = Pink40, description = "Order drinks", image = R.drawable.ic_drinks),
                UnitData(title = "Unit 13", color = BlueDark, description = "Go shopping", image = R.drawable.ic_shopping_cart),
                UnitData(title = "Unit 14", color = Rose3, description = "Ask about prices", image = R.drawable.ic_price_tag),
                UnitData(title = "Unit 15", color = FunctionalRedDark, description = "Talk about preferences", image = R.drawable.ic_preferences),
            )
        )
    }

    fun getFallbackSectionData(): List<SectionData> {
        val fallbackUnits = getFallbackSections()

        return listOf(
            SectionData(
                title = "Section 1: Rookie",
                color = FeatherGreen,
                darkerColor = FeatherGreenDark,
                description = "Learn how to introduce yourself and greet others.",
                image = R.drawable.ic_handshake,
                level = "N5",
                units = fallbackUnits[0]
            ),
            SectionData(
                title = "Section 2: Explorer",
                color = Cyan,
                darkerColor = CyanDark,
                description = "Talk about your daily routines and habits.",
                image = R.drawable.ic_calendar,
                level = "N5",
                units = fallbackUnits[1]
            ),
            SectionData(
                title = "Section 3: Traveler",
                color = Blue,
                darkerColor = BlueDark,
                description = "Learn useful phrases for shopping situations.",
                image = R.drawable.ic_shopping_cart,
                level = "N4",
                units = fallbackUnits[2]
            )
        )
    }
}