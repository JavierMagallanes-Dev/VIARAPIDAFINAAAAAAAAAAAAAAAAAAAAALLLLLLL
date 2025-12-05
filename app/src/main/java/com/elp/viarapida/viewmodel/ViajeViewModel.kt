package com.elp.viarapida.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elp.viarapida.data.model.Viaje
import com.elp.viarapida.data.repository.ViajeRepository
import com.elp.viarapida.util.ValidationUtils
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViajeViewModel : ViewModel() {

    private val viajeRepository = ViajeRepository()

    // Estados de la UI
    private val _viajesState = MutableStateFlow<ViajesState>(ViajesState.Initial)
    val viajesState: StateFlow<ViajesState> = _viajesState.asStateFlow()

    private val _viajes = MutableStateFlow<List<Viaje>>(emptyList())
    val viajes: StateFlow<List<Viaje>> = _viajes.asStateFlow()

    private val _viajeSeleccionado = MutableStateFlow<Viaje?>(null)
    val viajeSeleccionado: StateFlow<Viaje?> = _viajeSeleccionado.asStateFlow()

    // Estados de loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Filtros de búsqueda
    private val _origen = MutableStateFlow("")
    val origen: StateFlow<String> = _origen.asStateFlow()

    private val _destino = MutableStateFlow("")
    val destino: StateFlow<String> = _destino.asStateFlow()

    private val _fecha = MutableStateFlow<Timestamp?>(null)
    val fecha: StateFlow<Timestamp?> = _fecha.asStateFlow()

    /**
     * Busca viajes según origen, destino y fecha
     */
    fun buscarViajes(origen: String, destino: String, fecha: Timestamp) {
        // Validar selección
        val validation = ValidationUtils.validateSeleccionViaje(
            origen, destino, com.elp.viarapida.util.DateUtils.timestampToDateString(fecha)
        )

        if (!validation.isValid) {
            _viajesState.value = ViajesState.Error(validation.errorMessage)
            return
        }

        // Guardar filtros
        _origen.value = origen
        _destino.value = destino
        _fecha.value = fecha

        viewModelScope.launch {
            _isLoading.value = true
            _viajesState.value = ViajesState.Loading

            viajeRepository.buscarViajes(origen, destino, fecha).fold(
                onSuccess = { listaViajes ->
                    _viajes.value = listaViajes
                    _viajesState.value = if (listaViajes.isEmpty()) {
                        ViajesState.Empty
                    } else {
                        ViajesState.Success(listaViajes)
                    }
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _viajesState.value = ViajesState.Error(
                        exception.message ?: "Error al buscar viajes"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Obtiene todos los viajes disponibles
     */
    fun getAllViajes() {
        viewModelScope.launch {
            _isLoading.value = true
            _viajesState.value = ViajesState.Loading

            viajeRepository.getAllViajes().fold(
                onSuccess = { listaViajes ->
                    _viajes.value = listaViajes
                    _viajesState.value = if (listaViajes.isEmpty()) {
                        ViajesState.Empty
                    } else {
                        ViajesState.Success(listaViajes)
                    }
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _viajesState.value = ViajesState.Error(
                        exception.message ?: "Error al cargar viajes"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Obtiene un viaje por su ID
     */
    fun getViajePorId(viajeId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            viajeRepository.getViajePorId(viajeId).fold(
                onSuccess = { viaje ->
                    _viajeSeleccionado.value = viaje
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _viajesState.value = ViajesState.Error(
                        exception.message ?: "Error al cargar viaje"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Selecciona un viaje
     */
    fun seleccionarViaje(viaje: Viaje) {
        _viajeSeleccionado.value = viaje
    }

    /**
     * Limpia el viaje seleccionado
     */
    fun limpiarViajeSeleccionado() {
        _viajeSeleccionado.value = null
    }

    /**
     * Filtra viajes por tipo de servicio
     */
    fun filtrarPorTipoServicio(tipoServicio: String) {
        val viajesFiltrados = _viajes.value.filter {
            it.tipoServicio == tipoServicio
        }
        _viajesState.value = if (viajesFiltrados.isEmpty()) {
            ViajesState.Empty
        } else {
            ViajesState.Success(viajesFiltrados)
        }
    }

    /**
     * Ordena viajes por precio
     */
    fun ordenarPorPrecio(ascendente: Boolean = true) {
        val viajesOrdenados = if (ascendente) {
            _viajes.value.sortedBy { it.precio }
        } else {
            _viajes.value.sortedByDescending { it.precio }
        }
        _viajesState.value = ViajesState.Success(viajesOrdenados)
    }

    /**
     * Ordena viajes por hora de salida
     */
    fun ordenarPorHoraSalida() {
        val viajesOrdenados = _viajes.value.sortedBy { it.horaSalida }
        _viajesState.value = ViajesState.Success(viajesOrdenados)
    }

    /**
     * Resetea el estado de viajes
     */
    fun resetViajesState() {
        _viajesState.value = ViajesState.Initial
        _viajes.value = emptyList()
    }

    /**
     * Actualiza los filtros de búsqueda
     */
    fun actualizarOrigen(nuevoOrigen: String) {
        _origen.value = nuevoOrigen
    }

    fun actualizarDestino(nuevoDestino: String) {
        _destino.value = nuevoDestino
    }

    fun actualizarFecha(nuevaFecha: Timestamp) {
        _fecha.value = nuevaFecha
    }

    /**
     * Limpia los filtros
     */
    fun limpiarFiltros() {
        _origen.value = ""
        _destino.value = ""
        _fecha.value = null
    }
}

/**
 * Estados posibles de viajes
 */
sealed class ViajesState {
    object Initial : ViajesState()
    object Loading : ViajesState()
    object Empty : ViajesState()
    data class Success(val viajes: List<Viaje>) : ViajesState()
    data class Error(val message: String) : ViajesState()
}