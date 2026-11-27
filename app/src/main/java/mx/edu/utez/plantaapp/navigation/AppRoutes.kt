package mx.edu.utez.plantaapp.navigation

sealed class AppRoutes(val route: String) {
    object Login : AppRoutes("login")
    object Register : AppRoutes("register")
    object Home : AppRoutes("home")
    object Escanear : AppRoutes("escanear")
    object Riegos : AppRoutes("riegos")
    object PlantaDetail : AppRoutes("planta_detail/{plantaId}") {
        fun createRoute(plantaId: Long) = "planta_detail/$plantaId"
    }
}