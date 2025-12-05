package com.elp.viarapida.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Viaje(
    @DocumentId
    val id: String = "",
    val origen: String = "",
    val destino: String = "",
    val empresa: String = "",
    val fechaSalida: Timestamp = Timestamp.now(),
    val horaSalida: String = "",
    val horaLlegada: String = "",
    val duracion: String = "",
    val precio: Double = 0.0,
    val asientosDisponibles: Int = 0,
    val asientosTotales: Int = 0,
    val tipoServicio: String = "", // "Económico", "VIP", "Suite"
    val servicios: List<String> = emptyList(), // ["WiFi", "Baño", "TV", "Aire Acondicionado"]
    val activo: Boolean = true,
    val imagenUrl: String = "" // URL de la imagen del bus (opcional)
) {
    // Constructor vacío requerido por Firebase
    constructor() : this("", "", "", "", Timestamp.now(), "", "", "", 0.0, 0, 0, "", emptyList(), true, "")

    // Función para verificar disponibilidad
    fun tieneDisponibilidad(cantidadPasajeros: Int): Boolean {
        return asientosDisponibles >= cantidadPasajeros
    }

    // Función para obtener el porcentaje de ocupación
    fun porcentajeOcupacion(): Int {
        if (asientosTotales == 0) return 0
        val ocupados = asientosTotales - asientosDisponibles
        return (ocupados * 100) / asientosTotales
    }

    // Función para formatear la ruta
    fun rutaFormateada(): String = "$origen → $destino"
}