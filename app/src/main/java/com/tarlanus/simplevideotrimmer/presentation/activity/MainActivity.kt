package com.tarlanus.simplevideotrimmer.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.tarlanus.simplevideotrimmer.presentation.Main.screen.MainScreen
import com.tarlanus.simplevideotrimmer.presentation.OutPut.screen.OutputScreen
import com.tarlanus.simplevideotrimmer.presentation.Screens.ScreenMain
import com.tarlanus.simplevideotrimmer.presentation.Screens.ScreenOutput
import com.tarlanus.simplevideotrimmer.presentation.Screens.ScreenTrim
import com.tarlanus.simplevideotrimmer.presentation.Trim.screen.TrimScreen
import com.tarlanus.simplevideotrimmer.ui.theme.AccentYellow
import com.tarlanus.simplevideotrimmer.ui.theme.SimpleVideoTrimmerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleVideoTrimmerTheme {
                Scaffold { paddingValues ->

                    val navBackStack = rememberNavBackStack(ScreenMain)


                    NavDisplay(
                        modifier = Modifier.Companion
                            .fillMaxSize()
                            .background(AccentYellow)
                            .padding(paddingValues),
                        backStack = navBackStack,
                        onBack = {
                            navBackStack.subList(1, navBackStack.size).clear()
                        },
                        entryProvider = entryProvider {
                            entry<ScreenMain> {
                                MainScreen(onClick = { key1, key2 ->
                                    navBackStack.add(ScreenTrim(key1, key2))
                                })
                            }
                            entry<ScreenTrim> { key ->
                                TrimScreen(onClick = {
                                    navBackStack.add(ScreenOutput(it))
                                }, inputPath = key.inputPath, key.details)
                            }
                            entry<ScreenOutput> { key ->
                                OutputScreen(onClick = {
                                    navBackStack.subList(1, navBackStack.size).clear()
                                }, key.outPath)


                            }
                        }
                    )


                }
            }
        }
    }
}

