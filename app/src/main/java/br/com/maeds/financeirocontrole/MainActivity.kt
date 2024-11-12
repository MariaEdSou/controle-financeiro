package br.com.maeds.financeirocontrole

import MyFinanceAppTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFinanceAppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    HomeScreen() // Carrega a tela inicial
                }

            }
        }
    }
}

