package mx.edu.utez.plantaapp.data.remote.dto

data class PlantaDto(
    val id: Long? = null,
    val usuarioId: Long,
    val nombreComun: String,
    val nombreCientifico: String? = null,
    val categoria: String? = null,
    val descripcion: String? = null,
    val caracteristicasPrincipales: String? = null,
    val frecuenciaRiegoDias: Int? = 3,
    val frecuenciaRiegoTexto: String? = null,
    val cantidadAgua: String? = null,
    val necesidadLuz: String? = null,
    val tipoIluminacion: String? = null,
    val temperatura: String? = null,
    val humedad: String? = null,
    val tipoSuelo: String? = null,
    val recomendacionesGenerales: String? = null,
    val imagenBase64: String? = null,
    val ultimoRiego: String? = null,
    val proximoRiego: String? = null,
    val fechaRegistro: String? = null
)

data class AnalizarPlantaRequestDto(
    val usuarioId: Long,
    val imagenBase64: String,
    val mimeType: String = "image/jpeg"
)