package com.amolieres.setlistync.app.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.afacad_bold
import setlistsync.composeapp.generated.resources.afacad_italic
import setlistsync.composeapp.generated.resources.afacad_medium
import setlistsync.composeapp.generated.resources.afacad_regular

@Composable
fun afacadFontFamily() = FontFamily(
    Font(Res.font.afacad_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(Res.font.afacad_italic,  weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(Res.font.afacad_medium,  weight = FontWeight.Medium, style = FontStyle.Normal),
    Font(Res.font.afacad_bold,    weight = FontWeight.Bold,   style = FontStyle.Normal),
)

@Composable
fun appTypography(): Typography {
    val afacad = afacadFontFamily()
    val baseline = Typography()
    return Typography(
        displayLarge    = baseline.displayLarge.copy(fontFamily = afacad),
        displayMedium   = baseline.displayMedium.copy(fontFamily = afacad),
        displaySmall    = baseline.displaySmall.copy(fontFamily = afacad),
        headlineLarge   = baseline.headlineLarge.copy(fontFamily = afacad),
        headlineMedium  = baseline.headlineMedium.copy(fontFamily = afacad),
        headlineSmall   = baseline.headlineSmall.copy(fontFamily = afacad),
        titleLarge      = baseline.titleLarge.copy(fontFamily = afacad),
        titleMedium     = baseline.titleMedium.copy(fontFamily = afacad),
        titleSmall      = baseline.titleSmall.copy(fontFamily = afacad),
        bodyLarge       = baseline.bodyLarge.copy(fontFamily = afacad),
        bodyMedium      = baseline.bodyMedium.copy(fontFamily = afacad),
        bodySmall       = baseline.bodySmall.copy(fontFamily = afacad),
        labelLarge      = baseline.labelLarge.copy(fontFamily = afacad),
        labelMedium     = baseline.labelMedium.copy(fontFamily = afacad),
        labelSmall      = baseline.labelSmall.copy(fontFamily = afacad),
    )
}
