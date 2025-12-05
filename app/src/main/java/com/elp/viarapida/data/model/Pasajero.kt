package com.elp.viarapida.data.model

data class Pasajero(
    val nombre: String = "",
    val apellido: String = "",
    val dni: String = "",
    val asiento: String = ""
) {
    // Constructor vacío requerido por Firebase
    constructor() : this("", "", "", "")

    // Función para obtener nombre completo
    fun nombreCompleto(): String = "$nombre $apellido"

    // Validar que los datos estén completos
    fun esValido(): Boolean {
        return nombre.isNotBlank() &&
                apellido.isNotBlank() &&
                dni.isNotBlank() &&
                asiento.isNotBlank()
    }
}