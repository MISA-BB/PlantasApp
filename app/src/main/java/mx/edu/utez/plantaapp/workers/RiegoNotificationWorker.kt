package mx.edu.utez.plantaapp.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import mx.edu.utez.plantaapp.PlantaApplication
import mx.edu.utez.plantaapp.R
import java.time.LocalDate

class RiegoNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val usuarioId = inputData.getLong("USUARIO_ID", -1L)
        if (usuarioId == -1L) return Result.success()

        val app = context.applicationContext as PlantaApplication
        val resultado = app.container.plantaRepository.obtenerPlantas(usuarioId)

        resultado.onSuccess { plantas ->
            val hoy = LocalDate.now().toString()
            val pendientes = plantas.filter { planta ->
                planta.proximoRiego != null && planta.proximoRiego <= hoy
            }

            if (pendientes.isNotEmpty()) {
                val nombres = pendientes.joinToString(", ") { it.nombreComun }
                mostrarNotificacion(
                    titulo = "¡Hora de regar tus plantas! 💧",
                    mensaje = "Hoy toca regar: $nombres"
                )
            }
        }

        return Result.success()
    }

    private fun mostrarNotificacion(titulo: String, mensaje: String) {
        val channelId = "plantaapp_riego_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Recordatorios de Riego",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones cuando tus plantas necesitan agua"
        }
        manager.createNotificationChannel(channel)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1001, notification)
    }
}