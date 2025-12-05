package com.elp.viarapida.ui.screens.viajes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.elp.viarapida.util.DateUtils
import com.elp.viarapida.viewmodel.ViajeViewModel

/**
 * Pantalla de detalle de viaje
 */
@Composable
fun DetalleViajeScreen(
    viajeId: String,
    onNavigateBack: () -> Unit,
    onNavigateToSeleccionarAsientos: (String) -> Unit,
    viewModel: ViajeViewModel = viewModel()
) {
    val viajeSeleccionado by viewModel.viajeSeleccionado.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Cargar viaje al iniciar
    LaunchedEffect(viajeId) {
        viewModel.getViajePorId(viajeId)
    }

    Scaffold(
        topBar = {
            VRTopBar(
                title = "Detalle del Viaje",
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
                    onRetry = { viewModel.getViajePorId(viajeId) },
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
                        // Header con empresa y tipo de servicio
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Primary.copy(alpha = 0.1f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = viaje.empresa,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Primary
                                    )
                                    ServiceTypeChip(tipoServicio = viaje.tipoServicio)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Información de ruta
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "RUTA",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = viaje.horaSalida,
                                            style = MaterialTheme.typography.displaySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = viaje.origen,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = GrayMedium
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .align(Alignment.CenterVertically)
                                    )

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = viaje.horaLlegada,
                                            style = MaterialTheme.typography.displaySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = viaje.destino,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = GrayMedium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Divider()

                                Spacer(modifier = Modifier.height(16.dp))

                                // Duración
                                DetailInfoRow(
                                    icon = Icons.Default.Schedule,
                                    label = "Duración",
                                    value = viaje.duracion
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Fecha
                                DetailInfoRow(
                                    icon = Icons.Default.CalendarToday,
                                    label = "Fecha de salida",
                                    value = DateUtils.timestampToDateString(viaje.fechaSalida)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Servicios incluidos
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "SERVICIOS INCLUIDOS",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                viaje.servicios.chunked(2).forEach { rowServicios ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        rowServicios.forEach { servicio ->
                                            ServiceItem(
                                                servicio = servicio,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        // Relleno si hay un número impar
                                        if (rowServicios.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Disponibilidad
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "DISPONIBILIDAD",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${viaje.asientosDisponibles} asientos",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "de ${viaje.asientosTotales} totales",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = GrayMedium
                                        )
                                    }

                                    CircularProgressIndicator(
                                        progress = viaje.porcentajeOcupacion() / 100f,
                                        modifier = Modifier.size(60.dp),
                                        color = when {
                                            viaje.asientosDisponibles < 5 -> Error
                                            viaje.asientosDisponibles < 10 -> Warning
                                            else -> Success
                                        },
                                        strokeWidth = 6.dp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp))
                    }

                    // Footer con precio y botón
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shadowElevation = 8.dp
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
                                    text = "Precio por persona",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = GrayMedium
                                )
                                Text(
                                    text = "S/ ${String.format("%.2f", viaje.precio)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }

                            VRButton(
                                text = "Seleccionar Asientos",
                                onClick = { onNavigateToSeleccionarAsientos(viaje.id) },
                                enabled = viaje.asientosDisponibles > 0
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = GrayMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ServiceItem(
    servicio: String,
    modifier: Modifier = Modifier
) {
    val icon = when (servicio) {
        "WiFi" -> Icons.Default.Wifi
        "Baño" -> Icons.Default.Wc
        "TV" -> Icons.Default.Tv
        "Aire Acondicionado" -> Icons.Default.AcUnit
        else -> Icons.Default.CheckCircle
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Success.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Success,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = servicio,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}