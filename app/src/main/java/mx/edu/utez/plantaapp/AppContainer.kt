package mx.edu.utez.plantaapp

import android.content.Context
import mx.edu.utez.plantaapp.data.local.SessionManager
import mx.edu.utez.plantaapp.data.remote.RetrofitClient
import mx.edu.utez.plantaapp.data.repository.AuthRepository
import mx.edu.utez.plantaapp.data.repository.PlantaRepository

class AppContainer(context: Context) {
    val sessionManager = SessionManager(context)
    val authRepository = AuthRepository(RetrofitClient.api, sessionManager)
    val plantaRepository = PlantaRepository(RetrofitClient.api)
}