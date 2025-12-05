package com.elp.viarapida.ui.screens.auth

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elp.viarapida.ui.components.*
import com.elp.viarapida.ui.theme.Primary
import com.elp.viarapida.viewmodel.AuthState
import com.elp.viarapida.viewmodel.AuthViewModel

/**
 * Pantalla de registro de nuevos usuarios
 */
@Composable
fun RegistroScreen(
    onNavigateBack: () -> Unit,
    onRegistroSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var aceptaTerminos by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Observar cambios en el estado de autenticación
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Authenticated -> {
                onRegistroSuccess()
            }
            is AuthState.Error -> {
                errorMessage = state.message
                showError = true
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            VRTopBar(
                title = "Crear Cuenta",
                onNavigationClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono y subtítulo
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Completa tus datos para registrarte",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

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

            // Campos de datos personales
            Text(
                text = "DATOS PERSONALES",
                style = MaterialTheme.typography.labelLarge,
                color = Primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre
            VRTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombre",
                placeholder = "Ingrese su nombre",
                leadingIcon = Icons.Default.Person,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Apellido
            VRTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = "Apellido",
                placeholder = "Ingrese su apellido",
                leadingIcon = Icons.Default.Person,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Teléfono
            VRTextField(
                value = telefono,
                onValueChange = {
                    if (it.length <= 9 && it.all { char -> char.isDigit() }) {
                        telefono = it
                    }
                },
                label = "Teléfono",
                placeholder = "999999999",
                leadingIcon = Icons.Default.Phone,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campos de cuenta
            Text(
                text = "DATOS DE CUENTA",
                style = MaterialTheme.typography.labelLarge,
                color = Primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            VRTextField(
                value = email,
                onValueChange = { email = it },
                label = "Correo electrónico",
                placeholder = "ejemplo@correo.com",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contraseña
            VRTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                placeholder = "Mínimo 6 caracteres",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirmar contraseña
            VRTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirmar contraseña",
                placeholder = "Repita su contraseña",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                enabled = !isLoading,
                isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                errorMessage = if (confirmPassword.isNotEmpty() && password != confirmPassword)
                    "Las contraseñas no coinciden" else ""
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Checkbox términos y condiciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = aceptaTerminos,
                    onCheckedChange = { aceptaTerminos = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Primary
                    ),
                    enabled = !isLoading
                )
                Text(
                    text = "Acepto los Términos de Servicio y la Política de Privacidad",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de registro
            VRButton(
                text = "Crear Cuenta",
                onClick = {
                    showError = false
                    viewModel.registrar(
                        nombre = nombre,
                        apellido = apellido,
                        email = email,
                        telefono = telefono,
                        password = password,
                        confirmPassword = confirmPassword
                    )
                },
                fullWidth = true,
                isLoading = isLoading,
                enabled = nombre.isNotBlank() &&
                        apellido.isNotBlank() &&
                        email.isNotBlank() &&
                        telefono.length == 9 &&
                        password.isNotBlank() &&
                        password == confirmPassword &&
                        aceptaTerminos
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ya tengo cuenta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes una cuenta?",
                    style = MaterialTheme.typography.bodyMedium
                )
                VRTextButton(
                    text = "Inicia sesión",
                    onClick = onNavigateBack,
                    enabled = !isLoading
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}