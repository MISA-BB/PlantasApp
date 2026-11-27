package mx.edu.utez.plantaapp.navigation

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import mx.edu.utez.plantaapp.AppContainer
import mx.edu.utez.plantaapp.data.remote.dto.UsuarioDto
import mx.edu.utez.plantaapp.ui.components.BottomNavBar
import mx.edu.utez.plantaapp.ui.screens.*
import mx.edu.utez.plantaapp.ui.viewmodels.*
import mx.edu.utez.plantaapp.workers.RiegoNotificationWorker
import java.util.concurrent.TimeUnit

@Composable
fun AppNavGraph(container: AppContainer) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.provideFactory(container.authRepository)
    )
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(container.plantaRepository)
    )
    val detailViewModel: PlantaDetailViewModel = viewModel(
        factory = PlantaDetailViewModel.provideFactory(container.plantaRepository)
    )
    val riegosViewModel: RiegosViewModel = viewModel(
        factory = RiegosViewModel.provideFactory(container.plantaRepository)
    )

    val persistedUser by authViewModel.currentUser.collectAsState()
    var activeUser by remember { mutableStateOf<UsuarioDto?>(null) }

    LaunchedEffect(persistedUser) {
        if (persistedUser != null) {
            activeUser = persistedUser
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(activeUser?.id) {
        activeUser?.id?.let { userId ->
            programarNotificacionesRiego(context, userId)
        }
    }

    val startDestination = remember {
        if (activeUser != null) AppRoutes.Home.route else AppRoutes.Login.route
    }

    val bottomBar: @Composable () -> Unit = {
        BottomNavBar(
            currentRoute = currentRoute,
            onNavigate = { route ->
                if (currentRoute != route) {
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppRoutes.Login.route) {
            LaunchedEffect(activeUser) {
                if (activeUser != null) {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Login.route) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { usuario ->
                    activeUser = usuario
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppRoutes.Register.route)
                }
            )
        }

        composable(AppRoutes.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = { usuario ->
                    activeUser = usuario
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.Home.route) {
            val user = activeUser
            if (user != null) {
                HomeScreen(
                    usuario = user,
                    homeViewModel = homeViewModel,
                    onPlantaClick = { plantaId ->
                        navController.navigate(AppRoutes.PlantaDetail.createRoute(plantaId))
                    },
                    onEscanearClick = {
                        navController.navigate(AppRoutes.Escanear.route)
                    },
                    onLogoutClick = {
                        authViewModel.logout {
                            activeUser = null
                            navController.navigate(AppRoutes.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    bottomBar = bottomBar
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        composable(AppRoutes.Escanear.route) {
            val user = activeUser
            if (user != null) {
                EscanearPlantaScreen(
                    usuario = user,
                    homeViewModel = homeViewModel,
                    onPlantaAnalizada = { nuevaPlanta ->
                        val pId = nuevaPlanta.id
                        if (pId != null) {
                            navController.navigate(AppRoutes.PlantaDetail.createRoute(pId)) {
                                popUpTo(AppRoutes.Home.route)
                            }
                        } else {
                            navController.navigate(AppRoutes.Home.route)
                        }
                    },
                    bottomBar = bottomBar
                )
            }
        }

        composable(AppRoutes.Riegos.route) {
            val user = activeUser
            if (user != null) {
                RiegosScreen(
                    usuario = user,
                    riegosViewModel = riegosViewModel,
                    bottomBar = bottomBar
                )
            }
        }

        composable(
            route = AppRoutes.PlantaDetail.route,
            arguments = listOf(navArgument("plantaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val plantaId = backStackEntry.arguments?.getLong("plantaId") ?: return@composable
            val userId = activeUser?.id ?: return@composable
            PlantaDetailScreen(
                usuarioId = userId,
                plantaId = plantaId,
                detailViewModel = detailViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private fun programarNotificacionesRiego(context: Context, usuarioId: Long) {
    val inputData = workDataOf("USUARIO_ID" to usuarioId)
    val workRequest = PeriodicWorkRequestBuilder<RiegoNotificationWorker>(12, TimeUnit.HOURS)
        .setInputData(inputData)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "riego_reminder_work",
        ExistingPeriodicWorkPolicy.UPDATE,
        workRequest
    )
}