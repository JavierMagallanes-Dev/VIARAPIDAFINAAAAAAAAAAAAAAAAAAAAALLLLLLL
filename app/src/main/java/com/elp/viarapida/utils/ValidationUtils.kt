package com.elp.viarapida.util

import android.util.Patterns

object ValidationUtils {

    /**
     * Resultado de validación con mensaje
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String = ""
    )

    /**
     * Valida que un campo no esté vacío
     */
    fun validateNotEmpty(value: String, fieldName: String = "Campo"): ValidationResult {
        return if (value.isBlank()) {
            ValidationResult(false, "$fieldName no puede estar vacío")
        } else {
            ValidationResult(true)
        }
    }

    /**
     * Valida formato de email
     */
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(false, "Email no puede estar vacío")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult(false, Constants.ERROR_EMAIL_INVALIDO)
            else -> ValidationResult(true)
        }
    }

    /**
     * Valida longitud de contraseña
     */
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult(false, "Contraseña no puede estar vacía")
            password.length < 6 -> ValidationResult(false, Constants.ERROR_PASSWORD_CORTO)
            else -> ValidationResult(true)
        }
    }

    /**
     * Valida que las contraseñas coincidan
     */
    fun validatePasswordMatch(password: String, confirmPassword: String): ValidationResult {
        return if (password != confirmPassword) {
            ValidationResult(false, "Las contraseñas no coinciden")
        } else {
            ValidationResult(true)
        }
    }

    /**
     * Valida formato de DNI peruano (8 dígitos)
     */
    fun validateDNI(dni: String): ValidationResult {
        return when {
            dni.isBlank() -> ValidationResult(false, "DNI no puede estar vacío")
            dni.length != Constants.DNI_LENGTH -> ValidationResult(false, Constants.ERROR_DNI_INVALIDO)
            !dni.all { it.isDigit() } -> ValidationResult(false, "DNI debe contener solo números")
            else -> ValidationResult(true)
        }
    }

    /**
     * Valida formato de teléfono peruano (9 dígitos)
     */
    fun validateTelefono(telefono: String): ValidationResult {
        return when {
            telefono.isBlank() -> ValidationResult(false, "Teléfono no puede estar vacío")
            telefono.length != Constants.TELEFONO_LENGTH ->
                ValidationResult(false, Constants.ERROR_TELEFONO_INVALIDO)
            !telefono.all { it.isDigit() } ->
                ValidationResult(false, "Teléfono debe contener solo números")
            !telefono.startsWith("9") ->
                ValidationResult(false, "Teléfono debe comenzar con 9")
            else -> ValidationResult(true)
        }
    }

    /**
     * Valida nombre (longitud y caracteres)
     */
    fun validateNombre(nombre: String, fieldName: String = "Nombre"): ValidationResult {
        return when {
            nombre.isBlank() -> ValidationResult(false, "$fieldName no puede estar vacío")
            nombre.length < Constants.MIN_NOMBRE_LENGTH ->
                ValidationResult(false, "$fieldName debe tener al menos ${Constants.MIN_NOMBRE_LENGTH} caracteres")
            nombre.length > Constants.MAX_NOMBRE_LENGTH ->
                ValidationResult(false, "$fieldName no debe exceder ${Constants.MAX_NOMBRE_LENGTH} caracteres")
            !nombre.all { it.isLetter() || it.isWhitespace() } ->
                ValidationResult(false, "$fieldName debe contener solo letras")
            else -> ValidationResult(true)
        }
    }

    /**
     * Valida número de asiento
     */
    fun validateAsiento(asiento: String): ValidationResult {
        return when {
            asiento.isBlank() -> ValidationResult(false, "Debe seleccionar un asiento")
            else -> ValidationResult(true)
        }
    }

    /**
     * Valida cantidad de pasajeros
     */
    fun validateCantidadPasajeros(cantidad: Int): ValidationResult {
        return when {
            cantidad <= 0 -> ValidationResult(false, "Debe seleccionar al menos 1 pasajero")
            cantidad > Constants.MAX_PASAJEROS_POR_RESERVA ->
                ValidationResult(false, "Máximo ${Constants.MAX_PASAJEROS_POR_RESERVA} pasajeros por reserva")
            else -> ValidationResult(true)
        }
    }

    /**
     * Valida que el precio sea válido
     */
    fun validatePrecio(precio: Double): ValidationResult {
        return when {
            precio <= 0 -> ValidationResult(false, "Precio inválido")
            else -> ValidationResult(true)
        }
    }

    /**
     * Valida formulario completo de registro
     */
    fun validateRegistroCompleto(
        nombre: String,
        apellido: String,
        email: String,
        telefono: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        // Validar nombre
        validateNombre(nombre, "Nombre").let {
            if (!it.isValid) return it
        }

        // Validar apellido
        validateNombre(apellido, "Apellido").let {
            if (!it.isValid) return it
        }

        // Validar email
        validateEmail(email).let {
            if (!it.isValid) return it
        }

        // Validar teléfono
        validateTelefono(telefono).let {
            if (!it.isValid) return it
        }

        // Validar password
        validatePassword(password).let {
            if (!it.isValid) return it
        }

        // Validar que coincidan
        validatePasswordMatch(password, confirmPassword).let {
            if (!it.isValid) return it
        }

        return ValidationResult(true)
    }

    /**
     * Valida formulario de pasajero
     */
    fun validatePasajeroCompleto(
        nombre: String,
        apellido: String,
        dni: String,
        asiento: String
    ): ValidationResult {
        // Validar nombre
        validateNombre(nombre, "Nombre").let {
            if (!it.isValid) return it
        }

        // Validar apellido
        validateNombre(apellido, "Apellido").let {
            if (!it.isValid) return it
        }

        // Validar DNI
        validateDNI(dni).let {
            if (!it.isValid) return it
        }

        // Validar asiento
        validateAsiento(asiento).let {
            if (!it.isValid) return it
        }

        return ValidationResult(true)
    }

    /**
     * Valida selección de viaje
     */
    fun validateSeleccionViaje(
        origen: String,
        destino: String,
        fecha: String
    ): ValidationResult {
        return when {
            origen.isBlank() -> ValidationResult(false, "Seleccione origen")
            destino.isBlank() -> ValidationResult(false, "Seleccione destino")
            origen == destino -> ValidationResult(false, "Origen y destino deben ser diferentes")
            fecha.isBlank() -> ValidationResult(false, "Seleccione fecha")
            else -> ValidationResult(true)
        }
    }
}