package com.elp.viarapida.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elp.viarapida.ui.components.*
import com.elp.viarapida.ui.theme.*
import com.elp.viarapida.util.Constants
import com.elp.viarapida.viewmodel.AuthViewModel
import com.elp.viarapida.viewmodel.ViajeViewModel
import com.elp.viarapida.viewmodel.ViajesState
import com.google.firebase.Timestamp

/**
 * Pantalla principal (Home) de la aplicación
 */
@Composable
fun HomeScreen(
    onNavigateToBuscarViajes: () -> Unit,
    onNavigateToReservas: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToBuscarReserva: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    viajeViewModel: ViajeViewModel = viewModel()
) {
    val usuario by authViewModel.usuario.collectAsState()
    val viajesState by viajeViewModel.viajesState.collectAsState()

    // Cargar viajes destacados al iniciar
    LaunchedEffect(Unit) {
        viajeViewModel.getAllViajes()
    }

    Scaffold(
        topBar = {
            VRTopBarWithLogo(
                actions = {
                    IconButton(onClick = onNavigateToPerfil) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header con saludo
            item {
                WelcomeHeader(
                    userName = usuario?.nombre ?: "Usuario"
                )
            }

            // Búsqueda rápida
            item {
                QuickSearchCard(
                    onSearchClick = onNavigateToBuscarViajes,
                    onFindReservaClick = onNavigateToBuscarReserva
                )
            }

            // Rutas populares
            item {
                PopularRoutesSection()
            }

            // Viajes destacados
            item {
                VRSectionHeader(
                    title = "Viajes Destacados",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    action = {
                        VRTextButton(
                            text = "Ver todos",
                            onClick = onNavigateToBuscarViajes
                        )
                    }
                )
            }

            // Lista de viajes
            when (val state = viajesState) {
                is ViajesState.Loading -> {
                    item {
                        VRLoadingIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
                is ViajesState.Success -> {
                    items(state.viajes.take(5)) { viaje ->
                        ViajeCard(
                            viaje = viaje,
                            onClick = { /* Navegar a detalle */ },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
                is ViajesState.Empty -> {
                    item {
                        VREmptyState(
                            title = "Sin viajes disponibles",
                            message = "No hay viajes disponibles en este momento",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
                is ViajesState.Error -> {
                    item {
                        VRErrorState(
                            message = state.message,
                            onRetry = { viajeViewModel.getAllViajes() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
                else -> {}
            }

            // Información y beneficios
            item {
                BenefitsSection()
            }
        }
    }
}

@Composable
private fun WelcomeHeader(userName: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Primary,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "¡Hola, $userName!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "¿A dónde viajas hoy?",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun QuickSearchCard(
    onSearchClick: () -> Unit,
    onFindReservaClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Acciones Rápidas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.Search,
                    label = "Buscar Viajes",
                    onClick = onSearchClick,
                    modifier = Modifier.weight(1f)
                )

                QuickActionButton(
                    icon = Icons.Default.QrCodeScanner,
                    label = "Mis Reservas",
                    onClick = onFindReservaClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Primary.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PopularRoutesSection() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        VRSectionHeader(
            title = "Rutas Populares",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val routes = listOf(
                "Ayacucho - Lima",
                "Lima - Ayacucho",
                "Ayacucho - Huancayo",
                "Ayacucho - Cusco",
                "Lima - Ica"
            )

            items(routes) { route ->
                PopularRouteCard(route)
            }
        }
    }
}

@Composable
private fun PopularRouteCard(route: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Secondary.copy(alpha = 0.1f),
        modifier = Modifier.width(160.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.TripOrigin,
                contentDescription = null,
                tint = Secondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = route,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = Secondary
            )
        }
    }
}

@Composable
private fun BenefitsSection() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        VRSectionHeader(title = "¿Por qué Vía Rápida?")

        Spacer(modifier = Modifier.height(12.dp))

        BenefitItem(
            icon = Icons.Default.Security,
            title = "Viajes Seguros",
            description = "Buses modernos con los más altos estándares de seguridad"
        )

        Spacer(modifier = Modifier.height(12.dp))

        BenefitItem(
            icon = Icons.Default.Schedule,
            title = "Puntualidad",
            description = "Compromiso con los horarios establecidos"
        )

        Spacer(modifier = Modifier.height(12.dp))

        BenefitItem(
            icon = Icons.Default.Wifi,
            title = "Comodidad",
            description = "WiFi gratuito, aire acondicionado y más servicios"
        )
    }
}

@Composable
private fun BenefitItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Tertiary.copy(alpha = 0.2f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Tertiary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = GrayMedium
            )
        }
    }
}