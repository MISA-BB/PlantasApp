package mx.edu.utez.plantaapp.data.remote

import mx.edu.utez.plantaapp.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTH ---
    @POST("api/auth/register")
    suspend fun register(@Body request: UsuarioDto): Response<AuthResponseDto>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    // --- PLANTAS ---
    @Multipart
    @POST("api/plantas/identificar")
    suspend fun identificarPlanta(
        @Part("usuarioId") usuarioId: RequestBody,
        @Part imagen: MultipartBody.Part
    ): Response<PlantaDto>

    @GET("api/plantas/usuario/{usuarioId}")
    suspend fun obtenerPlantas(@Path("usuarioId") usuarioId: Long): Response<List<PlantaDto>>

    @GET("api/plantas/{id}")
    suspend fun obtenerPlantaPorId(@Path("id") id: Long): Response<PlantaDto>

    @PUT("api/plantas/{id}")
    suspend fun actualizarPlanta(
        @Path("id") id: Long,
        @Body planta: PlantaDto
    ): Response<PlantaDto>

    @POST("api/plantas/{id}/regar")
    suspend fun regarPlanta(@Path("id") id: Long): Response<RiegoResponseDto>

    @DELETE("api/plantas/{id}")
    suspend fun eliminarPlanta(@Path("id") id: Long): Response<Unit>

    // --- RIEGOS ---
    @GET("api/riegos/usuario/{usuarioId}")
    suspend fun obtenerHistorialRiegos(@Path("usuarioId") usuarioId: Long): Response<List<RiegoDto>>
}