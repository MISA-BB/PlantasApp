package mx.edu.utez.plantaapp.data.remote.dto

data class RiegoDto(
    val id: Long? = null,
    val usuarioId: Long,
    val plantaId: Long,
    val nombrePlanta: String,
    val imagenPlantaBase64: String? = null,
    val fechaRiego: String? = null
)