package com.elp.viarapida.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elp.viarapida.data.model.Usuario
import com.elp.viarapida.data.repository.AuthRepository
import com.elp.viarapida.util.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    // Estados de la UI
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    // Estados de loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkUserLoggedIn()
    }

    /**
     * Verifica si hay un usuario logueado al iniciar
     */
    private fun checkUserLoggedIn() {
        if (authRepository.isUserLoggedIn()) {
            _authState.value = AuthState.Authenticated
        }
    }

    /**
     * Inicia sesión con email y contraseña
     */
    fun login(email: String, password: String) {
        // Validar campos
        val emailValidation = ValidationUtils.validateEmail(email)
        if (!emailValidation.isValid) {
            _authState.value = AuthState.Error(emailValidation.errorMessage)
            return
        }

        val passwordValidation = ValidationUtils.validatePassword(password)
        if (!passwordValidation.isValid) {
            _authState.value = AuthState.Error(passwordValidation.errorMessage)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            authRepository.login(email, password).fold(
                onSuccess = { usuario ->
                    _usuario.value = usuario
                    _authState.value = AuthState.Authenticated
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Error al iniciar sesión"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Registra un nuevo usuario
     */
    fun registrar(
        nombre: String,
        apellido: String,
        email: String,
        telefono: String,
        password: String,
        confirmPassword: String
    ) {
        // Validar todos los campos
        val validation = ValidationUtils.validateRegistroCompleto(
            nombre, apellido, email, telefono, password, confirmPassword
        )

        if (!validation.isValid) {
            _authState.value = AuthState.Error(validation.errorMessage)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            authRepository.registrarUsuario(
                email, password, nombre, apellido, telefono
            ).fold(
                onSuccess = { usuario ->
                    _usuario.value = usuario
                    _authState.value = AuthState.Authenticated
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Error al registrar usuario"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Cierra sesión
     */
    fun logout() {
        authRepository.logout()
        _usuario.value = null
        _authState.value = AuthState.Initial
    }

    /**
     * Envía email de recuperación de contraseña
     */
    fun recuperarPassword(email: String) {
        val emailValidation = ValidationUtils.validateEmail(email)
        if (!emailValidation.isValid) {
            _authState.value = AuthState.Error(emailValidation.errorMessage)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            authRepository.enviarEmailRecuperacion(email).fold(
                onSuccess = {
                    _authState.value = AuthState.PasswordResetSent
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Error al enviar email"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Resetea el estado de autenticación
     */
    fun resetAuthState() {
        _authState.value = AuthState.Initial
    }
}

/**
 * Estados posibles de autenticación
 */
sealed class AuthState {
    object Initial : AuthState()
    object Authenticated : AuthState()
    object PasswordResetSent : AuthState()
    data class Error(val message: String) : AuthState()
}