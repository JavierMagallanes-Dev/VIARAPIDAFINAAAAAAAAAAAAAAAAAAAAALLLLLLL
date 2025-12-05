package com.elp.viarapida.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elp.viarapida.data.model.Pasajero
import com.elp.viarapida.data.model.Reserva
import com.elp.viarapida.data.repository.ReservaRepository
import com.elp.viarapida.util.ValidationUtils
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservaViewModel : ViewModel() {

    private val reservaRepository = ReservaRepository()

    // Estados de la UI
    private val _reservaState = MutableStateFlow<ReservaState>(ReservaState.Initial)
    val reservaState: StateFlow<ReservaState> = _reservaState.asStateFlow()

    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas: StateFlow<List<Reserva>> = _reservas.asStateFlow()

    private val _reservaSeleccionada = MutableStateFlow<Reserva?>(null)
    val reservaSeleccionada: StateFlow<Reserva?> = _reservaSeleccionada.asStateFlow()

    // Estados de loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Datos para crear reserva
    private val _pasajeros = MutableStateFlow<List<Pasajero>>(emptyList())
    val pasajeros: StateFlow<List<Pasajero>> = _pasajeros.asStateFlow()

    private val _asientosOcupados = MutableStateFlow<List<String>>(emptyList())
    val asientosOcupados: StateFlow<List<String>> = _asientosOcupados.asStateFlow()

    /**
     * Crea una nueva reserva
     */
    fun crearReserva(
        viajeId: String,
        pasajeros: List<Pasajero>,
        precioTotal: Double,
        metodoPago: String,
        viajeOrigen: String,
        viajeDestino: String,
        viajeFechaSalida: Timestamp,
        viajeEmpresa: String
    ) {
        // Validar cantidad de pasajeros
        val validacionCantidad = ValidationUtils.validateCantidadPasajeros(pasajeros.size)
        if (!validacionCantidad.isValid) {
            _reservaState.value = ReservaState.Error(validacionCantidad.errorMessage)
            return
        }

        // Validar cada pasajero
        pasajeros.forEach { pasajero ->
            val validacionPasajero = ValidationUtils.validatePasajeroCompleto(
                pasajero.nombre,
                pasajero.apellido,
                pasajero.dni,
                pasajero.asiento
            )
            if (!validacionPasajero.isValid) {
                _reservaState.value = ReservaState.Error(validacionPasajero.errorMessage)
                return
            }
        }

        // Validar precio
        val validacionPrecio = ValidationUtils.validatePrecio(precioTotal)
        if (!validacionPrecio.isValid) {
            _reservaState.value = ReservaState.Error(validacionPrecio.errorMessage)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            reservaRepository.crearReserva(
                viajeId = viajeId,
                pasajeros = pasajeros,
                precioTotal = precioTotal,
                metodoPago = metodoPago,
                viajeOrigen = viajeOrigen,
                viajeDestino = viajeDestino,
                viajeFechaSalida = viajeFechaSalida,
                viajeEmpresa = viajeEmpresa
            ).fold(
                onSuccess = { reserva ->
                    _reservaSeleccionada.value = reserva
                    _reservaState.value = ReservaState.ReservaCreada(reserva)
                    _isLoading.value = false
                    // Limpiar pasajeros después de crear
                    _pasajeros.value = emptyList()
                },
                onFailure = { exception ->
                    _reservaState.value = ReservaState.Error(
                        exception.message ?: "Error al crear reserva"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Obtiene todas las reservas del usuario
     */
    fun getReservasUsuario() {
        viewModelScope.launch {
            _isLoading.value = true

            reservaRepository.getReservasUsuario().fold(
                onSuccess = { listaReservas ->
                    _reservas.value = listaReservas
                    _reservaState.value = if (listaReservas.isEmpty()) {
                        ReservaState.Empty
                    } else {
                        ReservaState.Success(listaReservas)
                    }
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _reservaState.value = ReservaState.Error(
                        exception.message ?: "Error al cargar reservas"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Obtiene solo las reservas activas
     */
    fun getReservasActivas() {
        viewModelScope.launch {
            _isLoading.value = true

            reservaRepository.getReservasActivas().fold(
                onSuccess = { listaReservas ->
                    _reservas.value = listaReservas
                    _reservaState.value = if (listaReservas.isEmpty()) {
                        ReservaState.Empty
                    } else {
                        ReservaState.Success(listaReservas)
                    }
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _reservaState.value = ReservaState.Error(
                        exception.message ?: "Error al cargar reservas activas"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Obtiene el historial de reservas
     */
    fun getHistorialReservas() {
        viewModelScope.launch {
            _isLoading.value = true

            reservaRepository.getHistorialReservas().fold(
                onSuccess = { listaReservas ->
                    _reservas.value = listaReservas
                    _reservaState.value = if (listaReservas.isEmpty()) {
                        ReservaState.Empty
                    } else {
                        ReservaState.Success(listaReservas)
                    }
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _reservaState.value = ReservaState.Error(
                        exception.message ?: "Error al cargar historial"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Obtiene una reserva por su ID
     */
    fun getReservaPorId(reservaId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            reservaRepository.getReservaPorId(reservaId).fold(
                onSuccess = { reserva ->
                    _reservaSeleccionada.value = reserva
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _reservaState.value = ReservaState.Error(
                        exception.message ?: "Error al cargar reserva"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Busca una reserva por código
     */
    fun buscarReservaPorCodigo(codigoReserva: String) {
        if (codigoReserva.isBlank()) {
            _reservaState.value = ReservaState.Error("Ingrese un código de reserva")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            reservaRepository.getReservaPorCodigo(codigoReserva).fold(
                onSuccess = { reserva ->
                    _reservaSeleccionada.value = reserva
                    _reservaState.value = ReservaState.ReservaEncontrada(reserva)
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _reservaState.value = ReservaState.Error(
                        exception.message ?: "Reserva no encontrada"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Cancela una reserva
     */
    fun cancelarReserva(reservaId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            reservaRepository.cancelarReserva(reservaId).fold(
                onSuccess = {
                    _reservaState.value = ReservaState.ReservaCancelada
                    _isLoading.value = false
                    // Recargar reservas
                    getReservasUsuario()
                },
                onFailure = { exception ->
                    _reservaState.value = ReservaState.Error(
                        exception.message ?: "Error al cancelar reserva"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Obtiene los asientos ocupados de un viaje
     */
    fun getAsientosOcupados(viajeId: String) {
        viewModelScope.launch {
            reservaRepository.getAsientosOcupados(viajeId).fold(
                onSuccess = { asientos ->
                    _asientosOcupados.value = asientos
                },
                onFailure = {
                    _asientosOcupados.value = emptyList()
                }
            )
        }
    }

    /**
     * Agrega un pasajero a la lista
     */
    fun agregarPasajero(pasajero: Pasajero) {
        val validacion = ValidationUtils.validatePasajeroCompleto(
            pasajero.nombre,
            pasajero.apellido,
            pasajero.dni,
            pasajero.asiento
        )

        if (!validacion.isValid) {
            _reservaState.value = ReservaState.Error(validacion.errorMessage)
            return
        }

        val listActual = _pasajeros.value.toMutableList()
        listActual.add(pasajero)
        _pasajeros.value = listActual
    }

    /**
     * Elimina un pasajero de la lista
     */
    fun eliminarPasajero(indice: Int) {
        val listActual = _pasajeros.value.toMutableList()
        if (indice in listActual.indices) {
            listActual.removeAt(indice)
            _pasajeros.value = listActual
        }
    }

    /**
     * Limpia la lista de pasajeros
     */
    fun limpiarPasajeros() {
        _pasajeros.value = emptyList()
    }

    /**
     * Resetea el estado de reserva
     */
    fun resetReservaState() {
        _reservaState.value = ReservaState.Initial
    }

    /**
     * Limpia la reserva seleccionada
     */
    fun limpiarReservaSeleccionada() {
        _reservaSeleccionada.value = null
    }
}

/**
 * Estados posibles de reserva
 */
sealed class ReservaState {
    object Initial : ReservaState()
    object Empty : ReservaState()
    object ReservaCancelada : ReservaState()
    data class Success(val reservas: List<Reserva>) : ReservaState()
    data class ReservaCreada(val reserva: Reserva) : ReservaState()
    data class ReservaEncontrada(val reserva: Reserva) : ReservaState()
    data class Error(val message: String) : ReservaState()
}