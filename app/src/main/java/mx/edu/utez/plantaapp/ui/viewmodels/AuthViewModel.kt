package mx.edu.utez.plantaapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.edu.utez.plantaapp.data.remote.dto.UsuarioDto
import mx.edu.utez.plantaapp.data.repository.AuthRepository

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<UsuarioDto?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(correo: String, password: String, onSuccess: (UsuarioDto) -> Unit) {
        if (correo.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(error = "Completa todos los campos")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.login(correo, password)
            result.fold(
                onSuccess = { usuario ->
                    _uiState.value = AuthUiState(isLoading = false)
                    onSuccess(usuario)
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState(isLoading = false, error = e.message)
                }
            )
        }
    }

    fun register(fullName: String, correo: String, password: String, onSuccess: (UsuarioDto) -> Unit) {
        if (fullName.isBlank() || correo.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(error = "Completa todos los campos")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.register(fullName, correo, password)
            result.fold(
                onSuccess = { usuario ->
                    _uiState.value = AuthUiState(isLoading = false)
                    onSuccess(usuario)
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState(isLoading = false, error = e.message)
                }
            )
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLoggedOut()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    companion object {
        fun provideFactory(authRepository: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(authRepository) as T
                }
            }
    }
}