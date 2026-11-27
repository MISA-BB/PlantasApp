package mx.edu.utez.plantaapp.data.repository

import android.util.Base64
import mx.edu.utez.plantaapp.data.remote.ApiService
import mx.edu.utez.plantaapp.data.remote.dto.PlantaDto
import mx.edu.utez.plantaapp.data.remote.dto.RiegoDto
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class PlantaRepository(
    private val api: ApiService
) {
    suspend fun analizarPlanta(usuarioId: Long, imagenBase64: String): Result<PlantaDto> {
        return try {
            val bytes = Base64.decode(imagenBase64, Base64.DEFAULT)
            val userIdBody = usuarioId.toString().toRequestBody("text/plain".toMediaType())
            val imageBody = bytes.toRequestBody("image/jpeg".toMediaType())
            val imagePart = MultipartBody.Part.createFormData("imagen", "planta.jpg", imageBody)

            val response = api.identificarPlanta(userIdBody, imagePart)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("No se pudo identificar la planta (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al analizar la imagen: ${e.message}"))
        }
    }

    suspend fun obtenerPlantas(usuarioId: Long): Result<List<PlantaDto>> {
        return try {
            val response = api.obtenerPlantas(usuarioId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar plantas"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    suspend fun actualizarPlanta(planta: PlantaDto): Result<PlantaDto> {
        return try {
            val id = planta.id ?: return Result.failure(Exception("ID de planta inválido"))
            val response = api.actualizarPlanta(id, planta)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("No se pudo actualizar la planta (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    suspend fun regarPlanta(plantaId: Long): Result<PlantaDto> {
        return try {
            val response = api.regarPlanta(plantaId)
            val plantaActualizada = response.body()?.planta
            if (response.isSuccessful && plantaActualizada != null) {
                Result.success(plantaActualizada)
            } else {
                Result.failure(Exception("No se pudo registrar el riego"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    suspend fun eliminarPlanta(plantaId: Long): Result<Unit> {
        return try {
            val response = api.eliminarPlanta(plantaId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo eliminar la planta"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    suspend fun obtenerHistorialRiegos(usuarioId: Long): Result<List<RiegoDto>> {
        return try {
            val response = api.obtenerHistorialRiegos(usuarioId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar historial de riegos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }
}