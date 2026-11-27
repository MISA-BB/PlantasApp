package mx.edu.utez.plantaapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    val message: String? = null,
    @SerializedName(value = "usuario", alternate = ["user", "data"])
    val usuario: UsuarioDto? = null
)

data class UsuarioDto(
    @SerializedName(value = "id", alternate = ["usuarioId", "idUsuario"])
    val id: Long? = null,
    @SerializedName(value = "fullName", alternate = ["full_name", "nombre", "nombreCompleto"])
    val fullName: String? = null,
    val correo: String = "",
    val password: String? = null
)

data class LoginRequestDto(
    val correo: String,
    val password: String
)