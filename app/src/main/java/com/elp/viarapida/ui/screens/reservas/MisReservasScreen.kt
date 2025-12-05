package com.elp.viarapida.ui.screens.reservas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elp.viarapida.ui.components.*
import com.elp.viarapida.ui.theme.Primary
import com.elp.viarapida.viewmodel.ReservaState
import com.elp.viarapida.viewmodel.ReservaViewModel

/**
 * Pantalla de Mis Reservas
 */
@Composable
fun MisReservasScreen(
    onNavigateToDetalle: (String) -> Unit,
    onNavigateToBuscarReserva: () -> Unit,
    viewModel: ReservaViewModel = viewModel()
) {
    val reservaState by viewModel.reservaState.collectAsState()
    val reservas by viewModel.reservas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showCancelarDialog by remember { mutableStateOf(false) }
    var reservaACancelar by remember { mutableStateOf<String?>(null) }

    // Cargar reservas al iniciar
    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            0 -> viewModel.getReservasActivas()
            1 -> viewModel.getHistorialReservas()
        }
    }

    // Observar estado de cancelación
    LaunchedEffect(reservaState) {
        when (reservaState) {
            is ReservaState.ReservaCancelada -> {
                // Recargar reservas después de cancelar
                viewModel.getReservasActivas()
                viewModel.resetReservaState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            VRTopBar(
                title = "Mis Reservas",
                actions = {
                    IconButton(onClick = onNavigateToBuscarReserva) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Buscar reserva"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            VRTabRow(
                selectedTabIndex = selectedTab,
                tabs = listOf("Activas", "Historial"),
                onTabSelected = { selectedTab = it }
            )

            // Contenido según el estado
            when {
                isLoading -> {
                    VRLoadingIndicator(
                        modifier = Modifier.fillMaxSize(),
                        message = "Cargando reservas..."
                    )
                }
                reservas.isEmpty() -> {
                    VREmptyState(
                        title = if (selectedTab == 0) "No tienes reservas activas" else "Sin historial",
                        message = if (selectedTab == 0)
                            "Tus próximos viajes aparecerán aquí"
                        else
                            "Tu historial de viajes aparecerá aquí",
                        modifier = Modifier.fillMaxSize(),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ConfirmationNumber,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "${reservas.size} ${if (reservas.size == 1) "reserva" else "reservas"}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(reservas) { reserva ->
                            ReservaCard(
                                reserva = reserva,
                                onClick = { onNavigateToDetalle(reserva.id) }
                            )

                            // Botón de cancelar solo para reservas activas
                            if (selectedTab == 0 && reserva.estaActiva()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    VRButton(
                                        text = "Cancelar reserva",
                                        onClick = {
                                            reservaACancelar = reserva.id
                                            showCancelarDialog = true
                                        },
                                        variant = ButtonVariant.Outline
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación de cancelación
    if (showCancelarDialog && reservaACancelar != null) {
        VRConfirmDialog(
            title = "Cancelar Reserva",
            message = "¿Estás seguro de que deseas cancelar esta reserva? Esta acción no se puede deshacer.",
            confirmText = "Sí, cancelar",
            cancelText = "No",
            onConfirm = {
                viewModel.cancelarReserva(reservaACancelar!!)
                showCancelarDialog = false
                reservaACancelar = null
            },
            onDismiss = {
                showCancelarDialog = false
                reservaACancelar = null
            },
            isDestructive = true
        )
    }
}