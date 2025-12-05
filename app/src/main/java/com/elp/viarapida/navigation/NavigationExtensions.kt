package com.elp.viarapida.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder

/**
 * Extensiones útiles para navegación
 */

/**
 * Navega a una ruta y limpia el back stack
 */
fun NavController.navigateAndClearBackStack(route: String) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
    }
}

/**
 * Navega a una ruta con single top
 */
fun NavController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}

/**
 * Navega a una ruta guardando el estado
 */
fun NavController.navigateWithState(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Navega al login y limpia todo
 */
fun NavController.navigateToLogin() {
    navigateAndClearBackStack(Screen.Login.route)
}

/**
 * Navega al home principal después del login
 */
fun NavController.navigateToHome() {
    navigateAndClearBackStack(Screen.Home.route)
}

/**
 * Navega atrás de forma segura
 */
fun NavController.navigateBack() {
    if (previousBackStackEntry != null) {
        popBackStack()
    }
}