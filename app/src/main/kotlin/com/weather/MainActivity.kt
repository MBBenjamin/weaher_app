package com.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.weather.presentation.home.HomeScreen
import com.weather.presentation.theme.WeatherTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Única Activity do app — ponto de entrada da UI Compose.
 *
 * Edge-to-edge habilitado para uso correto dos insets em Scaffold MD3.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherTheme {
                HomeScreen()
            }
        }
    }
}
