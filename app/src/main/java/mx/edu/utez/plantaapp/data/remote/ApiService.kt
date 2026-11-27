package mx.edu.utez.plantaapp.data.remote

import mx.edu.utez.plantaapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body usuario: UsuarioDto): Response<UsuarioDto>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<UsuarioDto>

    @POST("api/plantas/analizar")
    suspend fun analizarPlanta(@Body request: AnalizarPlantaRequestDto): Response<PlantaDto>

    @GET("api/plantas/usuario/{usuarioId}")
    suspend fun obtenerPlantas(@Path("usuarioId") usuarioId: Long): Response<List<PlantaDto>>

    @PUT("api/plantas/{plantaId}/regar")
    suspend fun regarPlanta(@Path("plantaId") plantaId: Long): Response<PlantaDto>

    @DELETE("api/plantas/{plantaId}")
    suspend fun eliminarPlanta(@Path("plantaId") plantaId: Long): Response<Unit>

    @GET("api/riegos/usuario/{usuarioId}")
    suspend fun obtenerHistorialRiegos(@Path("usuarioId") usuarioId: Long): Response<List<RiegoDto>>
}