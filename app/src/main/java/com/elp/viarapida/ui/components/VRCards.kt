package com.elp.viarapida.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elp.viarapida.data.model.Viaje
import com.elp.viarapida.data.model.Reserva
import com.elp.viarapida.ui.theme.*
import com.elp.viarapida.util.DateUtils

/**
 * Tarjeta de viaje para mostrar en resultados de búsqueda
 */
@Composable
fun ViajeCard(
    viaje: Viaje,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Empresa y tipo de servicio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = viaje.empresa,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )

                ServiceTypeChip(tipoServicio = viaje.tipoServicio)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ruta: Origen -> Destino
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = viaje.horaSalida,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = viaje.origen,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayMedium
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = viaje.horaLlegada,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = viaje.destino,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Duración
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = GrayMedium,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Duración: ${viaje.duracion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GrayMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Servicios disponibles
            if (viaje.servicios.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viaje.servicios.take(4).forEach { servicio ->
                        ServiceIcon(servicio = servicio)
                    }
                    if (viaje.servicios.size > 4) {
                        Text(
                            text = "+${viaje.servicios.size - 4}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Precio y asientos disponibles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "S/ ${String.format("%.2f", viaje.precio)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Text(
                        text = "por persona",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayMedium
                    )
                }

                AsientosChip(
                    disponibles = viaje.asientosDisponibles,
                    totales = viaje.asientosTotales
                )
            }
        }
    }
}

/**
 * Tarjeta de reserva
 */
@Composable
fun ReservaCard(
    reserva: Reserva,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Código y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Código: ${reserva.codigoReserva}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = reserva.viajeEmpresa,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayMedium
                    )
                }

                EstadoChip(estado = reserva.estado)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ruta
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reserva.viajeOrigen,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(horizontal = 8.dp)
                )
                Text(
                    text = reserva.viajeDestino,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fecha de viaje
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = GrayMedium,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = DateUtils.timestampToDateTimeString(reserva.viajeFechaSalida),
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pasajeros
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = GrayMedium,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${reserva.cantidadPasajeros} ${if (reserva.cantidadPasajeros == 1) "pasajero" else "pasajeros"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = GrayMedium
                )
                Text(
                    text = "S/ ${String.format("%.2f", reserva.precioTotal)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
        }
    }
}

/**
 * Chip de tipo de servicio
 */
@Composable
fun ServiceTypeChip(tipoServicio: String) {
    val (color, icon) = when (tipoServicio) {
        "VIP" -> Tertiary to Icons.Default.Star
        "Suite" -> Secondary to Icons.Default.Hotel
        else -> Info to Icons.Default.DirectionsBus
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = tipoServicio,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Chip de asientos disponibles
 */
@Composable
fun AsientosChip(disponibles: Int, totales: Int) {
    val color = when {
        disponibles < 5 -> Warning
        disponibles < 10 -> Info
        else -> Success
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.EventSeat,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$disponibles disponibles",
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Chip de estado de reserva
 */
@Composable
fun EstadoChip(estado: String) {
    val (color, text) = when (estado) {
        "confirmada" -> Success to "Confirmada"
        "pendiente" -> Warning to "Pendiente"
        "cancelada" -> Error to "Cancelada"
        "completada" -> Info to "Completada"
        else -> GrayMedium to "Desconocido"
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Icono de servicio
 */
@Composable
fun ServiceIcon(servicio: String) {
    val icon = when (servicio) {
        "WiFi" -> Icons.Default.Wifi
        "Baño" -> Icons.Default.Wc
        "TV" -> Icons.Default.Tv
        "Aire Acondicionado" -> Icons.Default.AcUnit
        else -> Icons.Default.CheckCircle
    }

    Icon(
        imageVector = icon,
        contentDescription = servicio,
        tint = Primary,
        modifier = Modifier.size(20.dp)
    )
}