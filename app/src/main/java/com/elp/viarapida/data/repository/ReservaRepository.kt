package com.elp.viarapida.data.repository

import com.elp.viarapida.data.model.Pasajero
import com.elp.viarapida.data.model.Reserva
import com.elp.viarapida.util.Constants
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.*

class ReservaRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val reservasCollection = firestore.collection(Constants.COLLECTION_RESERVAS)
    private val viajesCollection = firestore.collection(Constants.COLLECTION_VIAJES)

    /**
     * Crea una nueva reserva
     */
    suspend fun crearReserva(
        viajeId: String,
        pasajeros: List<Pasajero>,
        precioTotal: Double,
        metodoPago: String,
        // Datos del viaje para guardarlo en la reserva
        viajeOrigen: String,
        viajeDestino: String,
        viajeFechaSalida: Timestamp,
        viajeEmpresa: String
    ): Result<Reserva> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            // Generar código único de reserva
            val codigoReserva = generarCodigoReserva()

            // Crear objeto reserva
            val reserva = Reserva(
                userId = userId,
                viajeId = viajeId,
                pasajeros = pasajeros,
                cantidadPasajeros = pasajeros.size,
                precioTotal = precioTotal,
                fechaReserva = Timestamp.now(),
                estado = Constants.ESTADO_CONFIRMADA,
                metodoPago = metodoPago,
                codigoReserva = codigoReserva,
                viajeOrigen = viajeOrigen,
                viajeDestino = viajeDestino,
                viajeFechaSalida = viajeFechaSalida,
                viajeEmpresa = viajeEmpresa
            )

            // Guardar en Firestore
            val docRef = reservasCollection.add(reserva).await()

            // Actualizar asientos disponibles del viaje
            actualizarAsientosViaje(viajeId, -pasajeros.size)

            // Retornar reserva con ID asignado
            Result.success(reserva.copy(id = docRef.id))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene todas las reservas del usuario actual
     */
    suspend fun getReservasUsuario(): Result<List<Reserva>> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val snapshot = reservasCollection
                .whereEqualTo("userId", userId)
                .orderBy("fechaReserva", Query.Direction.DESCENDING)
                .get()
                .await()

            val reservas = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Reserva::class.java)?.copy(id = doc.id)
            }

            Result.success(reservas)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene reservas activas del usuario (confirmadas o pendientes)
     */
    suspend fun getReservasActivas(): Result<List<Reserva>> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val snapshot = reservasCollection
                .whereEqualTo("userId", userId)
                .whereIn("estado", listOf(
                    Constants.ESTADO_CONFIRMADA,
                    Constants.ESTADO_PENDIENTE
                ))
                .orderBy("viajeFechaSalida", Query.Direction.ASCENDING)
                .get()
                .await()

            val reservas = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Reserva::class.java)?.copy(id = doc.id)
            }.filter { it.estaActiva() && !it.yaPaso() }

            Result.success(reservas)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene el historial de reservas (completadas y canceladas)
     */
    suspend fun getHistorialReservas(): Result<List<Reserva>> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val snapshot = reservasCollection
                .whereEqualTo("userId", userId)
                .whereIn("estado", listOf(
                    Constants.ESTADO_COMPLETADA,
                    Constants.ESTADO_CANCELADA
                ))
                .orderBy("fechaReserva", Query.Direction.DESCENDING)
                .get()
                .await()

            val reservas = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Reserva::class.java)?.copy(id = doc.id)
            }

            Result.success(reservas)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene una reserva por su ID
     */
    suspend fun getReservaPorId(reservaId: String): Result<Reserva> {
        return try {
            val doc = reservasCollection
                .document(reservaId)
                .get()
                .await()

            val reserva = doc.toObject(Reserva::class.java)?.copy(id = doc.id)
                ?: return Result.failure(Exception("Reserva no encontrada"))

            Result.success(reserva)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene una reserva por su código
     */
    suspend fun getReservaPorCodigo(codigoReserva: String): Result<Reserva> {
        return try {
            val snapshot = reservasCollection
                .whereEqualTo("codigoReserva", codigoReserva)
                .limit(1)
                .get()
                .await()

            val reserva = snapshot.documents.firstOrNull()
                ?.toObject(Reserva::class.java)
                ?.copy(id = snapshot.documents.first().id)
                ?: return Result.failure(Exception("Reserva no encontrada"))

            Result.success(reserva)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cancela una reserva
     */
    suspend fun cancelarReserva(reservaId: String): Result<Unit> {
        return try {
            // Obtener la reserva
            val reserva = getReservaPorId(reservaId).getOrNull()
                ?: return Result.failure(Exception("Reserva no encontrada"))

            // Verificar que se pueda cancelar (al menos 24 horas antes)
            val horasHastaViaje = com.elp.viarapida.util.DateUtils.hoursBetween(
                com.elp.viarapida.util.DateUtils.getCurrentTimestamp(),
                reserva.viajeFechaSalida
            )

            if (horasHastaViaje < Constants.HORAS_CANCELACION) {
                return Result.failure(Exception(
                    "No se puede cancelar con menos de ${Constants.HORAS_CANCELACION} horas de anticipación"
                ))
            }

            // Actualizar estado
            reservasCollection
                .document(reservaId)
                .update("estado", Constants.ESTADO_CANCELADA)
                .await()

            // Devolver asientos al viaje
            actualizarAsientosViaje(reserva.viajeId, reserva.cantidadPasajeros)

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza el estado de una reserva
     */
    suspend fun actualizarEstadoReserva(
        reservaId: String,
        nuevoEstado: String
    ): Result<Unit> {
        return try {
            reservasCollection
                .document(reservaId)
                .update("estado", nuevoEstado)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verifica si un asiento está ocupado en un viaje
     */
    suspend fun asientoEstaOcupado(viajeId: String, numeroAsiento: String): Result<Boolean> {
        return try {
            val snapshot = reservasCollection
                .whereEqualTo("viajeId", viajeId)
                .whereIn("estado", listOf(
                    Constants.ESTADO_CONFIRMADA,
                    Constants.ESTADO_PENDIENTE
                ))
                .get()
                .await()

            val reservas = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Reserva::class.java)
            }

            // Verificar si algún pasajero tiene ese asiento
            val ocupado = reservas.any { reserva ->
                reserva.pasajeros.any { pasajero ->
                    pasajero.asiento == numeroAsiento
                }
            }

            Result.success(ocupado)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene los asientos ocupados de un viaje
     */
    suspend fun getAsientosOcupados(viajeId: String): Result<List<String>> {
        return try {
            val snapshot = reservasCollection
                .whereEqualTo("viajeId", viajeId)
                .whereIn("estado", listOf(
                    Constants.ESTADO_CONFIRMADA,
                    Constants.ESTADO_PENDIENTE
                ))
                .get()
                .await()

            val reservas = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Reserva::class.java)
            }

            // Recopilar todos los asientos ocupados
            val asientosOcupados = reservas.flatMap { reserva ->
                reserva.pasajeros.map { pasajero -> pasajero.asiento }
            }.distinct()

            Result.success(asientosOcupados)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Funciones auxiliares privadas

    private suspend fun actualizarAsientosViaje(viajeId: String, cambio: Int) {
        try {
            val viajeDoc = viajesCollection.document(viajeId).get().await()
            val asientosActuales = viajeDoc.getLong("asientosDisponibles")?.toInt() ?: 0
            val nuevosAsientos = asientosActuales + cambio

            viajesCollection
                .document(viajeId)
                .update("asientosDisponibles", nuevosAsientos)
                .await()
        } catch (e: Exception) {
            // Log error pero no fallar la operación principal
        }
    }

    private fun generarCodigoReserva(): String {
        val timestamp = System.currentTimeMillis().toString().takeLast(6)
        val random = (1000..9999).random()
        return "VR$timestamp$random"
    }
}