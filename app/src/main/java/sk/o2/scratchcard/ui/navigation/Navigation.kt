package sk.o2.scratchcard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import sk.o2.scratchcard.ui.activation.ActivationScreen
import sk.o2.scratchcard.ui.main.MainScreen
import sk.o2.scratchcard.ui.scratch.ScratchScreen

object Routes {
    const val MAIN = "main"
    const val SCRATCH = "scratch"
    const val ACTIVATION = "activation"
}

@Composable
fun ScratchCardNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.MAIN) {
        composable(Routes.MAIN) {
            MainScreen(
                onNavigateToScratch = { navController.navigate(Routes.SCRATCH) },
                onNavigateToActivation = { navController.navigate(Routes.ACTIVATION) }
            )
        }
        composable(Routes.SCRATCH) {
            ScratchScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.ACTIVATION) {
            ActivationScreen(onBack = { navController.popBackStack() })
        }
    }
}
