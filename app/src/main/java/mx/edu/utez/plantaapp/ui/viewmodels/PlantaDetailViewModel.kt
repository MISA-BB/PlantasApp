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

data class PlantaDetailUiState(
    val planta: PlantaDto? = null,
    val isLoading: Boolean = false,
    val mensaje: String? = null,
    val error: String? = null
)

class PlantaDetailViewModel(
    private val plantaRepository: PlantaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlantaDetailUiState())
    val uiState: StateFlow<PlantaDetailUiState> = _uiState.asStateFlow()

    fun cargarDetalle(usuarioId: Long, plantaId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            plantaRepository.obtenerPlantas(usuarioId).fold(
                onSuccess = { lista ->
                    val encontrada = lista.find { it.id == plantaId }
                    _uiState.value = _uiState.value.copy(
                        planta = encontrada,
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

    fun regarPlanta(plantaId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            plantaRepository.regarPlanta(plantaId).fold(
                onSuccess = { actualizada ->
                    _uiState.value = _uiState.value.copy(
                        planta = actualizada,
                        isLoading = false,
                        mensaje = "¡Riego registrado con éxito! 💧"
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

    fun eliminarPlanta(plantaId: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            plantaRepository.eliminarPlanta(plantaId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onDeleted()
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

    companion object {
        fun provideFactory(plantaRepository: PlantaRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlantaDetailViewModel(plantaRepository) as T
                }
            }
    }
}