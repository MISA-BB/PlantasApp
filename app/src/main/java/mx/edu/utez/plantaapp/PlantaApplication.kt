package mx.edu.utez.plantaapp

import android.app.Application

class PlantaApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}