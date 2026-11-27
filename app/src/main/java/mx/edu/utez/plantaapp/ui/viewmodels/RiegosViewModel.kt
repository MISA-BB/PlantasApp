package mx.edu.utez.plantaapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.plantaapp.data.remote.dto.RiegoDto
import mx.edu.utez.plantaapp.data.repository.PlantaRepository

data class RiegosUiState(
    val riegos: List<RiegoDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class RiegosViewModel(
    private val plantaRepository: PlantaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RiegosUiState())
    val uiState: StateFlow<RiegosUiState> = _uiState.asStateFlow()

    fun cargarHistorial(usuarioId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            plantaRepository.obtenerHistorialRiegos(usuarioId).fold(
                onSuccess = { lista ->
                    _uiState.value = RiegosUiState(riegos = lista, isLoading = false)
                },
                onFailure = { e ->
                    _uiState.value = RiegosUiState(isLoading = false, error = e.message)
                }
            )
        }
    }

    companion object {
        fun provideFactory(plantaRepository: PlantaRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RiegosViewModel(plantaRepository) as T
                }
            }
    }
}