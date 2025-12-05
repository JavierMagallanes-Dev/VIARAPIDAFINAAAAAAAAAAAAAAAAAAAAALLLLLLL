package com.elp.viarapida.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Reserva(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val viajeId: String = "",
    val pasajeros: List<Pasajero> = emptyList(),
    val cantidadPasajeros: Int = 0,
    val precioTotal: Double = 0.0,
    val fechaReserva: Timestamp = Timestamp.now(),
    val estado: String = "pendiente", // "pendiente", "confirmada", "cancelada", "completada"
    val metodoPago: String = "",
    val codigoReserva: String = "",

    // Datos del viaje (para mostrar sin hacer otra consulta)
    val viajeOrigen: String = "",
    val viajeDestino: String = "",
    val viajeFechaSalida: Timestamp = Timestamp.now(),
    val viajeEmpresa: String = ""
) {
    // Constructor vacío requerido por Firebase
    constructor() : this(
        "", "", "", emptyList(), 0, 0.0,
        Timestamp.now(), "pendiente", "", "",
        "", "", Timestamp.now(), ""
    )

    // Función para verificar si la reserva está activa
    fun estaActiva(): Boolean = estado == "confirmada" || estado == "pendiente"

    // Función para verificar si ya pasó el viaje
    fun yaPaso(): Boolean {
        val ahora = System.currentTimeMillis()
        return viajeFechaSalida.toDate().time < ahora
    }

    // Función para obtener el color del estado
    fun getEstadoColor(): String {
        return when (estado) {
            "confirmada" -> "#388E3C" // Verde
            "pendiente" -> "#F57C00"  // Naranja
            "cancelada" -> "#D32F2F"  // Rojo
            "completada" -> "#1976D2" // Azul
            else -> "#9E9E9E"         // Gris
        }
    }

    // Función para obtener texto del estado
    fun getEstadoTexto(): String {
        return when (estado) {
            "confirmada" -> "Confirmada"
            "pendiente" -> "Pendiente"
            "cancelada" -> "Cancelada"
            "completada" -> "Completada"
            else -> "Desconocido"
        }
    }
}