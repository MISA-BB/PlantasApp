package mx.edu.utez.plantaapp.data.repository

import kotlinx.coroutines.flow.Flow
import mx.edu.utez.plantaapp.data.local.SessionManager
import mx.edu.utez.plantaapp.data.remote.ApiService
import mx.edu.utez.plantaapp.data.remote.dto.LoginRequestDto
import mx.edu.utez.plantaapp.data.remote.dto.UsuarioDto

class AuthRepository(
    private val api: ApiService,
    private val sessionManager: SessionManager
) {
    val currentUser: Flow<UsuarioDto?> = sessionManager.userFlow

    suspend fun login(correo: String, password: String): Result<UsuarioDto> {
        return try {
            val response = api.login(LoginRequestDto(correo.trim(), password))
            if (response.isSuccessful && response.body() != null) {
                val usuario = response.body()!!
                sessionManager.saveSession(usuario)
                Result.success(usuario)
            } else {
                Result.failure(Exception("Correo o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión con el servidor: ${e.message}"))
        }
    }

    suspend fun register(fullName: String, correo: String, password: String): Result<UsuarioDto> {
        return try {
            val nuevo = UsuarioDto(fullName = fullName.trim(), correo = correo.trim(), password = password)
            val response = api.register(nuevo)
            if (response.isSuccessful && response.body() != null) {
                val usuario = response.body()!!
                sessionManager.saveSession(usuario)
                Result.success(usuario)
            } else {
                Result.failure(Exception("No se pudo registrar. Verifica si el correo ya existe."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión con el servidor: ${e.message}"))
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }
}