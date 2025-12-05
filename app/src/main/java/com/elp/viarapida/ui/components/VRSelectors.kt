package com.elp.viarapida.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.elp.viarapida.ui.theme.*
import com.elp.viarapida.util.Constants
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Selector de ciudad (dropdown)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VRCitySelector(
    label: String,
    selectedCity: String,
    cities: List<String>,
    onCitySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCity,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Primary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                focusedLabelColor = Primary,
                disabledBorderColor = GrayLight,
                disabledTextColor = GrayMedium
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = city,
                            color = if (city == selectedCity) Primary else Color.Black
                        )
                    },
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Primary
                        )
                    }
                )
            }
        }
    }
}

/**
 * Selector de fecha (DatePicker)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VRDateSelector(
    label: String,
    selectedDate: Timestamp?,
    onDateSelected: (Timestamp) -> Unit,
    modifier: Modifier = Modifier,
    minDate: Timestamp = Timestamp.now(),
    maxDate: Timestamp = com.elp.viarapida.util.DateUtils.addDays(Timestamp.now(), Constants.DIAS_ANTICIPACION_COMPRA)
) {
    var showDialog by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val displayText = selectedDate?.let { dateFormat.format(it.toDate()) } ?: ""

    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        placeholder = { Text("Seleccione fecha") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = Primary
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            focusedLabelColor = Primary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
    )

    if (showDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.toDate()?.time ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                VRButton(
                    text = "Confirmar",
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Date(millis)
                            onDateSelected(Timestamp(date))
                        }
                        showDialog = false
                    }
                )
            },
            dismissButton = {
                VRTextButton(
                    text = "Cancelar",
                    onClick = { showDialog = false }
                )
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Primary,
                    todayDateBorderColor = Primary
                )
            )
        }
    }
}

/**
 * Selector de método de pago
 */
@Composable
fun VRPaymentMethodSelector(
    selectedMethod: String,
    onMethodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val paymentMethods = listOf(
        Constants.PAGO_EFECTIVO to Icons.Default.Money,
        Constants.PAGO_TARJETA to Icons.Default.CreditCard,
        Constants.PAGO_YAPE to Icons.Default.PhoneAndroid,
        Constants.PAGO_PLIN to Icons.Default.Smartphone
    )

    Column(modifier = modifier) {
        Text(
            text = "Método de pago",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        paymentMethods.forEach { (method, icon) ->
            PaymentMethodItem(
                method = method,
                icon = icon,
                isSelected = selectedMethod == method,
                onSelect = { onMethodSelected(method) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PaymentMethodItem(
    method: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Primary.copy(alpha = 0.1f) else Color.White,
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) Primary else GrayLight
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Primary else GrayMedium,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = method,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Primary else Color.Black,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Primary
                )
            }
        }
    }
}

/**
 * Selector de asientos (grid)
 */
@Composable
fun VRSeatSelector(
    totalSeats: Int,
    occupiedSeats: List<String>,
    selectedSeats: List<String>,
    onSeatSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxSeats: Int = Constants.MAX_PASAJEROS_POR_RESERVA
) {
    Column(modifier = modifier) {
        // Leyenda
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SeatLegendItem(color = Success, label = "Disponible")
            SeatLegendItem(color = Primary, label = "Seleccionado")
            SeatLegendItem(color = GrayMedium, label = "Ocupado")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid de asientos
        val rows = (totalSeats / Constants.ASIENTOS_POR_FILA)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 1..Constants.ASIENTOS_POR_FILA) {
                        val seatNumber = "${row + 1}${('A' + col - 1)}"
                        val isOccupied = occupiedSeats.contains(seatNumber)
                        val isSelected = selectedSeats.contains(seatNumber)
                        val canSelect = !isOccupied && (isSelected || selectedSeats.size < maxSeats)

                        SeatItem(
                            seatNumber = seatNumber,
                            isOccupied = isOccupied,
                            isSelected = isSelected,
                            canSelect = canSelect,
                            onClick = { if (canSelect) onSeatSelected(seatNumber) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeatItem(
    seatNumber: String,
    isOccupied: Boolean,
    isSelected: Boolean,
    canSelect: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isOccupied -> GrayMedium
        isSelected -> Primary
        else -> Success
    }

    Surface(
        modifier = Modifier
            .size(50.dp)
            .clickable(enabled = canSelect, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor.copy(alpha = if (canSelect) 1f else 0.5f)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EventSeat,
                contentDescription = seatNumber,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun SeatLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            modifier = Modifier.size(16.dp),
            shape = RoundedCornerShape(4.dp),
            color = color
        ) {}
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = GrayMedium
        )
    }
}

/**
 * Contador de pasajeros
 */
@Composable
fun VRPassengerCounter(
    count: Int,
    onCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxCount: Int = Constants.MAX_PASAJEROS_POR_RESERVA
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Número de pasajeros",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Máximo $maxCount por reserva",
                style = MaterialTheme.typography.bodySmall,
                color = GrayMedium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(
                onClick = { if (count > 1) onCountChange(count - 1) },
                enabled = count > 1
            ) {
                Icon(
                    imageVector = Icons.Default.RemoveCircle,
                    contentDescription = "Disminuir",
                    tint = if (count > 1) Primary else GrayLight,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            IconButton(
                onClick = { if (count < maxCount) onCountChange(count + 1) },
                enabled = count < maxCount
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Aumentar",
                    tint = if (count < maxCount) Primary else GrayLight,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}