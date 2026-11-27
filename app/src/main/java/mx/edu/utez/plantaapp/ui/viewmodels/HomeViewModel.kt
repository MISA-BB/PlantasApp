package mx.edu.utez.plantaapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.plantaapp.data.remote.dto.PlantaDto
import mx.edu.utez.plantaapp.data.repository.PlantaRepository

data class HomeUiState(
    val plantas: List<PlantaDto> = emptyList(),
    val isLoading: Boolean = false,
    val isAnalyzing: Boolean = false,
    val error: String? = null,
    val mensajeExito: String? = null
)

class HomeViewModel(
    private val plantaRepository: PlantaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun cargarPlantas(usuarioId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            plantaRepository.obtenerPlantas(usuarioId).fold(
                onSuccess = { lista ->
                    _uiState.value = _uiState.value.copy(
                        plantas = lista,
                        isLoading = false
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun analizarYGuardarPlanta(
        usuarioId: Long,
        imagenBase64: String,
        onPlantaCreada: (PlantaDto) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnalyzing = true, error = null)
            plantaRepository.analizarPlanta(usuarioId, imagenBase64).fold(
                onSuccess = { nuevaPlanta ->
                    _uiState.value = _uiState.value.copy(
                        isAnalyzing = false,
                        plantas = listOf(nuevaPlanta) + _uiState.value.plantas,
                        mensajeExito = "¡Planta identificada: ${nuevaPlanta.nombreComun}!"
                    )
                    onPlantaCreada(nuevaPlanta)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isAnalyzing = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun regarPlantaRapido(plantaId: Long, usuarioId: Long) {
        viewModelScope.launch {
            plantaRepository.regarPlanta(plantaId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(mensajeExito = "¡Planta regada! 💧")
                    cargarPlantas(usuarioId)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
            )
        }
    }

    fun limpiarMensajes() {
        _uiState.value = _uiState.value.copy(error = null, mensajeExito = null)
    }

    companion object {
        fun provideFactory(plantaRepository: PlantaRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(plantaRepository) as T
                }
            }
    }
}