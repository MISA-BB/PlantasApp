package mx.edu.utez.plantaapp.util

import java.text.SimpleDateFormat
import java.util.Locale

fun String?.formatearFechaRiego(): String {
    if (this.isNullOrBlank()) return "Sin fecha"
    return try {
        val fechaLimpia = this.substringBefore(".")
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatoSalida = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("es", "MX"))

        val date = formatoEntrada.parse(fechaLimpia)
        if (date != null) formatoSalida.format(date) else this
    } catch (e: Exception) {
        this
    }
}