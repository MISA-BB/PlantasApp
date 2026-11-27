package mx.edu.utez.plantaapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.edu.utez.plantaapp.data.remote.dto.UsuarioDto
import mx.edu.utez.plantaapp.ui.viewmodels.RiegosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiegosScreen(
    usuario: UsuarioDto,
    riegosViewModel: RiegosViewModel,
    bottomBar: @Composable () -> Unit
) {
    val uiState by riegosViewModel.uiState.collectAsState()

    LaunchedEffect(usuario.id) {
        usuario.id?.let { riegosViewModel.cargarHistorial(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Riegos", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = bottomBar
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
                uiState.riegos.isEmpty() -> {
                    Text(
                        text = "Aún no tienes riegos registrados",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.riegos, key = { it.id ?: it.hashCode() }) { riego ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = Color(0xFF0288D1),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = riego.nombrePlanta,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Regada el: ${riego.fechaRiego ?: "Recientemente"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}