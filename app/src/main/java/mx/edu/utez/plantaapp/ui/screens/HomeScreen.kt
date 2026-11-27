package mx.edu.utez.plantaapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.edu.utez.plantaapp.data.remote.dto.UsuarioDto
import mx.edu.utez.plantaapp.ui.components.PlantaCard
import mx.edu.utez.plantaapp.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    usuario: UsuarioDto,
    homeViewModel: HomeViewModel,
    onPlantaClick: (Long) -> Unit,
    onEscanearClick: () -> Unit,
    onLogoutClick: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(usuario.id) {
        usuario.id?.let { homeViewModel.cargarPlantas(it) }
    }

    LaunchedEffect(uiState.mensajeExito, uiState.error) {
        val msg = uiState.mensajeExito ?: uiState.error
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            homeViewModel.limpiarMensajes()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mi Jardín", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Hola, ${usuario.fullName}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onEscanearClick,
                icon = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                text = { Text("Escanear Planta") }
            )
        },
        bottomBar = bottomBar,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.plantas.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Aún no tienes plantas registradas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Toca \"Escanear Planta\" para tomarle una foto y conocer sus cuidados.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.plantas, key = { it.id ?: it.hashCode() }) { planta ->
                            PlantaCard(
                                planta = planta,
                                onClick = { planta.id?.let { onPlantaClick(it) } },
                                onRegarClick = {
                                    val pId = planta.id
                                    val uId = usuario.id
                                    if (pId != null && uId != null) {
                                        homeViewModel.regarPlantaRapido(pId, uId)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}