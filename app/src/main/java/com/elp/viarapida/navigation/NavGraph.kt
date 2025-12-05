package com.elp.viarapida.navigation

/**
 * Definición de rutas de navegación
 */
sealed class Screen(val route: String) {
    // Autenticación
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Registro : Screen("registro")
    object RecuperarPassword : Screen("recuperar_password")

    // Principales
    object Home : Screen("home")
    object MisReservas : Screen("mis_reservas")
    object Perfil : Screen("perfil")

    // Búsqueda y reserva
    object BuscarViajes : Screen("buscar_viajes")
    object ResultadosViajes : Screen("resultados_viajes")
    object DetalleViaje : Screen("detalle_viaje/{viajeId}") {
        fun createRoute(viajeId: String) = "detalle_viaje/$viajeId"
    }
    object SeleccionarAsientos : Screen("seleccionar_asientos/{viajeId}") {
        fun createRoute(viajeId: String) = "seleccionar_asientos/$viajeId"
    }
    object DatosPasajeros : Screen("datos_pasajeros")
    object ConfirmarReserva : Screen("confirmar_reserva")
    object ReservaExitosa : Screen("reserva_exitosa/{reservaId}") {
        fun createRoute(reservaId: String) = "reserva_exitosa/$reservaId"
    }

    // Detalle
    object DetalleReserva : Screen("detalle_reserva/{reservaId}") {
        fun createRoute(reservaId: String) = "detalle_reserva/$reservaId"
    }
    object BuscarReserva : Screen("buscar_reserva")

    // Perfil
    object EditarPerfil : Screen("editar_perfil")
    object ConfiguracionCuenta : Screen("configuracion_cuenta")
    object CambiarPassword : Screen("cambiar_password")
    object Ayuda : Screen("ayuda")
    object Terminos : Screen("terminos")
    // Dentro de sealed class Screen, agrega:

}