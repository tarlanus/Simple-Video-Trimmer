package com.tarlanus.simplevideotrimmer.presentation.activity

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.tarlanus.simplevideotrimmer.presentation.Main.screen.MainScreen
import com.tarlanus.simplevideotrimmer.presentation.OutPut.screen.OutputScreen
import com.tarlanus.simplevideotrimmer.presentation.Screens.ScreenMain
import com.tarlanus.simplevideotrimmer.presentation.Screens.ScreenOutput
import com.tarlanus.simplevideotrimmer.presentation.Screens.ScreenTrim
import com.tarlanus.simplevideotrimmer.presentation.Trim.screen.TrimScreen
import com.tarlanus.simplevideotrimmer.ui.theme.SimpleVideoTrimmerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transparentBars = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = transparentBars, navigationBarStyle = transparentBars)
        window.isNavigationBarContrastEnforced = false
        window.isStatusBarContrastEnforced = false
        val preferences = getSharedPreferences("appearance", MODE_PRIVATE)
        setContent {
            var preferredDark by remember {
                mutableStateOf(if (preferences.contains("dark")) preferences.getBoolean("dark", false) else null)
            }
            val dark = preferredDark ?: isSystemInDarkTheme()
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !dark
                    isAppearanceLightNavigationBars = !dark
                }
            }
            SimpleVideoTrimmerTheme(darkTheme = dark) {
                val navBackStack = rememberNavBackStack(ScreenMain)
                val backToInput: () -> Unit = {
                    navBackStack.subList(1, navBackStack.size).clear()
                }
                NavDisplay(
                    modifier = Modifier.fillMaxSize(),
                    backStack = navBackStack,
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    onBack = backToInput,
                    entryProvider = entryProvider {
                        entry<ScreenMain> {
                            MainScreen(dark = dark, onToggleTheme = {
                                preferredDark = !dark
                                preferences.edit().putBoolean("dark", !dark).apply()
                            }, onClick = { key1, key2 ->
                                navBackStack.add(ScreenTrim(key1, key2))
                            })
                        }
                        entry<ScreenTrim> { key ->
                            TrimScreen(onClick = {
                                navBackStack.add(ScreenOutput(it))
                            }, inputPath = key.inputPath, videoDetails = key.details, onBack = backToInput)
                        }
                        entry<ScreenOutput> { key ->
                            OutputScreen(onClick = backToInput, outputPath = key.outPath)
                        }
                    }
                )
            }
        }
    }
}

