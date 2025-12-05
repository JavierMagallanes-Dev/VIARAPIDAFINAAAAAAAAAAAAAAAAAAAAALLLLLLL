package com.elp.viarapida.ui.screens.viajes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elp.viarapida.ui.components.*
import com.elp.viarapida.ui.theme.Primary
import com.elp.viarapida.util.DateUtils
import com.elp.viarapida.viewmodel.ViajeViewModel
import com.elp.viarapida.viewmodel.ViajesState
import androidx.compose.ui.graphics.Color
/**
 * Pantalla de resultados de búsqueda de viajes
 */
@Composable
fun ResultadosViajesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetalle: (String) -> Unit,
    viewModel: ViajeViewModel = viewModel()
) {
    val viajesState by viewModel.viajesState.collectAsState()
    val viajes by viewModel.viajes.collectAsState()
    val origen by viewModel.origen.collectAsState()
    val destino by viewModel.destino.collectAsState()
    val fecha by viewModel.fecha.collectAsState()

    var showFiltros by remember { mutableStateOf(false) }
    var filtroSeleccionado by remember { mutableStateOf("Todos") }
    var ordenSeleccionado by remember { mutableStateOf("Precio") }

    Scaffold(
        topBar = {
            VRTopBar(
                title = "Resultados",
                onNavigationClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { showFiltros = !showFiltros }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtros"
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
            // Header con información de búsqueda
            BusquedaInfoHeader(
                origen = origen,
                destino = destino,
                fecha = fecha?.let { DateUtils.timestampToDateString(it) } ?: ""
            )

            // Filtros expandibles
            if (showFiltros) {
                FiltrosSection(
                    filtroSeleccionado = filtroSeleccionado,
                    onFiltroChange = { filtroSeleccionado = it },
                    ordenSeleccionado = ordenSeleccionado,
                    onOrdenChange = {
                        ordenSeleccionado = it
                        when (it) {
                            "Precio (Menor)" -> viewModel.ordenarPorPrecio(ascendente = true)
                            "Precio (Mayor)" -> viewModel.ordenarPorPrecio(ascendente = false)
                            "Hora de salida" -> viewModel.ordenarPorHoraSalida()
                        }
                    }
                )
            }

            // Lista de resultados
            when (val state = viajesState) {
                is ViajesState.Loading -> {
                    VRLoadingIndicator(
                        modifier = Modifier.fillMaxSize(),
                        message = "Buscando viajes disponibles..."
                    )
                }
                is ViajesState.Success -> {
                    val viajesFiltrados = when (filtroSeleccionado) {
                        "Económico" -> state.viajes.filter { it.tipoServicio == "Económico" }
                        "VIP" -> state.viajes.filter { it.tipoServicio == "VIP" }
                        "Suite" -> state.viajes.filter { it.tipoServicio == "Suite" }
                        else -> state.viajes
                    }

                    if (viajesFiltrados.isEmpty()) {
                        VREmptyState(
                            title = "Sin resultados",
                            message = "No se encontraron viajes con los filtros seleccionados",
                            modifier = Modifier.fillMaxSize(),
                            action = {
                                VRButton(
                                    text = "Limpiar filtros",
                                    onClick = { filtroSeleccionado = "Todos" }
                                )
                            }
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "${viajesFiltrados.size} ${if (viajesFiltrados.size == 1) "viaje encontrado" else "viajes encontrados"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            items(viajesFiltrados) { viaje ->
                                ViajeCard(
                                    viaje = viaje,
                                    onClick = { onNavigateToDetalle(viaje.id) }
                                )
                            }
                        }
                    }
                }
                is ViajesState.Empty -> {
                    VREmptyState(
                        title = "No hay viajes disponibles",
                        message = "No se encontraron viajes para la fecha seleccionada. Intenta con otra fecha.",
                        modifier = Modifier.fillMaxSize(),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.EventBusy,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp)
                            )
                        },
                        action = {
                            VRButton(
                                text = "Nueva búsqueda",
                                onClick = onNavigateBack
                            )
                        }
                    )
                }
                is ViajesState.Error -> {
                    VRErrorState(
                        message = state.message,
                        onRetry = { /* Reintentar búsqueda */ },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun BusquedaInfoHeader(
    origen: String,
    destino: String,
    fecha: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
            // Origen -> Destino
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Column {
                    Text(
                        text = origen,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Origen",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(horizontal = 12.dp)
                )

                Column {
                    Text(
                        text = destino,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Destino",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // Fecha
            Column(horizontalAlignment = Alignment.End) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = fecha,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun FiltrosSection(
    filtroSeleccionado: String,
    onFiltroChange: (String) -> Unit,
    ordenSeleccionado: String,
    onOrdenChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Filtros por tipo de servicio
        Text(
            text = "Tipo de servicio",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Todos", "Económico", "VIP", "Suite").forEach { filtro ->
                CustomFilterChip(
                    selected = filtroSeleccionado == filtro,
                    onClick = { onFiltroChange(filtro) },
                    label = filtro
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ordenar por
        Text(
            text = "Ordenar por",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Precio (Menor)", "Precio (Mayor)", "Hora de salida").forEach { orden ->
                CustomFilterChip(
                    selected = ordenSeleccionado == orden,
                    onClick = { onOrdenChange(orden) },
                    label = orden
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
    }
}

@Composable
private fun CustomFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) Primary else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (selected) Primary else Color.Gray.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) Color.White else Color.Black
            )
        }
    }
}