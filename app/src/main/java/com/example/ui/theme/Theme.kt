package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = CyberGreen,
  onPrimary = MilitaryBlack,
  primaryContainer = TacticalOlive,
  onPrimaryContainer = CyberGreen,
  secondary = CardBackground,
  onSecondary = BrightText,
  tertiary = WarningAmber,
  onTertiary = MilitaryBlack,
  background = MilitaryBlack,
  onBackground = BrightText,
  surface = GunmetalGray,
  onSurface = BrightText,
  surfaceVariant = CardBackground,
  onSurfaceVariant = MutedText,
  outline = BorderGreen,
  error = AlertRed,
  onError = MilitaryBlack
)

// We want to force dark theme for the tactical cyberpunk immersive experience.
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark-mode for military HUD
  dynamicColor: Boolean = false, // Disable default dynamic colors to preserve our crafted cyberpunk styling
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
