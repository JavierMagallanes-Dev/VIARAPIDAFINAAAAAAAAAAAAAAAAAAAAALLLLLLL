package com.elp.viarapida.ui.screens.auth

import androidx.compose.foundation.Image
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
 * Pantalla de inicio de sesión
 */
@Composable
fun LoginScreen(
    onNavigateToRegistro: () -> Unit,
    onNavigateToRecuperarPassword: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Observar cambios en el estado de autenticación
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Authenticated -> {
                onLoginSuccess()
            }
            is AuthState.Error -> {
                errorMessage = state.message
                showError = true
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo y título
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "VÍA RÁPIDA",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            Text(
                text = "Tu viaje comienza aquí",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Mensaje de error
            if (showError) {
                VRInfoBanner(
                    message = errorMessage,
                    type = BannerType.Error,
                    onDismiss = { showError = false }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Campo de email
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

            // Campo de contraseña
            VRTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                placeholder = "Ingrese su contraseña",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onImeAction = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        viewModel.login(email, password)
                    }
                },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Olvidé mi contraseña
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                VRTextButton(
                    text = "¿Olvidaste tu contraseña?",
                    onClick = onNavigateToRecuperarPassword,
                    enabled = !isLoading
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de iniciar sesión
            VRButton(
                text = "Iniciar Sesión",
                onClick = {
                    showError = false
                    viewModel.login(email, password)
                },
                fullWidth = true,
                isLoading = isLoading,
                enabled = email.isNotBlank() && password.isNotBlank()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f))
                Text(
                    text = "O",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
                Divider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de registro
            VRButton(
                text = "Crear una cuenta",
                onClick = onNavigateToRegistro,
                variant = ButtonVariant.Outline,
                fullWidth = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Información adicional
            Text(
                text = "Al iniciar sesión, aceptas nuestros\nTérminos de Servicio y Política de Privacidad",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}