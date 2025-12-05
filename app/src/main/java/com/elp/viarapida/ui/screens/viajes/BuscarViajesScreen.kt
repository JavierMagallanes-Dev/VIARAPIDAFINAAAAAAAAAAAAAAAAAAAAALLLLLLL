package com.elp.viarapida.ui.screens.viajes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.elp.viarapida.util.Constants
import com.elp.viarapida.viewmodel.ViajeViewModel
import com.google.firebase.Timestamp

/**
 * Pantalla de búsqueda de viajes
 */
@Composable
fun BuscarViajesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResultados: () -> Unit,
    viewModel: ViajeViewModel = viewModel()
) {
    var origen by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }
    var fechaSeleccionada by remember { mutableStateOf<Timestamp?>(null) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            VRTopBar(
                title = "Buscar Viajes",
                onNavigationClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Header ilustrativo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Primary.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Encuentra tu viaje ideal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Completa los datos para buscar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Mensaje de error
            if (showError) {
                VRInfoBanner(
                    message = errorMessage,
                    type = BannerType.Error,
                    onDismiss = { showError = false }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Selector de origen
            VRCitySelector(
                label = "Origen",
                selectedCity = origen,
                cities = Constants.CIUDADES_ORIGEN,
                onCitySelected = { origen = it },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de intercambio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        val temp = origen
                        origen = destino
                        destino = temp
                    },
                    enabled = origen.isNotEmpty() && destino.isNotEmpty() && !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Intercambiar",
                        tint = Primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de destino
            VRCitySelector(
                label = "Destino",
                selectedCity = destino,
                cities = Constants.CIUDADES_DESTINO,
                onCitySelected = { destino = it },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Selector de fecha
            VRDateSelector(
                label = "Fecha de viaje",
                selectedDate = fechaSeleccionada,
                onDateSelected = { fechaSeleccionada = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Consejo",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Text(
                            text = "Reserva con anticipación para obtener mejores precios y disponibilidad",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de búsqueda
            VRButton(
                text = "Buscar Viajes",
                onClick = {
                    // Validar campos
                    when {
                        origen.isEmpty() -> {
                            errorMessage = "Seleccione un origen"
                            showError = true
                        }
                        destino.isEmpty() -> {
                            errorMessage = "Seleccione un destino"
                            showError = true
                        }
                        origen == destino -> {
                            errorMessage = "El origen y destino deben ser diferentes"
                            showError = true
                        }
                        fechaSeleccionada == null -> {
                            errorMessage = "Seleccione una fecha"
                            showError = true
                        }
                        else -> {
                            showError = false
                            viewModel.buscarViajes(origen, destino, fechaSeleccionada!!)
                            onNavigateToResultados()
                        }
                    }
                },
                fullWidth = true,
                isLoading = isLoading,
                enabled = origen.isNotEmpty() &&
                        destino.isNotEmpty() &&
                        origen != destino &&
                        fechaSeleccionada != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Rutas sugeridas
            RutasSugeridasSection(
                onRutaSelected = { origenSugerido, destinoSugerido ->
                    origen = origenSugerido
                    destino = destinoSugerido
                }
            )
        }
    }
}

@Composable
private fun RutasSugeridasSection(
    onRutaSelected: (String, String) -> Unit
) {
    Column {
        Text(
            text = "Rutas Populares",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        val rutasPopulares = listOf(
            "Ayacucho" to "Lima",
            "Lima" to "Ayacucho",
            "Ayacucho" to "Huancayo",
            "Ayacucho" to "Cusco"
        )

        rutasPopulares.forEach { (origen, destino) ->
            CustomSuggestionChip(
                onClick = { onRutaSelected(origen, destino) },
                origen = origen,
                destino = destino
            )
        }
    }
}

@Composable
private fun CustomSuggestionChip(
    onClick: () -> Unit,
    origen: String,
    destino: String
) {
    Surface(
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = Primary.copy(alpha = 0.1f),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Primary
            )
            Text(
                text = origen,
                style = MaterialTheme.typography.bodySmall,
                color = Primary
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Primary
            )
            Text(
                text = destino,
                style = MaterialTheme.typography.bodySmall,
                color = Primary
            )
        }
    }
}