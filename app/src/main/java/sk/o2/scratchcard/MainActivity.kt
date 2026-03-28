package sk.o2.scratchcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import sk.o2.scratchcard.ui.navigation.ScratchCardNavHost
import sk.o2.scratchcard.ui.theme.ScratchCardTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScratchCardTheme {
                val navController = rememberNavController()
                ScratchCardNavHost(navController = navController)
            }
        }
    }
}
