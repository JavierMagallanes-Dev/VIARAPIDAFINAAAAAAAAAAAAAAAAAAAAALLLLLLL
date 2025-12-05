package com.elp.viarapida.ui.screens.reserva

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.elp.viarapida.viewmodel.ViajeViewModel
import com.elp.viarapida.viewmodel.ReservaViewModel

/**
 * Pantalla de selección de asientos
 */
@Composable
fun SeleccionarAsientosScreen(
    viajeId: String,
    onNavigateBack: () -> Unit,
    onNavigateToDatosPasajeros: () -> Unit,
    viajeViewModel: ViajeViewModel = viewModel(),
    reservaViewModel: ReservaViewModel = viewModel()
) {
    val viajeSeleccionado by viajeViewModel.viajeSeleccionado.collectAsState()
    val asientosOcupados by reservaViewModel.asientosOcupados.collectAsState()
    val isLoading by viajeViewModel.isLoading.collectAsState()

    var asientosSeleccionados by remember { mutableStateOf<List<String>>(emptyList()) }
    var cantidadPasajeros by remember { mutableStateOf(1) }

    // Cargar viaje y asientos ocupados
    LaunchedEffect(viajeId) {
        viajeViewModel.getViajePorId(viajeId)
        reservaViewModel.getAsientosOcupados(viajeId)
    }

    Scaffold(
        topBar = {
            VRTopBar(
                title = "Seleccionar Asientos",
                onNavigationClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                VRLoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            viajeSeleccionado == null -> {
                VRErrorState(
                    message = "No se pudo cargar el viaje",
                    onRetry = { viajeViewModel.getViajePorId(viajeId) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                val viaje = viajeSeleccionado!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Contenido scrolleable
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Info del viaje
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Primary.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${viaje.origen} → ${viaje.destino}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${viaje.empresa} - ${viaje.tipoServicio}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = GrayMedium
                                    )
                                }
                                Text(
                                    text = viaje.horaSalida,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Contador de pasajeros
                        VRPassengerCounter(
                            count = cantidadPasajeros,
                            onCountChange = {
                                cantidadPasajeros = it
                                // Limpiar selección si excede
                                if (asientosSeleccionados.size > it) {
                                    asientosSeleccionados = asientosSeleccionados.take(it)
                                }
                            },
                            maxCount = minOf(
                                Constants.MAX_PASAJEROS_POR_RESERVA,
                                viaje.asientosDisponibles
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Información de selección
                        if (asientosSeleccionados.isNotEmpty()) {
                            VRInfoBanner(
                                message = "Asientos seleccionados: ${asientosSeleccionados.joinToString(", ")}",
                                type = BannerType.Success
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (asientosSeleccionados.size < cantidadPasajeros) {
                            VRInfoBanner(
                                message = "Selecciona ${cantidadPasajeros - asientosSeleccionados.size} asiento(s) más",
                                type = BannerType.Info
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Selector de asientos
                        VRSeatSelector(
                            totalSeats = viaje.asientosTotales,
                            occupiedSeats = asientosOcupados,
                            selectedSeats = asientosSeleccionados,
                            onSeatSelected = { asiento ->
                                asientosSeleccionados = if (asientosSeleccionados.contains(asiento)) {
                                    asientosSeleccionados - asiento
                                } else {
                                    if (asientosSeleccionados.size < cantidadPasajeros) {
                                        asientosSeleccionados + asiento
                                    } else {
                                        asientosSeleccionados
                                    }
                                }
                            },
                            maxSeats = cantidadPasajeros
                        )

                        Spacer(modifier = Modifier.height(80.dp))
                    }

                    // Footer con resumen y botón
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Total",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = GrayMedium
                                    )
                                    Text(
                                        text = "S/ ${String.format("%.2f", viaje.precio * cantidadPasajeros)}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Primary
                                    )
                                    Text(
                                        text = "${cantidadPasajeros} pasajero(s)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GrayMedium
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${asientosSeleccionados.size}/${cantidadPasajeros}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (asientosSeleccionados.size == cantidadPasajeros)
                                            Success else Warning
                                    )
                                    Text(
                                        text = "seleccionados",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GrayMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            VRButton(
                                text = "Continuar",
                                onClick = {
                                    // Limpiar pasajeros anteriores
                                    reservaViewModel.limpiarPasajeros()
                                    onNavigateToDatosPasajeros()
                                },
                                fullWidth = true,
                                enabled = asientosSeleccionados.size == cantidadPasajeros
                            )
                        }
                    }
                }
            }
        }
    }
}