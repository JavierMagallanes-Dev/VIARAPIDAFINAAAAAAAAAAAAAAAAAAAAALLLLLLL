package com.elp.viarapida

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.elp.viarapida.navigation.Screen
import com.elp.viarapida.ui.screens.auth.*
import com.elp.viarapida.ui.screens.main.HomeScreen
import com.elp.viarapida.ui.screens.perfil.PerfilScreen
import com.elp.viarapida.ui.screens.reservas.MisReservasScreen
import com.elp.viarapida.ui.screens.viajes.*
import com.elp.viarapida.ui.screens.reserva.*
import com.elp.viarapida.ui.theme.ViaRapidaTheme
import com.elp.viarapida.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViaRapidaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ViaRapidaApp()
                }
            }
        }
    }
}

@Composable
fun ViaRapidaApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val isLoggedIn by authViewModel.authState.collectAsState()

    // Determinar pantalla inicial
    val startDestination = if (isLoggedIn is com.elp.viarapida.viewmodel.AuthState.Authenticated) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // AUTH SCREENS
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegistro = {
                    navController.navigate(Screen.Registro.route)
                },
                onNavigateToRecuperarPassword = {
                    navController.navigate(Screen.RecuperarPassword.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Registro.route) {
            RegistroScreen(
                onNavigateBack = { navController.navigateUp() },
                onRegistroSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.RecuperarPassword.route) {
            RecuperarPasswordScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // MAIN SCREENS
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToBuscarViajes = {
                    navController.navigate(Screen.BuscarViajes.route)
                },
                onNavigateToReservas = {
                    navController.navigate(Screen.MisReservas.route)
                },
                onNavigateToPerfil = {
                    navController.navigate(Screen.Perfil.route)
                },
                onNavigateToBuscarReserva = {
                    navController.navigate(Screen.BuscarReserva.route)
                }
            )
        }

        composable(Screen.Perfil.route) {
            PerfilScreen(
                onNavigateToEditarPerfil = {
                    navController.navigate(Screen.EditarPerfil.route)
                },
                onNavigateToConfiguracion = {
                    navController.navigate(Screen.ConfiguracionCuenta.route)
                },
                onNavigateToAyuda = {
                    navController.navigate(Screen.Ayuda.route)
                },
                onNavigateToTerminos = {
                    navController.navigate(Screen.Terminos.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MisReservas.route) {
            MisReservasScreen(
                onNavigateToDetalle = { reservaId ->
                    navController.navigate(Screen.DetalleReserva.createRoute(reservaId))
                },
                onNavigateToBuscarReserva = {
                    navController.navigate(Screen.BuscarReserva.route)
                }
            )
        }

        // VIAJES SCREENS
        composable(Screen.BuscarViajes.route) {
            BuscarViajesScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToResultados = {
                    navController.navigate(Screen.ResultadosViajes.route)
                }
            )
        }

        composable(Screen.ResultadosViajes.route) {
            ResultadosViajesScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToDetalle = { viajeId ->
                    navController.navigate(Screen.DetalleViaje.createRoute(viajeId))
                }
            )
        }

        composable(
            route = Screen.DetalleViaje.route,
            arguments = listOf(navArgument("viajeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val viajeId = backStackEntry.arguments?.getString("viajeId") ?: ""
            DetalleViajeScreen(
                viajeId = viajeId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToSeleccionarAsientos = { id ->
                    navController.navigate(Screen.SeleccionarAsientos.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.SeleccionarAsientos.route,
            arguments = listOf(navArgument("viajeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val viajeId = backStackEntry.arguments?.getString("viajeId") ?: ""
            SeleccionarAsientosScreen(
                viajeId = viajeId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToDatosPasajeros = {
                    navController.navigate(Screen.DatosPasajeros.route)
                }
            )
        }

        composable(Screen.DatosPasajeros.route) {
            // Obtener asientos del estado del ViewModel
            val viajeViewModel: com.elp.viarapida.viewmodel.ViajeViewModel = viewModel()
            val reservaViewModel: com.elp.viarapida.viewmodel.ReservaViewModel = viewModel()

            DatosPasajerosScreen(
                asientosSeleccionados = listOf(), // Se obtendrán del viewModel
                onNavigateBack = { navController.navigateUp() },
                onNavigateToConfirmar = {
                    navController.navigate(Screen.ConfirmarReserva.route)
                }
            )
        }
    }
}