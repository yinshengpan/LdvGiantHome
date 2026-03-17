package com.ledvance.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import com.ledvance.ui.theme.colors.ColorScheme
import com.ledvance.ui.theme.colors.darkColorScheme
import com.ledvance.ui.theme.colors.lightColorScheme
import timber.log.Timber

internal val LocalColorScheme = staticCompositionLocalOf { lightColorScheme() }
internal val LocalIsAppInDarkTheme = staticCompositionLocalOf { false }
val LocalSnackBarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}
@Stable
object AppTheme {
    val colors: ColorScheme
        @Composable
        get() = LocalColorScheme.current

    val typography: Typography
        @Composable
        get() = LedvanceTypography

    val isAppInDarkTheme: Boolean
        @Composable
        get() = LocalIsAppInDarkTheme.current
}


@Composable
fun LedvanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    Timber.tag("TAG").i("LedvanceTheme: darkTheme:$darkTheme")
    val colorScheme = when {
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    // Background theme
    val defaultBackgroundTheme = BackgroundTheme(
        color = colorScheme.screenBackground,
        tonalElevation = 2.dp,
    )
    val snackBarHostState = remember { SnackbarHostState() }
    CompositionLocalProvider(
        LocalBackgroundTheme provides defaultBackgroundTheme,
        LocalColorScheme provides colorScheme,
        LocalIsAppInDarkTheme provides darkTheme,
        LocalSnackBarHostState provides snackBarHostState
    ) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = colorScheme.primary
            ),
            typography = AppTheme.typography,
            content = content,
        )
    }
}
