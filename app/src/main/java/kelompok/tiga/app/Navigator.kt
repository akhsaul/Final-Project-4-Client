package kelompok.tiga.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun Navigator(navController: NavHostController){
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            Home(navController)
        }
        composable(Screen.Player.route) { entry ->
            val id = entry.arguments?.getString("id")
            requireNotNull(id)
            Player(navController, id.toInt())
        }
    }
}