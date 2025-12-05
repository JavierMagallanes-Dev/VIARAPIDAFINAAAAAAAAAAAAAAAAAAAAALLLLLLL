package com.elp.viarapida.data.repository

import com.elp.viarapida.data.model.Viaje
import com.elp.viarapida.util.Constants
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ViajeRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val viajesCollection = firestore.collection(Constants.COLLECTION_VIAJES)

    /**
     * Busca viajes según origen, destino y fecha
     */
    suspend fun buscarViajes(
        origen: String,
        destino: String,
        fecha: Timestamp
    ): Result<List<Viaje>> {
        return try {
            // Calculamos el rango de fechas (todo el día seleccionado)
            val startOfDay = getStartOfDay(fecha)
            val endOfDay = getEndOfDay(fecha)

            val snapshot = viajesCollection
                .whereEqualTo("origen", origen)
                .whereEqualTo("destino", destino)
                .whereGreaterThanOrEqualTo("fechaSalida", startOfDay)
                .whereLessThanOrEqualTo("fechaSalida", endOfDay)
                .whereEqualTo("activo", true)
                .orderBy("fechaSalida", Query.Direction.ASCENDING)
                .get()
                .await()

            val viajes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Viaje::class.java)
            }

            Result.success(viajes)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene todos los viajes disponibles (activos)
     */
    suspend fun getAllViajes(): Result<List<Viaje>> {
        return try {
            val snapshot = viajesCollection
                .whereEqualTo("activo", true)
                .orderBy("fechaSalida", Query.Direction.ASCENDING)
                .get()
                .await()

            val viajes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Viaje::class.java)
            }

            Result.success(viajes)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene un viaje por su ID
     */
    suspend fun getViajePorId(viajeId: String): Result<Viaje> {
        return try {
            val doc = viajesCollection
                .document(viajeId)
                .get()
                .await()

            val viaje = doc.toObject(Viaje::class.java)
                ?: return Result.failure(Exception("Viaje no encontrado"))

            Result.success(viaje)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene viajes por origen
     */
    suspend fun getViajesPorOrigen(origen: String): Result<List<Viaje>> {
        return try {
            val snapshot = viajesCollection
                .whereEqualTo("origen", origen)
                .whereEqualTo("activo", true)
                .orderBy("fechaSalida", Query.Direction.ASCENDING)
                .get()
                .await()

            val viajes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Viaje::class.java)
            }

            Result.success(viajes)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene viajes por destino
     */
    suspend fun getViajesPorDestino(destino: String): Result<List<Viaje>> {
        return try {
            val snapshot = viajesCollection
                .whereEqualTo("destino", destino)
                .whereEqualTo("activo", true)
                .orderBy("fechaSalida", Query.Direction.ASCENDING)
                .get()
                .await()

            val viajes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Viaje::class.java)
            }

            Result.success(viajes)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene viajes por tipo de servicio
     */
    suspend fun getViajesPorTipoServicio(tipoServicio: String): Result<List<Viaje>> {
        return try {
            val snapshot = viajesCollection
                .whereEqualTo("tipoServicio", tipoServicio)
                .whereEqualTo("activo", true)
                .orderBy("fechaSalida", Query.Direction.ASCENDING)
                .get()
                .await()

            val viajes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Viaje::class.java)
            }

            Result.success(viajes)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza la disponibilidad de asientos de un viaje
     */
    suspend fun actualizarAsientosDisponibles(
        viajeId: String,
        nuevosAsientosDisponibles: Int
    ): Result<Unit> {
        return try {
            viajesCollection
                .document(viajeId)
                .update("asientosDisponibles", nuevosAsientosDisponibles)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verifica si un viaje tiene disponibilidad para X pasajeros
     */
    suspend fun tieneDisponibilidad(
        viajeId: String,
        cantidadPasajeros: Int
    ): Result<Boolean> {
        return try {
            val viaje = getViajePorId(viajeId).getOrNull()
                ?: return Result.failure(Exception("Viaje no encontrado"))

            val disponible = viaje.tieneDisponibilidad(cantidadPasajeros)
            Result.success(disponible)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Funciones auxiliares para manejo de fechas

    private fun getStartOfDay(timestamp: Timestamp): Timestamp {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = timestamp.toDate()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return Timestamp(calendar.time)
    }

    private fun getEndOfDay(timestamp: Timestamp): Timestamp {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = timestamp.toDate()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        return Timestamp(calendar.time)
    }
}