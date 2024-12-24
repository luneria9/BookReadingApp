package com.example.bookreadingapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.bookreadingapp.R

//referenced from https://developer.android.com/codelabs/basic-android-kotlin-compose-material-theming#5
val BonaNona = FontFamily(
    Font(R.font.bonanova_regular),
    Font(R.font.bonanova_bold, FontWeight.Bold)
)

val MerriWeather = FontFamily(
    Font(R.font.merriweather_regular),
    Font(R.font.merriweather_bold, FontWeight.Bold)
)

val NanumMyeongjo = FontFamily(
    Font(R.font.nanummyeongjo_regular),
    Font(R.font.nanummyeongjo_bold, FontWeight.Bold)
)

//referenced from https://gitlab.com/crdavis/adaptivenavigationegcode/-/tree/master?ref_type=heads
// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = MerriWeather,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    displayMedium = TextStyle(
        fontFamily = MerriWeather,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    displaySmall = TextStyle(
        fontFamily = MerriWeather,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = BonaNona,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily = BonaNona,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = BonaNona,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = NanumMyeongjo,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = NanumMyeongjo,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = NanumMyeongjo,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp
    )
)
