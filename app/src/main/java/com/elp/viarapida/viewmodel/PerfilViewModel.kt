package com.elp.viarapida.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elp.viarapida.data.model.Usuario
import com.elp.viarapida.data.repository.UsuarioRepository
import com.elp.viarapida.util.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    private val usuarioRepository = UsuarioRepository()

    // Estados de la UI
    private val _perfilState = MutableStateFlow<PerfilState>(PerfilState.Initial)
    val perfilState: StateFlow<PerfilState> = _perfilState.asStateFlow()

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    // Estados de loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        cargarUsuario()
    }

    /**
     * Carga los datos del usuario actual
     */
    fun cargarUsuario() {
        viewModelScope.launch {
            _isLoading.value = true

            usuarioRepository.getUsuarioActual().fold(
                onSuccess = { usuario ->
                    _usuario.value = usuario
                    _perfilState.value = PerfilState.Success(usuario)
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _perfilState.value = PerfilState.Error(
                        exception.message ?: "Error al cargar perfil"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Actualiza el perfil del usuario
     */
    fun actualizarPerfil(
        nombre: String,
        apellido: String,
        telefono: String
    ) {
        // Validar nombre
        val validacionNombre = ValidationUtils.validateNombre(nombre, "Nombre")
        if (!validacionNombre.isValid) {
            _perfilState.value = PerfilState.Error(validacionNombre.errorMessage)
            return
        }

        // Validar apellido
        val validacionApellido = ValidationUtils.validateNombre(apellido, "Apellido")
        if (!validacionApellido.isValid) {
            _perfilState.value = PerfilState.Error(validacionApellido.errorMessage)
            return
        }

        // Validar teléfono
        val validacionTelefono = ValidationUtils.validateTelefono(telefono)
        if (!validacionTelefono.isValid) {
            _perfilState.value = PerfilState.Error(validacionTelefono.errorMessage)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            usuarioRepository.actualizarPerfil(nombre, apellido, telefono).fold(
                onSuccess = {
                    _perfilState.value = PerfilState.UpdateSuccess
                    _isLoading.value = false
                    // Recargar datos
                    cargarUsuario()
                },
                onFailure = { exception ->
                    _perfilState.value = PerfilState.Error(
                        exception.message ?: "Error al actualizar perfil"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Actualiza la foto de perfil
     */
    fun actualizarFotoPerfil(photoUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true

            usuarioRepository.actualizarFotoPerfil(photoUrl).fold(
                onSuccess = {
                    _perfilState.value = PerfilState.PhotoUpdateSuccess
                    _isLoading.value = false
                    // Recargar datos
                    cargarUsuario()
                },
                onFailure = { exception ->
                    _perfilState.value = PerfilState.Error(
                        exception.message ?: "Error al actualizar foto"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Actualiza el email del usuario
     */
    fun actualizarEmail(nuevoEmail: String) {
        // Validar email
        val validacion = ValidationUtils.validateEmail(nuevoEmail)
        if (!validacion.isValid) {
            _perfilState.value = PerfilState.Error(validacion.errorMessage)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            usuarioRepository.actualizarEmail(nuevoEmail).fold(
                onSuccess = {
                    _perfilState.value = PerfilState.EmailUpdateSuccess
                    _isLoading.value = false
                    // Recargar datos
                    cargarUsuario()
                },
                onFailure = { exception ->
                    _perfilState.value = PerfilState.Error(
                        exception.message ?: "Error al actualizar email"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Verifica si un email ya existe
     */
    fun verificarEmailDisponible(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val existe = usuarioRepository.emailYaExiste(email)
            onResult(!existe) // true si está disponible
        }
    }

    /**
     * Resetea el estado del perfil
     */
    fun resetPerfilState() {
        _perfilState.value = PerfilState.Initial
    }
}

/**
 * Estados posibles del perfil
 */
sealed class PerfilState {
    object Initial : PerfilState()
    object UpdateSuccess : PerfilState()
    object PhotoUpdateSuccess : PerfilState()
    object EmailUpdateSuccess : PerfilState()
    data class Success(val usuario: Usuario) : PerfilState()
    data class Error(val message: String) : PerfilState()
}