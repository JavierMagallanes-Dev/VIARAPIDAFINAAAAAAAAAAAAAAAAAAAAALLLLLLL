package com.elp.viarapida.ui.screens.reserva

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elp.viarapida.data.model.Pasajero
import com.elp.viarapida.ui.components.*
import com.elp.viarapida.ui.theme.*
import com.elp.viarapida.viewmodel.ReservaViewModel
import com.elp.viarapida.viewmodel.ReservaState

/**
 * Pantalla de datos de pasajeros
 */
@Composable
fun DatosPasajerosScreen(
    asientosSeleccionados: List<String>,
    onNavigateBack: () -> Unit,
    onNavigateToConfirmar: () -> Unit,
    viewModel: ReservaViewModel = viewModel()
) {
    var pasajerosFormulario by remember {
        mutableStateOf(
            asientosSeleccionados.map { asiento ->
                PasajeroFormulario(asiento = asiento)
            }
        )
    }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val reservaState by viewModel.reservaState.collectAsState()

    // Observar estado
    LaunchedEffect(reservaState) {
        when (val state = reservaState) {
            is ReservaState.Error -> {
                errorMessage = state.message
                showError = true
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            VRTopBar(
                title = "Datos de Pasajeros",
                onNavigationClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Banner informativo
                item {
                    VRInfoBanner(
                        message = "Completa los datos de cada pasajero. Asegúrate de que los nombres coincidan con el documento de identidad.",
                        type = BannerType.Info
                    )
                }

                // Mensaje de error
                if (showError) {
                    item {
                        VRInfoBanner(
                            message = errorMessage,
                            type = BannerType.Error,
                            onDismiss = { showError = false }
                        )
                    }
                }

                // Formularios de pasajeros
                itemsIndexed(pasajerosFormulario) { index, pasajero ->
                    PasajeroCard(
                        numero = index + 1,
                        pasajero = pasajero,
                        onPasajeroChange = { nuevoPasajero ->
                            pasajerosFormulario = pasajerosFormulario.toMutableList().apply {
                                this[index] = nuevoPasajero
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // Footer con botón
            Surface(
                modifier = Modifier.fillMaxWidth(),
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
                        Text(
                            text = "${pasajerosFormulario.size} pasajero(s)",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Paso 2 de 3",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrayMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    VRButton(
                        text = "Continuar",
                        onClick = {
                            // Validar y guardar pasajeros
                            val todosCompletos = pasajerosFormulario.all { it.esValido() }

                            if (todosCompletos) {
                                showError = false
                                // Limpiar lista anterior
                                viewModel.limpiarPasajeros()
                                // Agregar cada pasajero
                                pasajerosFormulario.forEach { formulario ->
                                    viewModel.agregarPasajero(
                                        Pasajero(
                                            nombre = formulario.nombre,
                                            apellido = formulario.apellido,
                                            dni = formulario.dni,
                                            asiento = formulario.asiento
                                        )
                                    )
                                }
                                onNavigateToConfirmar()
                            } else {
                                errorMessage = "Por favor completa todos los campos correctamente"
                                showError = true
                            }
                        },
                        fullWidth = true
                    )
                }
            }
        }
    }
}

/**
 * Clase auxiliar para el formulario
 */
data class PasajeroFormulario(
    val nombre: String = "",
    val apellido: String = "",
    val dni: String = "",
    val asiento: String
) {
    fun esValido(): Boolean {
        return nombre.isNotBlank() &&
                apellido.isNotBlank() &&
                dni.length == 8 &&
                dni.all { it.isDigit() }
    }
}

@Composable
private fun PasajeroCard(
    numero: Int,
    pasajero: PasajeroFormulario,
    onPasajeroChange: (PasajeroFormulario) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pasajero $numero",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = Primary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventSeat,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Asiento ${pasajero.asiento}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre
            VRTextField(
                value = pasajero.nombre,
                onValueChange = { onPasajeroChange(pasajero.copy(nombre = it)) },
                label = "Nombre",
                placeholder = "Ingrese nombre",
                leadingIcon = Icons.Default.Person,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Apellido
            VRTextField(
                value = pasajero.apellido,
                onValueChange = { onPasajeroChange(pasajero.copy(apellido = it)) },
                label = "Apellido",
                placeholder = "Ingrese apellido",
                leadingIcon = Icons.Default.Person,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(12.dp))

            // DNI
            VRTextField(
                value = pasajero.dni,
                onValueChange = {
                    if (it.length <= 8 && it.all { char -> char.isDigit() }) {
                        onPasajeroChange(pasajero.copy(dni = it))
                    }
                },
                label = "DNI",
                placeholder = "8 dígitos",
                leadingIcon = Icons.Default.Badge,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                isError = pasajero.dni.isNotEmpty() && pasajero.dni.length != 8,
                errorMessage = if (pasajero.dni.isNotEmpty() && pasajero.dni.length != 8)
                    "El DNI debe tener 8 dígitos" else ""
            )
        }
    }
}