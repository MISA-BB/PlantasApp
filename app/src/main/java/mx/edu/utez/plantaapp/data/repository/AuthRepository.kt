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
            val usuario = response.body()?.usuario
            if (response.isSuccessful && usuario != null) {
                sessionManager.saveSession(usuario)
                Result.success(usuario)
            } else {
                val msg = response.body()?.message ?: "Correo o contraseña incorrectos"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión con el servidor: ${e.message}"))
        }
    }

    suspend fun register(fullName: String, correo: String, password: String): Result<UsuarioDto> {
        return try {
            val nuevo = UsuarioDto(fullName = fullName.trim(), correo = correo.trim(), password = password)
            val response = api.register(nuevo)
            val usuario = response.body()?.usuario
            if (response.isSuccessful && usuario != null) {
                sessionManager.saveSession(usuario)
                Result.success(usuario)
            } else {
                val msg = response.body()?.message ?: "No se pudo registrar. Verifica si el correo ya existe."
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión con el servidor: ${e.message}"))
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }
}