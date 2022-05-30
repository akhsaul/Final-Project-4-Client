package kelompok.tiga.app

sealed class Screen(val route: String) {
    object Home : Screen("home/{category}"){
        fun createRoute(category: String) = "home/$category"
    }
    object Player : Screen("player/{id}"){
        fun createRoute(id: Int) = "player/$id"
    }
    object Info : Screen("info")
    object Report : Screen("report")
}

