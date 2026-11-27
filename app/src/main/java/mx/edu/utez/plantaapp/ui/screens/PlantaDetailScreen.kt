package mx.edu.utez.plantaapp.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.edu.utez.plantaapp.ui.viewmodels.PlantaDetailViewModel
import mx.edu.utez.plantaapp.util.formatearFechaRiego

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantaDetailScreen(
    usuarioId: Long,
    plantaId: Long,
    detailViewModel: PlantaDetailViewModel,
    onBack: () -> Unit
) {
    val uiState by detailViewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(plantaId) {
        detailViewModel.cargarDetalle(usuarioId, plantaId)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar planta") },
            text = { Text("¿Seguro que quieres eliminar esta planta de tu jardín?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        detailViewModel.eliminarPlanta(plantaId, onBack)
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    val plantaActual = uiState.planta
    if (showEditDialog && plantaActual != null) {
        var nuevoNombre by remember { mutableStateOf(plantaActual.nombreComun) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar nombre") },
            text = {
                OutlinedTextField(
                    value = nuevoNombre,
                    onValueChange = { nuevoNombre = it },
                    label = { Text("Nombre de la planta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val limpio = nuevoNombre.trim()
                        if (limpio.isNotEmpty()) {
                            val actualizada = plantaActual.copy(nombreComun = limpio)
                            showEditDialog = false
                            detailViewModel.actualizarPlanta(actualizada)
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.planta?.nombreComun ?: "Detalle de Planta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (uiState.planta != null) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editar nombre",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val planta = uiState.planta
            if (uiState.isLoading && planta == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (planta != null) {
                val bitmap = remember(planta.imagenBase64) {
                    try {
                        planta.imagenBase64?.let { b64 ->
                            val cleanB64 = b64.substringAfter(",")
                            val bytes = Base64.decode(cleanB64, Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                        }
                    } catch (_: Exception) {
                        null
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = planta.nombreComun,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = planta.nombreComun,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar nombre",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (!planta.nombreCientifico.isNullOrBlank()) {
                        Text(
                            text = planta.nombreCientifico,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { detailViewModel.regarPlanta(plantaId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Registrar Riego Hoy")
                    }

                    if (uiState.mensaje != null) {
                        Text(
                            text = uiState.mensaje!!,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (uiState.error != null) {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    InfoCard("Calendario de Riego", buildString {
                        append("Frecuencia: ${planta.frecuenciaRiegoTexto ?: "Cada ${planta.frecuenciaRiegoDias} días"}\n")
                        append("Cantidad de agua: ${planta.cantidadAgua ?: "Moderada"}\n")
                        append("Último riego: ${planta.ultimoRiego?.formatearFechaRiego() ?: "Hoy"}\n")
                        append("Próximo riego: ${planta.proximoRiego?.formatearFechaRiego() ?: "Pendiente"}")
                    })

                    if (!planta.descripcion.isNullOrBlank()) {
                        InfoCard("Descripción", planta.descripcion)
                    }

                    InfoCard("Condiciones Ideales", buildString {
                        append("Luz: ${planta.necesidadLuz ?: "-"} (${planta.tipoIluminacion ?: "-"})\n")
                        append("Temperatura: ${planta.temperatura ?: "-"}\n")
                        append("Humedad: ${planta.humedad ?: "-"}\n")
                        append("Suelo: ${planta.tipoSuelo ?: "-"}")
                    })

                    if (!planta.recomendacionesGenerales.isNullOrBlank()) {
                        InfoCard("Recomendaciones", planta.recomendacionesGenerales)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(titulo: String, contenido: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = contenido,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}