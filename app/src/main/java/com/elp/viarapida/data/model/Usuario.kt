package com.elp.viarapida.data.model

import com.google.firebase.Timestamp

data class Usuario(
    val uid: String = "",
    val email: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val telefono: String = "",
    val fotoPerfil: String = "",
    val fechaRegistro: Timestamp = Timestamp.now()
) {
    // Constructor vacío requerido por Firebase
    constructor() : this("", "", "", "", "", "", Timestamp.now())

    // Función para obtener nombre completo
    fun nombreCompleto(): String = "$nombre $apellido"
}