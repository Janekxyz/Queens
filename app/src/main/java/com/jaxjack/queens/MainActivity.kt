package com.jaxjack.queens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.jaxjack.queens.features.queengame.configuration.GameConfigurationScreen
import com.jaxjack.queens.features.queengame.game.QueenGameScreen
import com.jaxjack.queens.styleguide.theme.QueensTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QueensTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameConfigurationScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
