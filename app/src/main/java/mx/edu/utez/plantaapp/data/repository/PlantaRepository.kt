package mx.edu.utez.plantaapp.data.repository

import mx.edu.utez.plantaapp.data.remote.ApiService
import mx.edu.utez.plantaapp.data.remote.dto.AnalizarPlantaRequestDto
import mx.edu.utez.plantaapp.data.remote.dto.PlantaDto
import mx.edu.utez.plantaapp.data.remote.dto.RiegoDto

class PlantaRepository(
    private val api: ApiService
) {
    suspend fun analizarPlanta(usuarioId: Long, imagenBase64: String): Result<PlantaDto> {
        return try {
            val response = api.analizarPlanta(
                AnalizarPlantaRequestDto(
                    usuarioId = usuarioId,
                    imagenBase64 = imagenBase64
                )
            )
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

    suspend fun regarPlanta(plantaId: Long): Result<PlantaDto> {
        return try {
            val response = api.regarPlanta(plantaId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
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