package pl.veloryx.darkwave

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Ink = Color(0xFF090706)
val Panel = Color(0xFF17110D)
val PanelRaised = Color(0xFF211812)
val Bone = Color(0xFFEEE2C8)
val Muted = Color(0xFF968873)
val SignalRed = Color(0xFFD34731)
val SignalGold = Color(0xFFD7AC42)
val Line = Color(0xFF554534)
val Success = Color(0xFF65A975)

private val DarkwaveColors = darkColorScheme(
    primary = SignalGold,
    onPrimary = Ink,
    secondary = SignalRed,
    onSecondary = Bone,
    background = Ink,
    onBackground = Bone,
    surface = Panel,
    onSurface = Bone,
    surfaceVariant = PanelRaised,
    onSurfaceVariant = Muted,
    outline = Line,
    error = Color(0xFFFF6A55),
)

@Composable
fun DarkwaveTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkwaveColors, content = content)
}
