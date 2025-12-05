package com.elp.viarapida.util

object Constants {

    // FIREBASE COLLECTIONS
    const val COLLECTION_USUARIOS = "usuarios"
    const val COLLECTION_VIAJES = "viajes"
    const val COLLECTION_RESERVAS = "reservas"

    // SHARED PREFERENCES
    const val PREFS_NAME = "ViaRapidaPrefs"
    const val KEY_USER_ID = "userId"
    const val KEY_USER_EMAIL = "userEmail"
    const val KEY_IS_LOGGED_IN = "isLoggedIn"

    // ESTADOS DE RESERVA
    const val ESTADO_PENDIENTE = "pendiente"
    const val ESTADO_CONFIRMADA = "confirmada"
    const val ESTADO_CANCELADA = "cancelada"
    const val ESTADO_COMPLETADA = "completada"

    // TIPOS DE SERVICIO
    const val SERVICIO_ECONOMICO = "Económico"
    const val SERVICIO_VIP = "VIP"
    const val SERVICIO_SUITE = "Suite"

    // SERVICIOS DISPONIBLES
    val SERVICIOS_DISPONIBLES = listOf(
        "WiFi",
        "Baño",
        "TV",
        "Aire Acondicionado",
        "Recliner",
        "USB",
        "Manta",
        "Almohada"
    )

    // MÉTODOS DE PAGO
    const val PAGO_EFECTIVO = "Efectivo"
    const val PAGO_TARJETA = "Tarjeta"
    const val PAGO_YAPE = "Yape"
    const val PAGO_PLIN = "Plin"

    // RUTAS PRINCIPALES (basado en tu PDF - Ayacucho)
    val CIUDADES_ORIGEN = listOf(
        "Ayacucho",
        "Lima",
        "Huancayo",
        "Ica",
        "Cusco",
        "Andahuaylas"
    )

    val CIUDADES_DESTINO = listOf(
        "Lima",
        "Ayacucho",
        "Huancayo",
        "Ica",
        "Cusco",
        "Andahuaylas",
        "Huanta",
        "San Miguel"
    )

    // ASIENTOS CONFIGURACIÓN
    const val ASIENTOS_POR_FILA = 4
    const val TOTAL_FILAS = 10
    const val TOTAL_ASIENTOS = 40

    // VALIDACIONES
    const val MIN_NOMBRE_LENGTH = 2
    const val MAX_NOMBRE_LENGTH = 50
    const val DNI_LENGTH = 8
    const val TELEFONO_LENGTH = 9

    // FORMATO DE FECHA
    const val DATE_FORMAT_DISPLAY = "dd/MM/yyyy"
    const val DATE_FORMAT_FIREBASE = "yyyy-MM-dd"
    const val TIME_FORMAT = "HH:mm"
    const val DATETIME_FORMAT = "dd/MM/yyyy HH:mm"

    // MENSAJES DE ERROR
    const val ERROR_CAMPOS_VACIOS = "Por favor complete todos los campos"
    const val ERROR_EMAIL_INVALIDO = "Email inválido"
    const val ERROR_PASSWORD_CORTO = "La contraseña debe tener al menos 6 caracteres"
    const val ERROR_DNI_INVALIDO = "DNI debe tener 8 dígitos"
    const val ERROR_TELEFONO_INVALIDO = "Teléfono debe tener 9 dígitos"
    const val ERROR_RED = "Error de conexión. Verifica tu internet"
    const val ERROR_AUTENTICACION = "Error de autenticación"

    // MENSAJES DE ÉXITO
    const val SUCCESS_REGISTRO = "Registro exitoso"
    const val SUCCESS_LOGIN = "Inicio de sesión exitoso"
    const val SUCCESS_RESERVA = "Reserva realizada con éxito"
    const val SUCCESS_CANCELACION = "Reserva cancelada correctamente"

    // LÍMITES
    const val MAX_PASAJEROS_POR_RESERVA = 5
    const val DIAS_ANTICIPACION_COMPRA = 30
    const val HORAS_CANCELACION = 24

    // PRECIOS BASE (puedes ajustar según tu negocio)
    const val PRECIO_BASE_ECONOMICO = 30.0
    const val PRECIO_BASE_VIP = 50.0
    const val PRECIO_BASE_SUITE = 80.0

    // EXTRAS
    const val DESCUENTO_ESTUDIANTE = 0.1 // 10%
    const val DESCUENTO_TERCERA_EDAD = 0.15 // 15%
}