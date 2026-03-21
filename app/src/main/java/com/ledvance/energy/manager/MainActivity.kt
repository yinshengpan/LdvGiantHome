package com.ledvance.energy.manager

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.tracing.trace
import com.ledvance.energy.manager.model.DarkThemeMode
import com.ledvance.energy.manager.state.LedvanceApp
import com.ledvance.energy.manager.utils.DataStoreKeys
import com.ledvance.ui.theme.LedvanceTheme
import com.ledvance.utils.extensions.getInt
import com.ledvance.utils.extensions.isSystemInDarkTheme
import com.ledvance.utils.extensions.setStatusBarsIcons
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        var darkTheme by mutableStateOf(false)
        // var darkTheme by mutableStateOf(resources.configuration.isSystemInDarkTheme)
        initDarkTheme(onDarkThemeChanged = { isDarkTheme ->
            darkTheme = isDarkTheme
        })
        setContent {
            LedvanceTheme(darkTheme = darkTheme) {
                LedvanceApp()
            }
        }
        observeAppLifecycle()
    }

    private fun observeAppLifecycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                Timber.tag(TAG).i("App joined foregroun")
            }

            override fun onStop(owner: LifecycleOwner) {
                Timber.tag(TAG).i("App went to background")
            }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Timber.tag(TAG).i("onConfigurationChanged: $newConfig")
    }

    private fun initDarkTheme(onDarkThemeChanged: (Boolean) -> Unit) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    isSystemInDarkTheme(),
                    DataStoreKeys.darkThemeMode.getInt()
                ) { isSystemInDarkTheme, darkThemeMode ->
                    Timber.i("isSystemInDarkTheme:$isSystemInDarkTheme,darkThemeMode:$darkThemeMode")
                    return@combine when (DarkThemeMode.valueOf(darkThemeMode)) {
                        DarkThemeMode.Dark -> true
                        DarkThemeMode.FollowSystem -> isSystemInDarkTheme
                        DarkThemeMode.Light -> false
                    }
                }
                    .distinctUntilChanged()
                    .onEach { onDarkThemeChanged.invoke(it) }
                    .collectLatest { darkTheme ->
                        trace("LedvanceEdgeToEdge") {
                            enableEdgeToEdge(
                                statusBarStyle = SystemBarStyle.auto(
                                    lightScrim = Color.TRANSPARENT,
                                    darkScrim = Color.TRANSPARENT
                                ) { darkTheme },
                                navigationBarStyle = SystemBarStyle.auto(
                                    lightScrim = lightScrim,
                                    darkScrim = darkScrim
                                ) { darkTheme })
                            setStatusBarsIcons(darkIcons = !darkTheme)
                        }
                    }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"

        /**
         * The default light scrim, as defined by androidx and the platform:
         * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
         */
        private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

        /**
         * The default dark scrim, as defined by androidx and the platform:
         * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
         */
        private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
    }
}

