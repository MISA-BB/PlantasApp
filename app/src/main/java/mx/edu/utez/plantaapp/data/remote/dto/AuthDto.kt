package mx.edu.utez.plantaapp.data.remote.dto

data class UsuarioDto(
    val id: Long? = null,
    val fullName: String,
    val correo: String,
    val password: String? = null
)

data class LoginRequestDto(
    val correo: String,
    val password: String
)