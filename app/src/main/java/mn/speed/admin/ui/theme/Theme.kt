@file:Suppress("unused")

package mn.speed.admin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val GamingDarkBackground = Color(0xFF0D1117)
val GamingSurface = Color(0xFF161B22)
val GamingBlueAccent = Color(0xFF1F6FEB)
val GamingBlueGlow = Color(0xFF388BFD)
val TextWhite = Color(0xFFF0F6FC)
val TextGray = Color(0xFF8B949E)
val ErrorRed = Color(0xFFDA3633)
val SuccessGreen = Color(0xFF238636)

private val DarkColorScheme = darkColorScheme(
    primary = GamingBlueAccent,
    secondary = GamingBlueGlow,
    background = GamingDarkBackground,
    surface = GamingSurface,
    onPrimary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
    error = ErrorRed
)

@Composable
fun SpeedMNAdminTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    primaryColor: Color = GamingBlueAccent,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = primaryColor,
        secondary = primaryColor.copy(alpha = 0.7f),
        background = GamingDarkBackground,
        surface = GamingSurface,
        onPrimary = TextWhite,
        onBackground = TextWhite,
        onSurface = TextWhite,
        error = ErrorRed
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}