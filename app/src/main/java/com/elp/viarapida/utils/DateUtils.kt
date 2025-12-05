package com.elp.viarapida.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val displayDateFormat = SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault())
    private val firebaseDateFormat = SimpleDateFormat(Constants.DATE_FORMAT_FIREBASE, Locale.getDefault())
    private val timeFormat = SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat(Constants.DATETIME_FORMAT, Locale.getDefault())

    /**
     * Convierte un Timestamp de Firebase a String legible
     * Ejemplo: 05/12/2025
     */
    fun timestampToDateString(timestamp: Timestamp): String {
        return try {
            displayDateFormat.format(timestamp.toDate())
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Convierte un Timestamp a String con hora
     * Ejemplo: 05/12/2025 14:30
     */
    fun timestampToDateTimeString(timestamp: Timestamp): String {
        return try {
            dateTimeFormat.format(timestamp.toDate())
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Convierte un Timestamp a solo hora
     * Ejemplo: 14:30
     */
    fun timestampToTimeString(timestamp: Timestamp): String {
        return try {
            timeFormat.format(timestamp.toDate())
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Convierte String de fecha a Timestamp
     * Formato esperado: dd/MM/yyyy
     */
    fun dateStringToTimestamp(dateString: String): Timestamp? {
        return try {
            val date = displayDateFormat.parse(dateString)
            date?.let { Timestamp(it) }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtiene la fecha actual como Timestamp
     */
    fun getCurrentTimestamp(): Timestamp {
        return Timestamp.now()
    }

    /**
     * Obtiene la fecha actual como String
     * Formato: dd/MM/yyyy
     */
    fun getCurrentDateString(): String {
        return displayDateFormat.format(Date())
    }

    /**
     * Verifica si una fecha ya pasó
     */
    fun isPastDate(timestamp: Timestamp): Boolean {
        return timestamp.toDate().time < System.currentTimeMillis()
    }

    /**
     * Verifica si una fecha es futura
     */
    fun isFutureDate(timestamp: Timestamp): Boolean {
        return timestamp.toDate().time > System.currentTimeMillis()
    }

    /**
     * Calcula los días de diferencia entre dos fechas
     */
    fun daysBetween(start: Timestamp, end: Timestamp): Long {
        val diff = end.toDate().time - start.toDate().time
        return diff / (1000 * 60 * 60 * 24)
    }

    /**
     * Calcula las horas de diferencia entre dos fechas
     */
    fun hoursBetween(start: Timestamp, end: Timestamp): Long {
        val diff = end.toDate().time - start.toDate().time
        return diff / (1000 * 60 * 60)
    }

    /**
     * Verifica si quedan menos de X horas para una fecha
     */
    fun isLessThanXHoursAway(timestamp: Timestamp, hours: Int): Boolean {
        val hoursUntil = hoursBetween(getCurrentTimestamp(), timestamp)
        return hoursUntil < hours && hoursUntil > 0
    }

    /**
     * Agrega días a una fecha
     */
    fun addDays(timestamp: Timestamp, days: Int): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.time = timestamp.toDate()
        calendar.add(Calendar.DAY_OF_MONTH, days)
        return Timestamp(calendar.time)
    }

    /**
     * Obtiene el nombre del día de la semana
     * Ejemplo: Lunes, Martes, etc.
     */
    fun getDayOfWeek(timestamp: Timestamp): String {
        val calendar = Calendar.getInstance()
        calendar.time = timestamp.toDate()
        val dayFormat = SimpleDateFormat("EEEE", Locale("es", "ES"))
        return dayFormat.format(calendar.time).capitalize(Locale.getDefault())
    }

    /**
     * Verifica si una fecha está dentro del rango permitido de compra
     * (no más de X días de anticipación)
     */
    fun isWithinPurchaseRange(timestamp: Timestamp, maxDays: Int): Boolean {
        val daysUntil = daysBetween(getCurrentTimestamp(), timestamp)
        return daysUntil in 0..maxDays
    }

    /**
     * Formatea una duración en horas y minutos
     * Ejemplo: "3h 30m"
     */
    fun formatDuration(durationString: String): String {
        return try {
            val parts = durationString.split(":")
            if (parts.size == 2) {
                val hours = parts[0].toIntOrNull() ?: 0
                val minutes = parts[1].toIntOrNull() ?: 0
                buildString {
                    if (hours > 0) append("${hours}h ")
                    if (minutes > 0) append("${minutes}m")
                }
            } else {
                durationString
            }
        } catch (e: Exception) {
            durationString
        }
    }

    /**
     * Valida que una fecha sea válida para reserva
     */
    fun isValidBookingDate(timestamp: Timestamp): Boolean {
        return isFutureDate(timestamp) &&
                isWithinPurchaseRange(timestamp, Constants.DIAS_ANTICIPACION_COMPRA)
    }
}